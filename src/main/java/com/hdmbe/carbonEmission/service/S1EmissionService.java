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
import java.util.stream.Collectors;

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
    // 파라미터 year, month 추가
    public List<String> process(List<S1Log> logList, int year, int month) {
        if (logList.isEmpty()) return new ArrayList<>();

        List<String> excludedCars = new ArrayList<>(); // 미등록 차량 리스트

        // 1. [삭제 로직] 엑셀 날짜 무시, 선택한 "연/월" 기준으로 "S1"만 삭제
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

        // 2. [최적화 & 준비] 모든 차량 이력 가져오기 (Map<사번, List<Vehicle>>)
        // 반복문 안에서 DB 조회를 없애기 위함
        Map<String, List<Vehicle>> vehicleHistoryMap = vehicleRepository.findAll().stream()
                .filter(v -> v.getDriverMemberId() != null)
                .collect(Collectors.groupingBy(Vehicle::getDriverMemberId));

        // [최적화 2] 배출계수(Factor) 미리 가져오기.  DB를 루프 밖에서 한 번만 찔러서 Map에 담아둡니다.
        Map<FuelType, BigDecimal> factorMap = factorRepository.findAll().stream()
                .collect(Collectors.toMap(
                        // 가정: Entity에 getFuelType()이 있다고 가정
                        f -> f.getFuelType(),
                        f -> f.getEmissionFactor()
                ));

        // 3. 계산 및 저장 로직 시작
        List<CarbonEmissionDailyLog> dailyLogs = new ArrayList<>();
        Map<String, BigDecimal> monthlyTotalMap = new HashMap<>();
        Map<String, Vehicle> vehicleMap = new HashMap<>();

        for (S1Log log : logList) {
            // [수정 1] DB 조회가 아니라, 미리 준비한 Map에서 이력 리스트를 꺼냄
            List<Vehicle> historyList = vehicleHistoryMap.get(log.getMemberId());

            // [수정 2] 로그 날짜에 맞는 "진짜 차" 찾기 (메서드 분리)
            Vehicle vehicle = findValidVehicle(historyList, log.getAccessDate());

            if (vehicle == null) continue; // 차량 없는 직원은 패스

            // 2. 계산 값 준비
            // (주의: 직원 통근은 보통 편도 거리가 저장되어 있다면 x2 왕복 처리가 필요할 수 있음. 확인 필요!)
            BigDecimal oneWay = vehicle.getOperationDistance();
            BigDecimal distance = oneWay.multiply(new BigDecimal("2")); // 왕복 계산
            BigDecimal efficiency = vehicle.getCarModel().getCustomEfficiency();
            FuelType fuelType = vehicle.getCarModel().getFuelType();
//            BigDecimal factor = factorRepository.findByFuelType(fuelType)
//                    .orElseThrow(() -> new IllegalArgumentException("배출계수 데이터 누락: " + fuelType))
//                    .getEmissionFactor();
            // [수정] DB 조회 대신 Map에서 꺼내기
            BigDecimal factor = factorMap.get(fuelType);

            if (factor == null) {
                // 혹시 모를 데이터 누락 대비
                throw new IllegalArgumentException("배출계수 데이터 누락: " + fuelType);
            }

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

    // =================================================================
    // [신규 추가] 날짜 비교 로직
    // =================================================================
    private Vehicle findValidVehicle(List<Vehicle> historyList, LocalDate logDate) {
        // 이력이 아예 없으면 null
        if (historyList == null || historyList.isEmpty()) {
            return null;
        }

        return historyList.stream()
                // 1. "차량 등록일(calcBaseDate)"이 "로그 날짜(logDate)"보다 이전(=과거)이거나 같은 차만 남김
                // (우리가 1900-01-01을 넣었으므로, 외부 차량이나 옛날 차는 무조건 통과됨!)
                .filter(v -> !v.getCalcBaseDate().isAfter(logDate))

                // 2. 그 중에서 "등록일이 가장 최신인 순서"로 정렬 (내림차순)
                // 예: 1월 등록(모닝), 7월 등록(아반떼) -> 8월 로그 계산 시 7월(아반떼)가 맨 위로 옴
                .sorted((v1, v2) -> v2.getCalcBaseDate().compareTo(v1.getCalcBaseDate()))

                // 3. 1등으로 뽑힌 차가 바로 "그 당시에 탔던 차"임!
                .findFirst()
                .orElse(null);
    }
}
