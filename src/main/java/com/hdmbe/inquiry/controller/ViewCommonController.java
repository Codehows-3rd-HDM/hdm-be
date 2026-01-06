package com.hdmbe.inquiry.controller;

import com.hdmbe.emissionTarget.service.EmissionTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/common")
@RequiredArgsConstructor
public class ViewCommonController {

    private final EmissionTargetService emissionTargetService;

    /**
     * 배출량 조회용 - 실적 데이터가 있는 연도만 반환
     */
    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        return ResponseEntity.ok(emissionTargetService.getAvailableBaseYears());
    }

    /**
     * 목표 대비 배출량 조회용 - 실적 + 목표 데이터가 있는 모든 연도 반환
     */
    @GetMapping("/years/all")
    public ResponseEntity<List<Integer>> getAllAvailableYears() {
        return ResponseEntity.ok(emissionTargetService.getAvailableAnalysisYears());
    }
}
