package com.hdmbe.controller;

import com.hdmbe.dto.CompanyRequestDto;
import com.hdmbe.dto.CompanyResponseDto;
import com.hdmbe.dto.CompanySearchDto;
import com.hdmbe.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService service;

    @PostMapping
    public ResponseEntity<CompanyResponseDto> create(@RequestBody CompanyRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/search")
    public ResponseEntity<List<CompanyResponseDto>> search(@RequestBody CompanySearchDto dto) {
        return ResponseEntity.ok(service.search(dto));
    }
}
