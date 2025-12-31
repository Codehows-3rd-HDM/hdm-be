package com.hdmbe.inquiry.service;

import com.hdmbe.emissionTarget.dto.CategoryTargetDto;
import com.hdmbe.emissionTarget.dto.FullTargetResponseDto;
import com.hdmbe.emissionTarget.dto.MonthlyActualResponseDto;
import com.hdmbe.emissionTarget.service.EmissionTargetService;
import com.hdmbe.inquiry.dto.ViewEmissionTargetDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ViewEmissionTargetService {

    private final EmissionTargetService emissionTargetService;

    // 메인 메서드
    @Transactional(readOnly = true)
    public ViewEmissionTargetDto analyzeTargetVsActual(int year, String type)
    {
        // 1. 목표 데이터 가져오기
        FullTargetResponseDto targets = emissionTargetService.getTargets(year);
        CategoryTargetDto targetDto = extractTargetByType(targets, type);

        // 2. 실적 데이터 가져오기
        MonthlyActualResponseDto actual = getActualByType(year, type);

        // [추가] 최신 데이터가 몇 월까지 있는지 가져오기
        Integer latestMonth = emissionTargetService.getLatestDataMonth(year);

        // 3. 월별 데이터 병합
        List<ViewEmissionTargetDto.MonthlyComparisonDto> comparisonList = new ArrayList<>();

        // 현재 날짜 확인 (미래 데이터 처리를 위해)
        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        // 1월부터 12월까지 루프
        for (int i = 0; i < 12; i++) {
            int month = i + 1;

            // 목표값 (없으면 0)
            BigDecimal tVal = targetDto.getMonthly().get(i).getValue();
            if (tVal == null) tVal = BigDecimal.ZERO;

            // 실적값 가져오기
            BigDecimal aVal = actual.getMonthly().get(i).getValue();
            if (aVal == null) aVal = BigDecimal.ZERO;

            // [로직] 선택한 연도가 미래거나, '올해의 미래 달'인 경우 실적을 0(또는 null)으로 처리
            // (DB에 데이터가 없어서 이미 0일 확률이 높지만, 확실하게 하기 위함)
            if (year == currentYear && month > currentMonth) {
                aVal = BigDecimal.ZERO; // 차트에서 미래 실적은 0으로 표시
            }

            comparisonList.add(ViewEmissionTargetDto.MonthlyComparisonDto.builder()
                    .month(month)
                    .target(tVal) // 목표는 미래도 표시
                    .actual(aVal) // 실적은 과거~현재만
                    .build());
        }

        // 4. 상단 KPI 카드용 총계 계산
        // 목표 총합 (Total Target)
        BigDecimal totalTarget = targetDto.getTotal();
        if (totalTarget == null || totalTarget.compareTo(BigDecimal.ZERO) == 0) {
            // 월별 합계로 계산
            totalTarget = comparisonList.stream()
                    .map(ViewEmissionTargetDto.MonthlyComparisonDto::getTarget)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // 실적 총합 (Total Actual) - 현재까지 누적된 값
        BigDecimal totalActual = actual.getTotal();

        // 달성률 계산 (실적 / 목표 * 100)
        Double rate = 0.0;
        if (totalTarget.compareTo(BigDecimal.ZERO) > 0) {
            rate = totalActual.divide(totalTarget, 3, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        return ViewEmissionTargetDto.builder()
                .totalTarget(totalTarget)
                .totalActual(totalActual)
                .totalAchievementRate(rate)
                .monthlyData(comparisonList)
                .latestMonth(latestMonth)
                .build();
    }

    private CategoryTargetDto extractTargetByType(FullTargetResponseDto fullDto, String type) {
        // 프론트 탭: "scope1", "scope3", "total" (소문자로 올 수도 있음)
        if ("Scope1".equalsIgnoreCase(type)) return fullDto.getScope1();
        if ("Scope3".equalsIgnoreCase(type)) return fullDto.getScope3();
        return fullDto.getTotal();
    }

    private MonthlyActualResponseDto getActualByType(int year, String type) {
        if ("Scope1".equalsIgnoreCase(type)) return emissionTargetService.getActualsByScope(year, 1);
        if ("Scope3".equalsIgnoreCase(type)) return emissionTargetService.getActualsByScope(year, 3);
        return emissionTargetService.getActuals(year);
    }
}
