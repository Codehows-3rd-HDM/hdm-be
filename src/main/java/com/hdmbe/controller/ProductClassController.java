package com.hdmbe.controller;

import com.hdmbe.dto.ProductClassRequestDto;
import com.hdmbe.dto.ProductClassResponseDto;
import com.hdmbe.dto.ProductClassSearchDto;
import com.hdmbe.service.ProductClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-class")
@RequiredArgsConstructor
public class ProductClassController {

    private final ProductClassService service;

    @PostMapping
    public ResponseEntity<ProductClassResponseDto> create(@RequestBody ProductClassRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProductClassResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/search")
    public ResponseEntity<List<ProductClassResponseDto>> search(@RequestBody ProductClassSearchDto dto) {
        return ResponseEntity.ok(service.search(dto));
    }
}
