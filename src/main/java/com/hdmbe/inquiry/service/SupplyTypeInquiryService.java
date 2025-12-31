package com.hdmbe.inquiry.service;

import com.hdmbe.inquiry.dto.SupplyTypeInquiryRequestDto;
import com.hdmbe.inquiry.dto.SupplyTypeInquiryResponseDto;
import com.hdmbe.inquiry.repository.SupplyTypeInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SupplyTypeInquiryService {

    private final SupplyTypeInquiryRepository supplyTypeInquiryRepository;

    public List<SupplyTypeInquiryResponseDto> getSupplyTypeEmission(
            SupplyTypeInquiryRequestDto dto
    ) {
        List<Object[]> rows =
                supplyTypeInquiryRepository.findSupplyTypeSummary(
                        dto.getYear(),
                        dto.getMonth()
                );

        if (rows.isEmpty()) {
            return Collections.emptyList();
        }
        BigDecimal grandTotal = rows.stream()
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Map<Integer, BigDecimal>> trendMap = new HashMap<>();

        if (dto.getYear() != null && dto.getMonth() == null) {

            List<Object[]> trendRows =
                    supplyTypeInquiryRepository.findSupplyTypeMonthlyTrend(
                            dto.getYear()
                    );

            for (Object[] r : trendRows) {
                String supplyTypeName = (String) r[0];
                Integer month = (Integer) r[1];
                BigDecimal emission = (BigDecimal) r[2];

                trendMap
                        .computeIfAbsent(supplyTypeName, k -> new HashMap<>())
                        .put(month, emission);
            }
        }

        List<SupplyTypeInquiryResponseDto> result = new ArrayList<>();
        BigDecimal ratioSum = BigDecimal.ZERO;
        SupplyTypeInquiryResponseDto maxRatioItem = null;

        for (Object[] r : rows) {

            String supplyTypeName = (String) r[0];
            BigDecimal totalEmission = (r[1] == null) ? BigDecimal.ZERO : (BigDecimal) r[1];

            BigDecimal totalDistance = (r[2] == null) ? BigDecimal.ZERO :
                    BigDecimal.valueOf(((Number) r[2]).doubleValue());

            Long tripCount = (r[3] == null) ? 0L : ((Number) r[3]).longValue();

            BigDecimal ratio =
                    grandTotal.signum() == 0
                    ? BigDecimal.ZERO
                            : totalEmission.multiply(BigDecimal.valueOf(100))
                            .divide(grandTotal, 2, RoundingMode.HALF_UP);

            BigDecimal avgEmission =
                    tripCount == 0
                    ? BigDecimal.ZERO
                            : totalEmission.divide(
                                    BigDecimal.valueOf(tripCount), 3, RoundingMode.HALF_UP
                    );

            List<BigDecimal> monthlyTrend = null;
            if (trendMap.containsKey(supplyTypeName)) {
                Map<Integer, BigDecimal> monthData = trendMap.get(supplyTypeName);
                monthlyTrend = IntStream.rangeClosed(1, 12)
                        .mapToObj(m -> monthData.getOrDefault(m, BigDecimal.ZERO))
                        .toList();
            }

            SupplyTypeInquiryResponseDto dtoItem =
                    SupplyTypeInquiryResponseDto.builder()
                            .supplyTypeName(supplyTypeName)
                            .totalEmission(totalEmission)
                            .ratio(ratio)
                            .totalDistance(totalDistance)
                            .tripCount(tripCount)
                            .avgEmission(avgEmission)
                            .monthlyTrend(monthlyTrend)
                            .build();

                    result.add(dtoItem);
                    ratioSum = ratioSum.add(ratio);

            if (maxRatioItem == null || (ratio != null && ratio.compareTo(maxRatioItem.getRatio() != null ? maxRatioItem.getRatio() : BigDecimal.ZERO) > 0)) {
                maxRatioItem = dtoItem;
            }
        }

      
        if (maxRatioItem != null) {
            BigDecimal difference = BigDecimal.valueOf(100).subtract(ratioSum);
            maxRatioItem.setRatio(
                    (maxRatioItem.getRatio() != null ? maxRatioItem.getRatio() : BigDecimal.ZERO).add(difference)
            );
        }

        return result;
    }
}
