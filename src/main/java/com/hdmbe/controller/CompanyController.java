package com.hdmbe.controller;

import com.hdmbe.dto.CompanyRequestDto;
import com.hdmbe.dto.CompanyResponseDto;
import com.hdmbe.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public CompanyResponseDto create(@RequestBody CompanyRequestDto dto) {
        return service.create(dto);
    }
}
