package com.hdmbe.controller;

import com.hdmbe.dto.VehicleRequestDto;
import com.hdmbe.dto.VehicleResponseDto;
import com.hdmbe.dto.VehicleSearchDto;
import com.hdmbe.service.VehicleService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
@Builder
public class VehicleController {

    private final VehicleService service;
    // 등록
    @PostMapping
    public VehicleResponseDto create(@RequestBody VehicleRequestDto dto) {
        return service.create(dto);
    }

    // 조회
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // 검색
    @PostMapping("/search")
    public ResponseEntity<List<VehicleResponseDto>> search(@RequestBody VehicleSearchDto searchDto) {
        return ResponseEntity.ok(service.search(searchDto));
    }


}
