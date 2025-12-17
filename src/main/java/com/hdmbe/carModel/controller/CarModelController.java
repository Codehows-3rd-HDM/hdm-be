package com.hdmbe.carModel.controller;

import com.hdmbe.carModel.dto.CarModelRequestDto;
import com.hdmbe.carModel.dto.CarModelResponseDto;
import com.hdmbe.carModel.service.CarModelService;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/car-model")
@RequiredArgsConstructor
public class CarModelController {

    private final CarModelService carModelService;

    // 등록
    @PostMapping
    public ResponseEntity<CarModelResponseDto> create(@RequestBody CarModelRequestDto request) {
        return ResponseEntity.ok(carModelService.createCarModel(request));
    }

    // 조회 + 검색 (페이지네이션)
    @GetMapping("/search")
    public Page<CarModelResponseDto> search(
            @RequestParam(required = false) Long parentCategoryId,
            @RequestParam(required = false) Long carCategoryId,
            @RequestParam(required = false) FuelType fuelType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return carModelService.search(
                parentCategoryId,
                carCategoryId,
                fuelType,
                keyword,
                page,
                size
        );
    }


    // 단일 수정
    @PutMapping("/{id}")
    public CarModelResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody CarModelRequestDto dto) {
        return carModelService.updateSingle(id, dto);
    }

    // 페이지 전체 수정
    @PatchMapping("/bulk")
    public List<CarModelResponseDto> updateMultiple(@RequestBody List<CarModelRequestDto> dtoList) {
        return carModelService.updateMultiple(dtoList);
    }

    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carModelService.deleteCarModel(id);
    }

}
