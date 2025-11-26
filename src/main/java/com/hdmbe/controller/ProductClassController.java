package com.hdmbe.controller;

import com.hdmbe.dto.ProductClassRequestDto;
import com.hdmbe.dto.ProductClassResponseDto;
import com.hdmbe.dto.ProductClassSearchDto;
import com.hdmbe.service.ProductClassService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-class")
@RequiredArgsConstructor
@Builder
public class ProductClassController {

    private final ProductClassService service;
    // 등록
    @PostMapping
    public ResponseEntity<ProductClassResponseDto> create(@RequestBody ProductClassRequestDto requestDto) {
        ProductClassResponseDto response = service.create(requestDto);
        return ResponseEntity.ok(response);

    }
    // 조회
    @GetMapping
    public ResponseEntity<List<ProductClassResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    // 검색
    @PostMapping("/search")
    public ResponseEntity<List<ProductClassResponseDto>> search(
            @RequestBody ProductClassSearchDto searchDto) {

        return ResponseEntity.ok(service.search(searchDto));
    }

}
