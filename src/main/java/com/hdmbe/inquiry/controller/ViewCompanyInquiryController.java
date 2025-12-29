package com.hdmbe.inquiry.controller;


import com.hdmbe.inquiry.dto.ViewCompanyRequestDto;
import com.hdmbe.inquiry.dto.ViewCompanyResponseDto;
import com.hdmbe.inquiry.service.ViewCompanyInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ViewCompanyInquiryController {

    private final ViewCompanyInquiryService viewCompanyInquiryService;

    @GetMapping("/view/company")
    public ResponseEntity<List<ViewCompanyResponseDto>> getCompanyEmission(
            ViewCompanyRequestDto viewCompanyRequestDto
    )
    {
        List<ViewCompanyResponseDto> result = viewCompanyInquiryService.findEmission(viewCompanyRequestDto);

        return ResponseEntity.ok(result);
    }
}
