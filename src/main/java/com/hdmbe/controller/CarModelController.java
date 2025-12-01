package com.hdmbe.controller;

import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.dto.CarModelSearchDto;
import com.hdmbe.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car-model")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService service;

    @PostMapping
    public ResponseEntity<CarModelResponseDto> create(@RequestBody CarModelRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CarModelResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/search")
    public ResponseEntity<List<CarModelResponseDto>> search(@RequestBody CarModelSearchDto dto) {
        return ResponseEntity.ok(service.search(dto));
    }
}
