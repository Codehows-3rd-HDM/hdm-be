package com.hdmbe.emissionTarget.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hdmbe.emissionTarget.dto.FullTargetResponseDto;
import com.hdmbe.emissionTarget.dto.MonthlyActualResponseDto;
import com.hdmbe.emissionTarget.dto.SaveEmissionTargetRequest;
import com.hdmbe.emissionTarget.service.EmissionTargetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/emission-targets")
@RequiredArgsConstructor
public class EmissionTargetController {

    private final EmissionTargetService emissionTargetService;

    // 연도별 목표 조회 (전체/Scope1/Scope3 묶음)
    @GetMapping("/{year}")
    public ResponseEntity<FullTargetResponseDto> getTargets(@PathVariable int year) {
        System.out.println("[EmissionTargetController] GET /" + year);
        return ResponseEntity.ok(emissionTargetService.getTargets(year));
    }

    // 연도별 목표 저장 (등록/수정 동일 처리)
    @PostMapping("/{year}")
    public ResponseEntity<FullTargetResponseDto> saveTargets(@PathVariable int year,
            @RequestBody SaveEmissionTargetRequest request) {
        System.out.println("[EmissionTargetController] POST /" + year);
        return ResponseEntity.ok(emissionTargetService.saveTargets(year, request));
    }

    // 기준실적 연도 목록 조회
    @GetMapping("/base-years")
    public ResponseEntity<List<Integer>> getAvailableBaseYears() {
        System.out.println("[EmissionTargetController] GET /base-years");
        return ResponseEntity.ok(emissionTargetService.getAvailableBaseYears());
    }

    // 특정 연도의 월별 실제 배출량
    @GetMapping("/actuals/{year}")
    public ResponseEntity<MonthlyActualResponseDto> getActuals(@PathVariable int year) {
        System.out.println("[EmissionTargetController] GET /actuals/" + year);
        return ResponseEntity.ok(emissionTargetService.getActuals(year));
    }

    // 특정 연도의 Scope별 월별 실제 배출량
    @GetMapping("/actuals/{year}/scope/{scope}")
    public ResponseEntity<MonthlyActualResponseDto> getActualsByScope(@PathVariable int year, @PathVariable int scope) {
        System.out.println("[EmissionTargetController] GET /actuals/" + year + "/scope/" + scope);
        return ResponseEntity.ok(emissionTargetService.getActualsByScope(year, scope));
    }
}
