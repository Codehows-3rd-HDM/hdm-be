package com.hdmbe.controller;

import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.dto.CarModelSearchDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.service.CarModelService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/car-model")
@RequiredArgsConstructor
@Builder
public class CarModelController {

    private final CarModelService service;
    // 등록
    @PostMapping
    public CarModelResponseDto create(@RequestBody CarModelRequestDto dto) {
        return service.create(dto);
    }
    // 조회
    @GetMapping
    public ResponseEntity<List<CarModelResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 검색
    @PostMapping("/search")
    public List<CarModelResponseDto> search(@RequestBody CarModelSearchDto searchDto) {
        return service.search(searchDto);
    }
}
