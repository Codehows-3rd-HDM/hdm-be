package com.hdmbe.inquiry.service;


import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;
import com.hdmbe.inquiry.dto.ViewPeriodEmissionRequestDto;
import com.hdmbe.inquiry.dto.ViewPeriodEmissionResponseDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import com.hdmbe.vehicle.repository.VehicleOperationPurposeMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ViewPeriodService {

    private final EmissionDailyRepository dailyRepository;
    private final VehicleOperationPurposeMapRepository purposeMapRepository;

    /**
            * 기간별 탄소 배출량 조회
     * Input: ViewPeriodEmissionRequestDto (검색 조건)
     * Output: ViewPeriodEmissionResponseDto (결과)
     */
    public ViewPeriodEmissionResponseDto searchPeriodData(ViewPeriodEmissionRequestDto requestDto) {

        LocalDate startDate = requestDto.getStartDate();
        LocalDate endDate = requestDto.getEndDate();

        // 1. [현재] 전체 차량 합계
        ViewPeriodEmissionResponseDto.EmissionSummary current = calculateTotalSummary(startDate, endDate);

        // 2. [작년] 전체 차량 합계
        ViewPeriodEmissionResponseDto.EmissionSummary lastYear = calculateTotalSummary(startDate.minusYears(1), endDate.minusYears(1));

        return ViewPeriodEmissionResponseDto.builder()
                .current(current)
                .lastYear(lastYear)
                .build();
    }

    // =================================================================
    //  전체 차량 집계 로직
    // =================================================================
    private ViewPeriodEmissionResponseDto.EmissionSummary calculateTotalSummary(LocalDate start, LocalDate end) {

        // 1. 해당 기간의 "모든 차량" 로그 조회
        // (Repository에 findAllByOperationDateBetween 메서드 필요)
        List<CarbonEmissionDailyLog> logs = dailyRepository.findAllByOperationDateBetween(start, end);

        BigDecimal totalEmission = BigDecimal.ZERO;
        BigDecimal totalDistance = BigDecimal.ZERO;
        BigDecimal scope1 = BigDecimal.ZERO;
        BigDecimal scope3 = BigDecimal.ZERO;

        // Key: 차량ID, Value: 이력 리스트
        // 같은 차가 또 나오면 DB 안 가고 여기서 꺼내씀
        Map<Long, List<VehicleOperationPurposeMap>> historyCache = new HashMap<>();

        // 2. 로그 하나씩 까보면서 집계 (Loop)
        for (CarbonEmissionDailyLog log : logs) {
            BigDecimal emission = log.getDailyEmission();
            if (emission == null) continue;

            Vehicle vehicle = log.getVehicle();
            LocalDate logDate = log.getOperationDate();     // 로그의 날짜 꺼냄

            // 이 차량의 Scope가 1도 아니고 3도 아니면 -> 제외 대상
            // 차량과 "로그 날짜"를 같이 넘김
            // 캐시(Map)를 함께 넘겨서 조회 속도 올림
            int scopeType = getVehicleScope(vehicle, logDate, historyCache);

            if (scopeType == 0) {
                continue; // 여기서 바로 다음 차로 넘어감 (집계 제외)
            }

            // --- 아래는 "유효한(Scope 1,3)" 차량만 실행됨 ---

            // 2-1. 총 배출량 누적 (이제 직원차량은 안 더해짐)
            totalEmission = totalEmission.add(emission);

            // 2-2. 거리 누적
            if (vehicle.getOperationDistance() != null) {
                BigDecimal dailyDist = vehicle.getOperationDistance().multiply(new BigDecimal("2"));
                totalDistance = totalDistance.add(dailyDist);
            }

            // 2-3. Scope별 분리 누적
            if (scopeType == 1) {
                scope1 = scope1.add(emission);
            } else if (scopeType == 3) {
                scope3 = scope3.add(emission);
            }
        }

        return ViewPeriodEmissionResponseDto.EmissionSummary.builder()
                .totalEmission(totalEmission)
                .totalDistance(totalDistance)
                .scope1(scope1)
                .scope3(scope3)
                .build();
    }

    // 날짜(date)를 받아서 자바에서 판단
    private int getVehicleScope(Vehicle vehicle, LocalDate logDate,
                                Map<Long, List<VehicleOperationPurposeMap>> cache) {

        // 1. 이 차의 모든 이력을 "과거 -> 현재" 순서로 가져옴
        List<VehicleOperationPurposeMap> maps =
                purposeMapRepository.findAllByVehicleOrderByEndDateAsc(vehicle);

        // 2. 하나씩 꺼내보면서 "이 로그 날짜가 내 구간인가?" 확인
        for (VehicleOperationPurposeMap map : maps) {
            LocalDate endDate = map.getEndDate();

            // 조건: "이력이 아직 안 끝났거나(NULL)" OR "로그 날짜보다 늦게 끝났으면"
            // => 즉, 로그 날짜가 이 맵핑의 유효기간 안에 들어온다는 뜻
            if (endDate == null || !logDate.isAfter(endDate)) {

                OperationPurpose purpose = map.getOperationPurpose();
                if (purpose != null && purpose.getDefaultScope() != null) {
                    int scope = purpose.getDefaultScope();
                    // 1, 3번만 인정 (아니면 0)
                    return (scope == 1 || scope == 3) ? scope : 0;
                }
                // 목적 데이터가 이상하면 그냥 무시하고 다음 루프나 0 리턴
                return 0;
            }
        }

        // 여기까지 왔는데 아무것도 안 걸리면 (매우 드문 경우) -> 집계 제외
        return 0;
    }
}
