package com.hdmbe.SupplyCustomer.controller;

import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.service.SupplyCustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/supply-customer")
@RequiredArgsConstructor
public class SupplyCustomerController {

    private final SupplyCustomerService supplyCustomerService;

    @PostMapping
    public ResponseEntity<SupplyCustomerResponseDto> create(@RequestBody SupplyCustomerRequestDto dto) {
        return ResponseEntity.ok(supplyCustomerService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<SupplyCustomerResponseDto>> getAll() {
        return ResponseEntity.ok(supplyCustomerService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SupplyCustomerResponseDto>> search(@RequestParam String customerName) {
        SupplyCustomerRequestDto dto = SupplyCustomerRequestDto.builder()
                .customerNameFilter(customerName)
                .build();
        return ResponseEntity.ok(supplyCustomerService.search(dto));
    }
}
