package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.OperationPurposeInquiryRequestDto;
import com.hdmbe.inquiry.dto.OperationPurposeInquiryResponseDto;
import com.hdmbe.inquiry.service.OperationPurposeInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/view/operation-purpose")
@RequiredArgsConstructor
public class OperationPurposeInquiryController {

    private final OperationPurposeInquiryService operationPurposeInquiryService;

    @GetMapping
    public List<OperationPurposeInquiryResponseDto> search(
            OperationPurposeInquiryRequestDto requestDto
    ) {
        return operationPurposeInquiryService.getPurposeEmission(requestDto);
    }
}
