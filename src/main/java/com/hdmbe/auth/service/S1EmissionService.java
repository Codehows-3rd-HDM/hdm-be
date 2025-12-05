package com.hdmbe.service;

import com.hdmbe.component.EmissionCalculator;
import com.hdmbe.constant.FuelType;
import com.hdmbe.entity.CarbonEmissionDailyLog;
import com.hdmbe.entity.S1Log;
import com.hdmbe.entity.Vehicle;
import com.hdmbe.repository.CarbonEmissionFactorRepository;
import com.hdmbe.repository.EmissionDailyRepository;
import com.hdmbe.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    private final EmissionCalculator calculator; // 계산기 컴포넌트
    private final MonthlyLogService monthlyLogService; // 월별 저장 서비스

    @Transactional
    public void process(List<S1Log> logList) {
        if (logList.isEmpty()) return;

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
            // 여기서는 Company에 저장된 거리를 왕복 거리라고 가정하고 그대로 씁니다.
            BigDecimal oneWay = vehicle.getCompany().getOneWayDistance();
            BigDecimal distance = oneWay.multiply(new BigDecimal("2")); // ✅ 왕복 계산!
            BigDecimal efficiency = vehicle.getCarModel().getCustomEfficiency();
            FuelType fuelType = vehicle.getCarModel().getFuelType();
            BigDecimal factor = factorRepository.findByFuelType(fuelType).getEmissionFactor();

            // 3. 계산기 호출
            BigDecimal emission = calculator.calculate(distance, efficiency, factor);

            // 4. Daily 생성
            LocalDate date = log.getAccessTime().toLocalDate();
            dailyLogs.add(CarbonEmissionDailyLog.builder()
                    .vehicle(vehicle)
                    .operationDate(date)
                    .dailyEmission(emission)
                    .build());

            // 5. Monthly 집계 (메모리)
            String key = date.getYear() + "_" + date.getMonthValue() + "_" + vehicle.getId();
            monthlyTotalMap.put(key, monthlyTotalMap.getOrDefault(key, BigDecimal.ZERO).add(emission));
            vehicleMap.put(key, vehicle);
        }

        // 6. 저장
        emissionDailyRepository.saveAll(dailyLogs);
        monthlyLogService.saveMonthlyLogsBulk(monthlyTotalMap, vehicleMap);
    }
}
