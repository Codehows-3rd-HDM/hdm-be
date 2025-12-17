package com.hdmbe.SupplyCustomer.controller;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.service.SupplyCustomerService;
import com.hdmbe.company.dto.CompanyResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

//    // 조회+검색 (페이지네이션)
//    @GetMapping("/search")
//    public Page<SupplyCustomerResponseDto> search(
//            @RequestParam(required = false) String customerName,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "15") int size
//    ) {
//        return supplyCustomerService.search(
//                customerName,
//                page,
//                size
//        );
//    }
//    // 단일 수정
//
//    // 전체 수정
//
//    // 삭제
}
