package com.hdmbe.carCategory.controller;


import com.hdmbe.carCategory.dto.CarCategoryResponseDto;
import com.hdmbe.carCategory.repository.CarCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class CarCategoryController {

    private final CarCategoryRepository carCategoryRepository;

    @GetMapping("/all")
    public List<CarCategoryResponseDto> getAllCategories() {
        return carCategoryRepository.findAll().stream()
                .map(CarCategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
