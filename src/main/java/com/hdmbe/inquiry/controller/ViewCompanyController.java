package com.hdmbe.inquiry.controller;


import com.hdmbe.inquiry.dto.ViewCompanyRequestDto;
import com.hdmbe.inquiry.dto.ViewCompanyResponseDto;
import com.hdmbe.inquiry.service.ViewCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ViewCompanyController {

    private final ViewCompanyService viewCompanyService;

    @GetMapping("/view/company")
    public ResponseEntity<List<ViewCompanyResponseDto>> getCompanyEmission(
            ViewCompanyRequestDto viewCompanyRequestDto
    )
    {
        List<ViewCompanyResponseDto> result = viewCompanyService.findEmission(viewCompanyRequestDto);

        return ResponseEntity.ok(result);
    }
}
