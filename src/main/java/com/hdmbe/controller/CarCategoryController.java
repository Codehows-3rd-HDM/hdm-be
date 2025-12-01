package com.hdmbe.controller;

import com.hdmbe.dto.CarCategoryRequestDto;
import com.hdmbe.dto.CarCategoryResponseDto;
import com.hdmbe.service.CarCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CarCategoryController {

    private final CarCategoryService carCategoryService;

    @PostMapping
    public ResponseEntity<CarCategoryResponseDto> create(@RequestBody CarCategoryRequestDto requestDto) {
        return ResponseEntity.ok(carCategoryService.create(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<CarCategoryResponseDto>> getAll() {
        return ResponseEntity.ok(carCategoryService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarCategoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(carCategoryService.findById(id));
    }
}
