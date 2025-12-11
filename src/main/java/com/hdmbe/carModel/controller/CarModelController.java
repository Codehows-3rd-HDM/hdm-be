package com.hdmbe.carModel.controller;

import com.hdmbe.carModel.dto.CarModelRequestDto;
import com.hdmbe.carModel.dto.CarModelResponseDto;
import com.hdmbe.carModel.service.CarModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<CarModelResponseDto> create(@RequestBody CarModelRequestDto dto) {
        return ResponseEntity.ok(carModelService.create(dto));
    }

    // 조회 + 검색 (페이지네이션)
    @GetMapping
    public Page<CarModelResponseDto> findAll(
            @ModelAttribute CarModelRequestDto dto,
            Pageable pageable
    ) {
        return carModelService.findAll(dto, pageable);
    }

    // 단일 수정
    @PatchMapping("/{id}")
    public CarModelResponseDto updateOne(
            @PathVariable Long id,
            @RequestBody CarModelRequestDto dto
    ) {
        return carModelService.updateOne(id, dto);
    }
    // 페이지 전체 수정
//    @PatchMapping("/bulk")
//    public List<CarModelResponseDto> updateBulk(@RequestBody List<CarModelRequestDto> dto) {
//        return carModelService.updateBulk(dto);
//    }
    // 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        carModelService.delete(id);
    }


}
