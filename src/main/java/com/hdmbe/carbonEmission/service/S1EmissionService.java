package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.repository.CarbonEmissionJdbcRepository;
import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.carbonEmission.component.EmissionCalculator;
import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;
import com.hdmbe.excelUpNiceS1.entity.S1Log;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class S1EmissionService {
    private final VehicleRepository vehicleRepository;
    private final CarbonEmissionFactorRepository factorRepository;
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;
    private final MonthlyLogService monthlyLogService; // 월별 저장 서비스
    private final EmissionCalculator calculator; // 계산기 컴포넌트
    private final CarbonEmissionJdbcRepository carbonEmissionJdbcRepository;

    @Transactional
    // ✅ 파라미터 year, month 추가됨!
    public List<String> process(List<S1Log> logList, int year, int month) {
        if (logList.isEmpty()) return new ArrayList<>();

        List<String> excludedCars = new ArrayList<>(); // 미등록 차량 리스트

        // 1. [삭제 로직] 엑셀 날짜 안 믿음! 사용자가 선택한 "연/월" 기준으로 "S1"만 삭제
        LocalDate startDate;
        LocalDate endDate;

        if (month == 0) {
            // 연간 선택 시
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
            // 월별 집계 삭제 (S1만)
            emissionMonthlyRepository.deleteByYearAndSource(year, "S1");
        } else {
            // 월간 선택 시
            YearMonth ym = YearMonth.of(year, month);
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
            // 월별 집계 삭제 (S1만)
            emissionMonthlyRepository.deleteByYearMonthAndSource(year, month, "S1");
        }

        // 일별 데이터 삭제 (기간 + S1 꼬리표)
        emissionDailyRepository.deleteByDateAndSource(startDate, endDate, "S1");

        // 3. 계산 및 저장 로직 시작
        List<CarbonEmissionDailyLog> dailyLogs = new ArrayList<>();
        Map<String, BigDecimal> monthlyTotalMap = new HashMap<>();
        Map<String, Vehicle> vehicleMap = new HashMap<>();

        for (S1Log log : logList) {
            // 1. 차량 조회 (사번으로 찾기!)
            Vehicle vehicle = vehicleRepository.findByDriverMemberId(log.getMemberId())
                    .orElse(null);

            if (vehicle == null) continue; // 차량 없는 직원은 패스

            // 2. 계산 값 준비
            // (주의: 직원 통근은 보통 편도 거리가 저장되어 있다면 x2 왕복 처리가 필요할 수 있음. 확인 필요!)
            BigDecimal oneWay = vehicle.getOperationDistance();
            BigDecimal distance = oneWay.multiply(new BigDecimal("2")); // ✅ 왕복 계산!
            BigDecimal efficiency = vehicle.getCarModel().getCustomEfficiency();
            FuelType fuelType = vehicle.getCarModel().getFuelType();
            BigDecimal factor = factorRepository.findByFuelType(fuelType)
                    .orElseThrow(() -> new IllegalArgumentException("배출계수 데이터 누락: " + fuelType))
                    .getEmissionFactor();

            // 3. 계산기 호출
            BigDecimal emission = calculator.calculate(distance, efficiency, factor);

            // 4. Daily 생성
            LocalDate date = log.getAccessDate();
            dailyLogs.add(CarbonEmissionDailyLog.builder()
                    .vehicle(vehicle)
                    .operationDate(date)
                    .dailyEmission(emission)
                    .emissionSource("S1")
                    .build());

            // 5. Monthly 집계 (메모리)
            String key = date.getYear() + "_" + date.getMonthValue() + "_" + vehicle.getId();
            monthlyTotalMap.put(key, monthlyTotalMap.getOrDefault(key, BigDecimal.ZERO).add(emission));
            vehicleMap.put(key, vehicle);
        }

        // 6. 저장
       // emissionDailyRepository.saveAll(dailyLogs);
        carbonEmissionJdbcRepository.saveAllDailyBatch(dailyLogs);  // JDBC로 한 방에 저장

        monthlyLogService.saveMonthlyLogsBulk(monthlyTotalMap, vehicleMap, "S1");


        return excludedCars;
    }
}
