package com.hdmbe.fuelTypeAnalysis.service;

import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.fuelTypeAnalysis.dto.FuelTypeRequestDto;
import com.hdmbe.fuelTypeAnalysis.dto.FuelTypeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private final EmissionMonthlyRepository emissionMonthlyRepository;

    public List<FuelTypeResponseDto> getFuelEmission(
            FuelTypeRequestDto dto
    ) {

        // 파이차트
        List<Object[]> pieRows = emissionMonthlyRepository.findFuelEmissionForPie(
                dto.getYear(),
                dto.getMonth(),
                dto.getScope()
        );

        Map<FuelType, BigDecimal> fuelTotalMap = new HashMap<>();
        BigDecimal grandTotal = BigDecimal.ZERO;

        for (Object[] r : pieRows) {
            FuelType fuel = (FuelType) r[0];
            BigDecimal sum = (BigDecimal) r[1];
            fuelTotalMap.put(fuel, sum);
            grandTotal = grandTotal.add(sum);
        }

        // 그래프
        Map<FuelType, Map<Integer, BigDecimal>> fuelMonthMap = new HashMap<>();

        if (dto.getYear() != null && dto.getMonth() == null) {

            List<Object[]> trendRows =
                    emissionMonthlyRepository.findYearlyMonthlyTrend(
                            dto.getYear(),
                            dto.getScope()
                    );

            for (Object[] r : trendRows) {
                FuelType fuel = (FuelType) r[0];
                Integer month = (Integer) r[1];
                BigDecimal sum = (BigDecimal) r[2];

                fuelMonthMap
                        .computeIfAbsent(fuel, k -> new HashMap<>())
                        .put(month, sum);
            }
        }
        List<FuelTypeResponseDto> result = new ArrayList<>();
        BigDecimal sumRatios = BigDecimal.ZERO;

        FuelTypeResponseDto maxRatioItem = null;

        for (Map.Entry<FuelType, BigDecimal> entry : fuelTotalMap.entrySet()) {

            FuelType fuel = entry.getKey();
            BigDecimal total = entry.getValue();

            BigDecimal ratio =
                    grandTotal.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : total.multiply(BigDecimal.valueOf(100))
                            .divide(grandTotal, 2, RoundingMode.HALF_UP);

            // 가장 큰 비율 항목 찾기
            if (maxRatioItem == null || ratio.compareTo(maxRatioItem.getRatio()) > 0) {
                maxRatioItem = FuelTypeResponseDto.builder()
                        .fuelType(fuel)
                        .totalEmission(total)
                        .ratio(ratio)
                        .monthlyTrend(null) // 나중에 채움
                        .build();
            }

            sumRatios = sumRatios.add(ratio);

            List<BigDecimal> monthlyTrend = null;
            if (fuelMonthMap.containsKey(fuel)) {
                Map<Integer, BigDecimal> monthData = fuelMonthMap.get(fuel);
                monthlyTrend = IntStream.rangeClosed(1, 12)
                        .mapToObj(m -> monthData.getOrDefault(m, BigDecimal.ZERO))
                        .toList();
            }

            result.add(
                    FuelTypeResponseDto.builder()
                            .fuelType(fuel)
                            .totalEmission(total)
                            .ratio(ratio)
                            .monthlyTrend(monthlyTrend)
                            .build()
            );
        }

        // 4. 합계 보정 (가장 큰 비율 항목에 차이 보정)
        BigDecimal difference = BigDecimal.valueOf(100).subtract(sumRatios);
        if (difference.compareTo(BigDecimal.ZERO) != 0 && maxRatioItem != null) {
            for (FuelTypeResponseDto dtoItem : result) {
                if (dtoItem.getFuelType() == maxRatioItem.getFuelType()) {
                    dtoItem.setRatio(dtoItem.getRatio().add(difference));
                    break;
                }
            }
        }

        return result;
    }
}