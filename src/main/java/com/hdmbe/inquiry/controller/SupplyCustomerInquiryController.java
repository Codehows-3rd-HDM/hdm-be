package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.SupplyCustomerInquiryRequestDto;
import com.hdmbe.inquiry.dto.SupplyCustomerInquiryResponseDto;
import com.hdmbe.inquiry.service.SupplyCustomerInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/supply-customer")
@RequiredArgsConstructor
public class SupplyCustomerInquiryController {

    private final SupplyCustomerInquiryService supplyCustomerInquiryService;

    @GetMapping
    public List<SupplyCustomerInquiryResponseDto> search(
            SupplyCustomerInquiryRequestDto dto
    ) {
        return supplyCustomerInquiryService.getSupplyCustomerEmission(dto);
    }
}
