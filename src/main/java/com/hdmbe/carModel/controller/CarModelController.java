package com.hdmbe.carModel.controller;

import com.hdmbe.carModel.dto.CarModelRequestDto;
import com.hdmbe.carModel.dto.CarModelResponseDto;
import com.hdmbe.carModel.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/car-model")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;

    @PostMapping
    public ResponseEntity<CarModelResponseDto> create(@RequestBody CarModelRequestDto dto) {
        return ResponseEntity.ok(carModelService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<CarModelResponseDto>> getAll() {
        return ResponseEntity.ok(carModelService.getAll());
    }
    @GetMapping("/search")
    public ResponseEntity<List<CarModelResponseDto>> search(CarModelRequestDto dto) {
        return ResponseEntity.ok(carModelService.search(dto));
    }


}
