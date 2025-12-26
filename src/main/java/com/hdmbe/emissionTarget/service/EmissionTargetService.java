package com.hdmbe.emissionTarget.service;

import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.emissionTarget.dto.CategoryTargetDto;
import com.hdmbe.emissionTarget.dto.FullTargetResponseDto;
import com.hdmbe.emissionTarget.dto.MonthlyActualResponseDto;
import com.hdmbe.emissionTarget.dto.MonthlyValueDto;
import com.hdmbe.emissionTarget.dto.SaveEmissionTargetRequest;
import com.hdmbe.emissionTarget.entity.EmissionTarget;
import com.hdmbe.emissionTarget.repository.EmissionTargetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmissionTargetService {

    private static final List<String> TARGET_TYPES = List.of("Total", "Scope1", "Scope3");

    private final EmissionTargetRepository emissionTargetRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;

    @Transactional(readOnly = true)
    public FullTargetResponseDto getTargets(int year) {
        System.out.println("[EmissionTargetService] 연도별 목표 조회 요청 year=" + year);
        return FullTargetResponseDto.builder()
                .Total(loadCategory(year, "Total"))
                .Scope1(loadCategory(year, "Scope1"))
                .Scope3(loadCategory(year, "Scope3"))
                .build();
    }

    @Transactional
    public FullTargetResponseDto saveTargets(int year, SaveEmissionTargetRequest request) {
        System.out.println("[EmissionTargetService] 연도별 목표 저장 시작 year=" + year);

        Map<String, CategoryTargetDto> requestMap = new HashMap<>();
        requestMap.put("Total", request.getTotal());
        requestMap.put("Scope1", request.getScope1());
        requestMap.put("Scope3", request.getScope3());

        TARGET_TYPES.forEach(type -> upsertCategory(year, type, requestMap.get(type)));

        System.out.println("[EmissionTargetService] 연도별 목표 저장 완료 year=" + year);
        return getTargets(year);
    }

    @Transactional(readOnly = true)
    public List<Integer> getAvailableBaseYears() {
        List<Integer> years = emissionMonthlyRepository.findDistinctYears();
        System.out.println("[EmissionTargetService] 실적 존재 연도 조회 결과=" + years);
        return years;
    }

    @Transactional(readOnly = true)
    public MonthlyActualResponseDto getActuals(int year) {
        System.out.println("[EmissionTargetService] 기준 연도 실적 조회 year=" + year);

        List<MonthlyValueDto> monthly = initEmptyMonthly();

        List<EmissionMonthlyRepository.MonthlySumView> rows = emissionMonthlyRepository.sumByYear(year);
        for (EmissionMonthlyRepository.MonthlySumView row : rows) {
            int monthIdx = Math.max(1, Math.min(12, row.getMonth()));
            monthly.set(monthIdx - 1, MonthlyValueDto.builder()
                    .month(monthIdx)
                    .value(normalize(row.getTotal()))
                    .build());
        }

        BigDecimal total = monthly.stream()
                .map(MonthlyValueDto::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        System.out.println("[EmissionTargetService] 기준 연도 실적 합계=" + total);

        return MonthlyActualResponseDto.builder()
                .year(year)
                .monthly(monthly)
                .total(total)
                .build();
    }

    private CategoryTargetDto loadCategory(int year, String type) {
        List<EmissionTarget> records = emissionTargetRepository.findByYearAndTargetTypeOrderByMonthAsc(year, type);
        if (records.isEmpty()) {
            System.out.println("[EmissionTargetService] 대상 데이터 없음 year=" + year + ", type=" + type + " -> zero fill");
            return CategoryTargetDto.empty();
        }

        CategoryTargetDto dto = CategoryTargetDto.empty();

        records.forEach(record -> {
            if (record.getMonth() == null) {
                dto.setTotal(normalize(record.getTargetEmission()));
            } else {
                int idx = Math.max(1, Math.min(12, record.getMonth()));
                dto.getMonthly().set(idx - 1, MonthlyValueDto.builder()
                        .month(idx)
                        .value(normalize(record.getTargetEmission()))
                        .build());
            }
        });

        // total 누락 시 월 합계로 보정
        if (dto.getTotal() == null || dto.getTotal().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal sum = dto.getMonthly().stream()
                    .map(MonthlyValueDto::getValue)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTotal(sum);
        }

        return dto;
    }

    private void upsertCategory(int year, String type, CategoryTargetDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException(type + " 데이터가 없습니다.");
        }

        validateCategory(type, dto);

        System.out.println("[EmissionTargetService] " + type + " 기존 데이터 삭제 및 재등록");
        emissionTargetRepository.deleteByYearAndTargetType(year, type);

        LocalDateTime now = LocalDateTime.now();
        List<EmissionTarget> entities = new ArrayList<>();

        entities.add(EmissionTarget.builder()
                .year(year)
                .month(null)
                .targetType(type)
                .targetEmission(normalize(dto.getTotal()))
                .setAt(now)
                .build());

        dto.getMonthly().stream()
                .sorted(Comparator.comparing(MonthlyValueDto::getMonth))
                .forEach(monthly -> entities.add(
                EmissionTarget.builder()
                        .year(year)
                        .month(monthly.getMonth())
                        .targetType(type)
                        .targetEmission(normalize(monthly.getValue()))
                        .setAt(now)
                        .build()
        ));

        emissionTargetRepository.saveAll(entities);
    }

    private void validateCategory(String type, CategoryTargetDto dto) {
        if (dto.getMonthly() == null || dto.getMonthly().size() != 12) {
            throw new IllegalArgumentException(type + " 월별 데이터는 12개월이어야 합니다.");
        }

        Set<Integer> monthSet = new HashSet<>();
        for (MonthlyValueDto monthly : dto.getMonthly()) {
            if (monthly.getMonth() == null) {
                throw new IllegalArgumentException(type + " 월 정보가 누락되었습니다.");
            }
            int m = monthly.getMonth();
            if (m < 1 || m > 12) {
                throw new IllegalArgumentException(type + " 월 정보가 범위를 벗어났습니다: " + m);
            }
            if (!monthSet.add(m)) {
                throw new IllegalArgumentException(type + " 월 정보가 중복되었습니다: " + m);
            }
        }

        BigDecimal total = normalize(dto.getTotal());
        BigDecimal monthlySum = dto.getMonthly().stream()
                .map(m -> normalize(m.getValue()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (monthlySum.compareTo(total) != 0) {
            throw new IllegalArgumentException(type + " 월 합계(" + monthlySum + ")와 총합(" + total + ")이 일치해야 합니다.");
        }
    }

    private List<MonthlyValueDto> initEmptyMonthly() {
        List<MonthlyValueDto> monthly = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            monthly.add(MonthlyValueDto.builder().month(i).value(BigDecimal.ZERO).build());
        }
        return monthly;
    }

    private BigDecimal normalize(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(3, RoundingMode.HALF_UP);
        }
        return value.setScale(3, RoundingMode.HALF_UP);
    }
}
