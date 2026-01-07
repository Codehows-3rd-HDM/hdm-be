package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.component.EmissionCalculator;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;

import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.carbonEmission.repository.CarbonEmissionJdbcRepository;
import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;

import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.excelUpNiceS1.entity.NiceparkLog;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NiceParkEmissionService {
    private final VehicleRepository vehicleRepository;
    private final CarbonEmissionFactorRepository factorRepository;
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;
    private final EmissionCalculator calculator; // ✅ 분리한 계산기
    private final MonthlyLogService monthlyLogService; // ✅ 분리한 월별 서비스
    private final CarbonEmissionJdbcRepository carbonEmissionJdbcRepository;

    @Transactional
    // year, month 파라미터 추가
    public List<String> process(List<NiceparkLog> logList, int year, int month) {

        // 1. 데이터 없으면 빈 리스트 반환
        if (logList.isEmpty()) return new ArrayList<>();

        List<String> excludedCars = new ArrayList<>(); // 결과 리턴용

        // 2. [수정] 삭제 범위 설정 (엑셀 날짜 안 믿음. 선택한 연/월 기준!)
        LocalDate startDate;
        LocalDate endDate;

        if (month == 0) {
            // "연간" 선택 시 -> 1월 1일 ~ 12월 31일
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);
        } else {
            // "월간" 선택 시 -> 해당 월 1일 ~ 말일
            YearMonth ym = YearMonth.of(year, month);
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
        }

        // 3. 삭제 실행
        // "이 기간의 NICE 데이터는 싹 지워라"
        emissionDailyRepository.deleteByDateAndSource(startDate, endDate, "NICE");

        // (월별 집계 데이터도 삭제 - 필요 시 유지)
        if (month == 0) {
            emissionMonthlyRepository.deleteByYearAndSource(year, "NICE");
        } else {
            emissionMonthlyRepository.deleteByYearMonthAndSource(year, month, "NICE");
        }

        // 2. [최적화 & 준비] 차량 조회 (이번엔 차량번호가 Key!)
        // Map<차량번호, List<Vehicle>> 형태로 그룹핑
        // (주의: 같은 차량번호라도 날짜별로 이력이 여러 개일 수 있으니 List임)
        Map<String, List<Vehicle>> vehicleHistoryMap = vehicleRepository.findAll().stream()
                .filter(v -> v.getCarNumber() != null) // 혹시 모를 null 방지
                .collect(Collectors.groupingBy(Vehicle::getCarNumber));

        // 3. 계산 및 저장 로직
        List<CarbonEmissionDailyLog> dailyLogs = new ArrayList<>();
        Map<String, BigDecimal> monthlyTotalMap = new HashMap<>();
        Map<String, Vehicle> vehicleMap = new HashMap<>(); // Monthly 저장을 위해 Vehicle 객체 임시 저장

        for (NiceparkLog niceparkLog : logList) {
            // 로그 날짜 먼저 확보 (차 찾을 때 필요함)
            LocalDate logDate = niceparkLog.getAccessTime().toLocalDate();

            // [수정 1] Map에서 이력 꺼내기
            List<Vehicle> historyList = vehicleHistoryMap.get(niceparkLog.getCarNumber());

            // [수정 2] 날짜 비교 로직으로 "진짜 차" 찾기
            Vehicle vehicle = findValidVehicle(historyList, logDate);

            String memberId = vehicle != null ? vehicle.getDriverMemberId() : null;


            // [나이스 DB에 임직원 차량있을 경우]
            // 차량이 DB에 없거나, '사번'이 있는 임직원 차량이면 나이스파크 집계에서 제외
            if (vehicle == null || (memberId != null && !memberId.trim().isEmpty())) {
                if (vehicle != null) {
                    excludedCars.add(niceparkLog.getCarNumber()); // 제외 명단에 추가!
                    log.warn("임직원 차량({})이 나이스파크 집계에서 제외되었습니다.", niceparkLog.getCarNumber(), memberId);
                }
                //else {
                    // DB에 없는 외부차량 (로그 너무 많이 찍힐 수 있으니 필요하면 주석 처리)
                    // log.debug("미등록 외부 차량({}) : 집계 제외됨", niceparkLog.getCarNumber());
              //  }
                continue;
            }

            // 2. 계산에 필요한 값 준비
            BigDecimal oneWay = vehicle.getOperationDistance();
            BigDecimal distance = oneWay.multiply(new BigDecimal("2")); // ✅ 왕복 계산!
            BigDecimal efficiency = vehicle.getCarModel().getCustomEfficiency(); // 연비
            FuelType fuelType = vehicle.getCarModel().getFuelType();
            BigDecimal factor = factorRepository.findByFuelType(fuelType)
                    .orElseThrow(() -> new IllegalArgumentException("배출계수 데이터 누락: " + fuelType))
                    .getEmissionFactor(); // (캐싱 권장)

            // 3. 계산기 호출!
            BigDecimal emission = calculator.calculate(distance, efficiency, factor);

            // 4. Daily 생성
            LocalDate date = niceparkLog.getAccessTime().toLocalDate();
            dailyLogs.add(CarbonEmissionDailyLog.builder()
                    .vehicle(vehicle)
                    .operationDate(date)
                    .dailyEmission(emission)
                    .emissionSource("NICE") // 꼬리표
                    .build());

            // 5. Monthly 집계 (메모리)
            String key = date.getYear() + "_" + date.getMonthValue() + "_" + vehicle.getId();
            monthlyTotalMap.put(key, monthlyTotalMap.getOrDefault(key, BigDecimal.ZERO).add(emission));
            vehicleMap.put(key, vehicle);
        }

        // 6. 저장
     //   emissionDailyRepository.saveAll(dailyLogs);   // Daily 저장
        carbonEmissionJdbcRepository.saveAllDailyBatch(dailyLogs);  // JDBC로 한 방에 저장

        // 7. Monthly 저장 위임
        monthlyLogService.saveMonthlyLogsBulk(monthlyTotalMap, vehicleMap, "NICE");

        return excludedCars;
    }

    // =================================================================
    // ✅ [재사용] S1EmissionService랑 똑같은 로직입니다. (복붙 가능)
    // =================================================================
    private Vehicle findValidVehicle(List<Vehicle> historyList, LocalDate logDate) {
        if (historyList == null || historyList.isEmpty()) {
            return null;
        }
        return historyList.stream()
                .filter(v -> !v.getCalcBaseDate().isAfter(logDate)) // 미래 차 제외
                .sorted((v1, v2) -> v2.getCalcBaseDate().compareTo(v1.getCalcBaseDate())) // 최신순 정렬
                .findFirst()
                .orElse(null);
    }
}
