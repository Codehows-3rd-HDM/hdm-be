package com.hdmbe.SupplyCustomer.controller;

import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.service.SupplyCustomerService;
import com.hdmbe.company.dto.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/supply-customer")
@RequiredArgsConstructor
public class SupplyCustomerController {

    private final SupplyCustomerService supplyCustomerService;

    // 등록
    @PostMapping
    public ResponseEntity<SupplyCustomerResponseDto> create(@RequestBody SupplyCustomerRequestDto dto) {
        return ResponseEntity.ok(supplyCustomerService.create(dto));
    }

    // 조회+검색 (페이지네이션)
    @GetMapping("search")
    public Page<SupplyCustomerResponseDto> search(
            @RequestParam(required = false) String customerName,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        return supplyCustomerService.search(
                customerName,
                pageable
        );
    }

    // 단일 수정
    @PutMapping("/{id}")
    public SupplyCustomerResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody SupplyCustomerRequestDto dto
    ) {
        return supplyCustomerService.updateSingle(id, dto);
    }

    // 다중 수정
    @PatchMapping("/bulk")
    public List<SupplyCustomerResponseDto> updateMultiple(
            @RequestBody List<SupplyCustomerRequestDto> dtoList
    ) {
        return supplyCustomerService.updateMultiple(dtoList);
    }

    // 단일 삭제
    @DeleteMapping("/{id}")
    public void deleteSingle(@PathVariable Long id) {
        supplyCustomerService.deleteSingle(id);
    }

    // 다중 삭제 (체크박스)
    @DeleteMapping
    public void deleteMultiple(@RequestBody List<Long> ids) {
        supplyCustomerService.deleteMultiple(ids);
    }
}
