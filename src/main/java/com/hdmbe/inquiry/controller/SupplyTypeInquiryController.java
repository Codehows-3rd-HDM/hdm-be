package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.SupplyTypeInquiryRequestDto;
import com.hdmbe.inquiry.dto.SupplyTypeInquiryResponseDto;
import com.hdmbe.inquiry.service.SupplyTypeInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/supply-type")
@RequiredArgsConstructor
public class SupplyTypeInquiryController {

    private final SupplyTypeInquiryService supplyTypeInquiryService;

    @GetMapping
    public List<SupplyTypeInquiryResponseDto> search(
            SupplyTypeInquiryRequestDto requestDto
    ) {
        return supplyTypeInquiryService.getSupplyTypeEmission(requestDto);
    }
}
