package com.hdmbe.inquiry.service;

import com.hdmbe.inquiry.dto.OperationPurposeInquiryRequestDto;
import com.hdmbe.inquiry.dto.OperationPurposeInquiryResponseDto;
import com.hdmbe.inquiry.repository.OperationPurposeInquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class OperationPurposeInquiryService {

    private final OperationPurposeInquiryRepository operationPurposeInquiryRepository;

    public List<OperationPurposeInquiryResponseDto> getPurposeEmission(
            OperationPurposeInquiryRequestDto dto
    ) {
        List<Object[]> rows = operationPurposeInquiryRepository.findPurposeSummary(
                dto.getYear(),
                dto.getMonth(),
                dto.getDefaultScope()

        );

        // 전체 배출량 계산 (비율용)
        BigDecimal grandTotal = rows.stream()
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 연간 조회면 그래프 데이터 조회
        Map<String, Map<Integer, BigDecimal>> trendMap = new HashMap<>();
        if (dto.getYear() != null && dto.getMonth() == null) {

            List<Object[]> trendRows =
                    operationPurposeInquiryRepository.findPurposeMonthlyTrend(dto.getYear(), dto.getDefaultScope());

            for (Object[] r : trendRows) {
                trendMap
                        .computeIfAbsent((String) r[0], k -> new HashMap<>())
                        .put((Integer) r[1], (BigDecimal) r[2]);
            }
        }

        List<OperationPurposeInquiryResponseDto> result = new ArrayList<>();
        BigDecimal ratioSum = BigDecimal.ZERO;
        OperationPurposeInquiryResponseDto maxRatioItem = null;

        for (Object[] r : rows) {

            String purpose = (String) r[0];
            BigDecimal totalEmission = (BigDecimal) r[1];
            Long tripCount = (Long) r[2];

            BigDecimal totalDistance = (r[3] == null) ? BigDecimal.ZERO :
                    BigDecimal.valueOf(((Number) r[3]).doubleValue());

            BigDecimal ratio =
                    grandTotal.signum() == 0
                            ? BigDecimal.ZERO
                            : totalEmission.multiply(BigDecimal.valueOf(100))
                            .divide(grandTotal, 2, RoundingMode.HALF_UP);

            BigDecimal avgEmission =
                    tripCount == 0
                            ? BigDecimal.ZERO
                            : totalEmission.divide(
                            BigDecimal.valueOf(tripCount),
                            3,
                            RoundingMode.HALF_UP
                    );

            List<BigDecimal> monthlyTrend = null;
            if (trendMap.containsKey(purpose)) {
                Map<Integer, BigDecimal> monthData = trendMap.get(purpose);
                monthlyTrend = IntStream.rangeClosed(1, 12)
                        .mapToObj(m -> monthData.getOrDefault(m, BigDecimal.ZERO))
                        .toList();
            }

            OperationPurposeInquiryResponseDto dtoItem =
                    OperationPurposeInquiryResponseDto.builder()
                            .purposeName(purpose)
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
