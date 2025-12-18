package com.hdmbe.vehicle.controller;

import com.hdmbe.vehicle.dto.VehicleRequestDto;
import com.hdmbe.vehicle.dto.VehicleResponseDto;
import com.hdmbe.vehicle.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

//    // 등록
//    @PostMapping
//    public ResponseEntity<VehicleResponseDto> create(@RequestBody VehicleRequestDto dto) {
//        return ResponseEntity.ok(vehicleService.create(dto));
//    }

    // 전체 조회 + 검색
    @GetMapping("search")
    public Page<VehicleResponseDto> search(
            @RequestParam(required = false) String carNumber,
            @RequestParam(required = false) Long purposeId,
            @RequestParam(required = false) String companyName,
            @RequestParam(required = false) String driverMemberId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return vehicleService.search(
                carNumber,
                purposeId,
                companyName,
                driverMemberId,
                keyword,
                page,
                size
        );
    }
    // 단일 수정
    @PutMapping("/{id}")
    public VehicleResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody VehicleRequestDto dto
    ) {
        return vehicleService.updateSingle(id, dto);
    }
    // 전체 수정
    @PatchMapping("/bulk")
    public List<VehicleResponseDto> updateMultiple(
            @RequestBody List<VehicleRequestDto> dto
    ) {
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
