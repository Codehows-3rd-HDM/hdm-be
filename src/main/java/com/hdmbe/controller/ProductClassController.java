package com.hdmbe.controller;

import com.hdmbe.dto.ProductClassRequestDto;
import com.hdmbe.dto.ProductClassResponseDto;
import com.hdmbe.service.ProductClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product-class")
@RequiredArgsConstructor
public class ProductClassController {

    private final ProductClassService service;

    @PostMapping
    public ResponseEntity<ProductClassResponseDto> create(@RequestBody ProductClassRequestDto requestDto) {
        ProductClassResponseDto response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }
}
