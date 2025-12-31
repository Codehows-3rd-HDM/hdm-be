package com.hdmbe.vehicle.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hdmbe.vehicle.dto.VehicleRequestDto;
import com.hdmbe.vehicle.dto.VehicleResponseDto;
import com.hdmbe.vehicle.service.VehicleService;

import lombok.RequiredArgsConstructor;

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

    // 전체 조회 + 검색
    @GetMapping("search")
    public Page<VehicleResponseDto> search(
            @RequestParam(required = false) String carNumber,
            @RequestParam(required = false) Long purposeName,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String driverMemberId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 15) Pageable pageable) {
        return vehicleService.search(
                carNumber,
                purposeName,
                companyName,
                driverMemberId,
                keyword,
                pageable);
    }

    // 단일 수정
    @PutMapping("/{id}")
    public VehicleResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody VehicleRequestDto dto) {
        return vehicleService.updateSingle(id, dto);
    }

    // 전체 수정
    @PatchMapping("/bulk-update")
    public List<VehicleResponseDto> updateMultiple(
            @RequestBody List<VehicleRequestDto> dto) {
        return vehicleService.updateMultiple(dto);
    }

    // 단일 삭제
    @DeleteMapping("/{id}")
    public void deleteSingle(@PathVariable Long id) {
        vehicleService.deleteSingle(id);
    }

    // 멀티 삭제
    @DeleteMapping
    public void deleteMultiple(@RequestBody List<Long> ids) {
        vehicleService.deleteMultiple(ids);
    }
}
