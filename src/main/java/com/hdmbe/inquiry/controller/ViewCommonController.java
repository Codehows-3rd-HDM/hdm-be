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

    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        return ResponseEntity.ok(emissionTargetService.getAvailableAnalysisYears());
    }
}
