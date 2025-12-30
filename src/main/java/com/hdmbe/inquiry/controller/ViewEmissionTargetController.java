package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.ViewEmissionTargetDto;
import com.hdmbe.inquiry.service.ViewEmissionTargetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/target")
@RequiredArgsConstructor
public class ViewEmissionTargetController {

    private final ViewEmissionTargetService viewEmissionTargetService;

    @GetMapping
    public ResponseEntity<ViewEmissionTargetDto> getTargetAnalysis(
            // 1. int 대신 Integer(객체) (null 체크를 위해)
            // 2. required = false로 해서 안 보내도 에러 안 나게 함
            @RequestParam(name = "year", required = false) Integer year,
            @RequestParam(name = "type", defaultValue = "total") String type
    )
    {
        // year가 안 넘어왔으면 (null이면) -> 자동으로 '올해'로 설정
        if (year == null) {
            year = java.time.LocalDate.now().getYear();
        }

        // 1. 서비스 호출
        ViewEmissionTargetDto response = viewEmissionTargetService.analyzeTargetVsActual(year, type);

        // 2. 응답
        return ResponseEntity.ok(response);
    }

    // 연도 목록 조회 API
    @GetMapping("/years")
    public ResponseEntity<List<Integer>> getAvailableYears() {
        List<Integer> years = viewEmissionTargetService.getAvailableYears();
        return ResponseEntity.ok(years);
    }
}
