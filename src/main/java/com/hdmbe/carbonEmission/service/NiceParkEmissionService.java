package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.component.EmissionCalculator;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;

import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;

import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.excelUpNiceS1.entity.NiceparkLog;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
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
public class NiceParkEmissionService {
    private final VehicleRepository vehicleRepository;
    private final CarbonEmissionFactorRepository factorRepository;
    private final EmissionDailyRepository emissionDailyRepository;

    private final EmissionCalculator calculator; // ✅ 분리한 계산기
    private final MonthlyLogService monthlyLogService; // ✅ 분리한 월별 서비스

    @Transactional
    public void process(List<NiceparkLog> logList) {
        if (logList.isEmpty()) return;

        List<CarbonEmissionDailyLog> dailyLogs = new ArrayList<>();
        Map<String, BigDecimal> monthlyTotalMap = new HashMap<>();
        Map<String, Vehicle> vehicleMap = new HashMap<>(); // Monthly 저장을 위해 Vehicle 객체 임시 저장

        for (NiceparkLog log : logList) {
            // 1. 차량 조회
            Vehicle vehicle = vehicleRepository.findByCarNumber(log.getCarNumber()).orElse(null);
            if (vehicle == null) continue;

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
        emissionDailyRepository.saveAll(dailyLogs); // Daily 저장
        monthlyLogService.saveMonthlyLogsBulk(monthlyTotalMap, vehicleMap); // Monthly 저장 위임!
    }
}
