package com.hdmbe.inquiry.service;

import com.hdmbe.inquiry.dto.SupplyCustomerInquiryRequestDto;
import com.hdmbe.inquiry.dto.SupplyCustomerInquiryResponseDto;
import com.hdmbe.inquiry.repository.SupplyCustomerInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SupplyCustomerInquiryService {

    private final SupplyCustomerInquiryRepository supplyCustomerInquiryRepository;

    public List<SupplyCustomerInquiryResponseDto> getSupplyCustomerEmission(
            SupplyCustomerInquiryRequestDto dto
    ) {

        List<Object[]> rows =
                supplyCustomerInquiryRepository.findSupplyCustomerSummary(
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
                    supplyCustomerInquiryRepository.findSupplyCustomerMonthlyTrend(
                            dto.getYear()
                    );

            for (Object[] r : trendRows) {
                String customerName = (String) r[0];
                Integer month = (Integer) r[1];
                BigDecimal emission = (BigDecimal) r[2];

                trendMap
                        .computeIfAbsent(customerName, k -> new HashMap<>())
                        .put(month, emission);
            }
        }
        List<SupplyCustomerInquiryResponseDto> result = new ArrayList<>();
        BigDecimal ratioSum = BigDecimal.ZERO;
        SupplyCustomerInquiryResponseDto maxRatioItem = null;

        for (Object[] r : rows) {

            String customerName = (String) r[0];
            BigDecimal totalEmission = (BigDecimal) r[1];
            BigDecimal totalDistance = (BigDecimal) r[2];
            Long tripCount = (Long) r[3];

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
            if (trendMap.containsKey(customerName)) {
                Map<Integer, BigDecimal> monthData = trendMap.get(customerName);
                monthlyTrend = IntStream.rangeClosed(1, 12)
                        .mapToObj(m -> monthData.getOrDefault(m, BigDecimal.ZERO))
                        .toList();
            }

            SupplyCustomerInquiryResponseDto dtoItem =
                    SupplyCustomerInquiryResponseDto.builder()
                            .customerName(customerName)
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
