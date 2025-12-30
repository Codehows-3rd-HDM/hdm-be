package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.ViewPeriodEmissionRequestDto;
import com.hdmbe.inquiry.dto.ViewPeriodEmissionResponseDto;
import com.hdmbe.inquiry.service.ViewPeriodInquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ViewPeriodEmissionController {

    private final ViewPeriodInquiryService inquiryService;

    /**
     * 기간별 탄소 배출량 조회 API
     * 요청 URL 예시: /view/period?carId=1&startDate=2024-01-01&endDate=2024-12-31
     */
    @GetMapping("/view/period")
    public ResponseEntity<ViewPeriodEmissionResponseDto> getPeriodEmission(
            // @ModelAttribute를 쓰면 쿼리 파라미터가 DTO에 자동으로 쏙 들어갑니다.
            @ModelAttribute ViewPeriodEmissionRequestDto requestDto
    ) {
        log.info("기간별 조회 요청 - 기간: {} ~ {}",
                requestDto.getStartDate(), requestDto.getEndDate());

        ViewPeriodEmissionResponseDto response = inquiryService.searchPeriodData(requestDto);

        return ResponseEntity.ok(response);
    }
}
