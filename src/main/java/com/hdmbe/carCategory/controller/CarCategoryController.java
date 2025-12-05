package com.hdmbe.carCategory.controller;

import com.hdmbe.carCategory.dto.CarCategoryRequestDto;
import com.hdmbe.carCategory.dto.CarCategoryResponseDto;
import com.hdmbe.carCategory.service.CarCategoryService;
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
