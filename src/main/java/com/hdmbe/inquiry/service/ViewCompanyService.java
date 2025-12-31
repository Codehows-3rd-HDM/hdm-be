package com.hdmbe.inquiry.service;


import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.inquiry.dto.ViewCompanyRequestDto;
import com.hdmbe.inquiry.dto.ViewCompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewCompanyService {

    private final EmissionMonthlyRepository emissionMonthlyRepository;

    @Transactional(readOnly = true)
    public List<ViewCompanyResponseDto> findEmission(ViewCompanyRequestDto req) {

        // 1. 연도 처리 (없으면 DB 최신 연도)
        if (req.getYear() == null) {
            Integer latestYear = emissionMonthlyRepository.findLatestYear();
            // 데이터가 없으면(null) 시스템의 현재 연도를 가져옴
            if (latestYear == null) {
                latestYear = java.time.LocalDate.now().getYear();
            }
            req.setYear(latestYear);
        }

        // 2. 월 처리 (0 -> 전체)
        Integer targetMonth = (req.getMonth() != null && req.getMonth() == 0) ? null : req.getMonth();

        // 추가. 기준 날짜 계산 (핵심)
        YearMonth ym =
                (targetMonth != null)
                        ? YearMonth.of(req.getYear(), targetMonth)
                        : YearMonth.of(req.getYear(), 12);

        LocalDate targetDate = ym.atEndOfMonth();

        // 3. 키워드 처리
        String targetKeyword = (req.getKeyword() != null && !req.getKeyword().trim().isEmpty())
                ? req.getKeyword()
                : null;

        // 4. DB 조회 (Scope 파라미터 제거됨)
        List<ViewCompanyResponseDto> stats = emissionMonthlyRepository.findEmissionByCompany(
                req.getYear(),
                targetMonth,
                targetDate,
                targetKeyword
        );

        // 5. 비율 계산 및 정렬 (비공개 메서드 활용)
        calculateRatioAndSort(stats);

        return stats;
    }

    // 비율 계산 메서드
    private void calculateRatioAndSort(List<ViewCompanyResponseDto> stats) {
        BigDecimal totalSum = stats.stream()
                .map(ViewCompanyResponseDto::getTotalEmission)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalSum.compareTo(BigDecimal.ZERO) > 0) {
            for (ViewCompanyResponseDto dto : stats) {
                BigDecimal ratio = dto.getTotalEmission()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalSum, 1, RoundingMode.HALF_UP);
                dto.setRatio(ratio.doubleValue());
            }
        } else {
            stats.forEach(dto -> dto.setRatio(0.0));
        }

        // 배출량 높은 순 정렬 (Top 5 추출하기 좋게)
        stats.sort((a, b) -> b.getTotalEmission().compareTo(a.getTotalEmission()));
    }
}
