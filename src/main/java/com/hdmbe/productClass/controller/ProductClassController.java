package com.hdmbe.productClass.controller;

import com.hdmbe.productClass.dto.ProductClassRequestDto;
import com.hdmbe.productClass.dto.ProductClassResponseDto;
import com.hdmbe.productClass.service.ProductClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product-class")
@RequiredArgsConstructor
public class ProductClassController {

    private final ProductClassService productClassService;

    @PostMapping
    public ResponseEntity<ProductClassResponseDto> create(@RequestBody ProductClassRequestDto dto) {
        return ResponseEntity.ok(productClassService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductClassResponseDto>> getAll() {
        return ResponseEntity.ok(productClassService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductClassResponseDto>> search(@RequestParam String className) {
        ProductClassRequestDto dto = ProductClassRequestDto.builder()
                .classNameFilter(className)
                .build();
        return ResponseEntity.ok(productClassService.search(dto));
    }
}
