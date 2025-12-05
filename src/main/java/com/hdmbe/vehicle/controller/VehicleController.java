package com.hdmbe.vehicle.controller;

import com.hdmbe.vehicle.dto.VehicleRequestDto;
import com.hdmbe.vehicle.dto.VehicleResponseDto;
import com.hdmbe.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    // 등록
    @PostMapping
    public ResponseEntity<VehicleResponseDto> create(@RequestBody VehicleRequestDto dto) {
        return ResponseEntity.ok(vehicleService.create(dto));
    }

    // 전체 조회
    @GetMapping
    public ResponseEntity<List<VehicleResponseDto>> getAll() {
        return ResponseEntity.ok(vehicleService.getAll());
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<VehicleResponseDto>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String carNumber,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String driverMemberId
    ) {
        VehicleRequestDto dto = VehicleRequestDto.builder()
                .keyword(keyword)
                .carNumberFilter(carNumber)
                .companyNameFilter(companyName)
                .driverMemberIdFilter(driverMemberId)
                .build();
        return ResponseEntity.ok(vehicleService.search(dto));
    }
}
