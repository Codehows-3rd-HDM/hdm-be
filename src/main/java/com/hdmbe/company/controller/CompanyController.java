package com.hdmbe.company.controller;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponseDto> create(@RequestBody CompanyRequestDto dto) {
        return ResponseEntity.ok(companyService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponseDto>> getAll() {
        return ResponseEntity.ok(companyService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponseDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) Long processId,
            @RequestParam(required = false) Long productClassId,
            @RequestParam(required = false) String address
    ) {
        CompanyRequestDto dto = CompanyRequestDto.builder()
                .keyword(keyword)
                .companyNameFilter(companyName)
                .processIdFilter(processId)
                .productClassIdFilter(productClassId)
                .addressFilter(address)
                .build();

        return ResponseEntity.ok(companyService.search(dto));
    }

}
