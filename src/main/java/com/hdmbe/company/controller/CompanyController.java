package com.hdmbe.company.controller;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    // 등록
    @PostMapping
    public ResponseEntity<CompanyResponseDto> create(@RequestBody CompanyRequestDto dto) {
        return ResponseEntity.ok(companyService.create(dto));
    }

    // 조회,검색
    @GetMapping("/search")
    public Page<CompanyResponseDto> search(
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String supplyTypeName,
            @RequestParam(required = false) String supplyCustomerName,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return companyService.search(
                companyName,
                supplyTypeName,
                supplyCustomerName,
                address,
                keyword,
                page,
                size
        );
    }

    // 단일 수정
    @PutMapping("/{id}")
    public CompanyResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody CompanyRequestDto request
    ) {
        return companyService.updateSingle(id, request);
    }

    // 페이지 전체 수정
    @PatchMapping("/bulk")
    public List<CompanyResponseDto> updateMultiple(
            @RequestBody List<CompanyRequestDto> requestList
    ) {
        return companyService.updateMultiple(requestList);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        companyService.delete(id);
    }
}
