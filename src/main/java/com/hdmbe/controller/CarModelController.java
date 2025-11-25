package com.hdmbe.controller;

import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/car-model")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService service;

    @PostMapping
    public CarModelResponseDto create(@RequestBody CarModelRequestDto dto) {
        return service.create(dto);
    }
}
