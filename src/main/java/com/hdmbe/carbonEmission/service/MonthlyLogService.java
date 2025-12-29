package com.hdmbe.carbonEmission.service;


import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.carbonEmission.repository.CarbonEmissionJdbcRepository;
import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MonthlyLogService {
    //private final EmissionMonthlyRepository emissionMonthlyRepository;
    private final CarbonEmissionJdbcRepository carbonEmissionJdbcRepository;

    @Transactional
    public void saveMonthlyLogsBulk(Map<String, BigDecimal> monthlyTotalMap,
                                    Map<String, Vehicle> vehicleMap,
                                    String source) { // Vehicle 정보도 필요해서 맵으로 받음

        List<CarbonEmissionMonthlyLog> logsToSave = new ArrayList<>();

        for (String key : monthlyTotalMap.keySet()) {
            // Key 예시: "2025_10_차량ID" (파싱해서 씀)
            String[] parts = key.split("_");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            BigDecimal excelTotal = monthlyTotalMap.get(key);
            Vehicle vehicle = vehicleMap.get(key);

            // DB 조회 로직 삭제! (findByVehicleAndYearAndMonth 제거)
            // 그냥 "이만큼 더해줘"라고 담은 객체를 무조건 생성합니다.
            CarbonEmissionMonthlyLog log = CarbonEmissionMonthlyLog.builder()
                    .vehicle(vehicle)
                    .year(year)
                    .month(month)
                    .totalEmission(excelTotal) // 이번에 더할 값만 넣음
                    // .createdAt, .updatedAt은 JDBC 내부에서 처리하거나 여기서 넣어도 됨
                    .emissionSource(source)
                    .build();

            logsToSave.add(log);
        }
        carbonEmissionJdbcRepository.saveAllMonthlyBatch(logsToSave);
    }

            // DB 조회 (이미 있는지)
//            CarbonEmissionMonthlyLog finalLog = emissionMonthlyRepository
//                    .findByVehicleAndYearAndMonth(vehicle, year, month)
//                    .orElse(CarbonEmissionMonthlyLog.builder()
//                            .vehicle(vehicle)
//                            .year(year)
//                            .month(month)
//                            .totalEmission(BigDecimal.ZERO)
//                            .build());

            // 합산
//            BigDecimal newTotal = finalLog.getTotalEmission().add(excelTotal);
//            finalLog.setTotalEmission(newTotal);
//
//            logsToSave.add(finalLog);
//        }
//
//        emissionMonthlyRepository.saveAll(logsToSave);
//    }
}
