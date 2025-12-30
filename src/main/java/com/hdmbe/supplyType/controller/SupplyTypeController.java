package com.hdmbe.supplyType.controller;

import com.hdmbe.supplyType.dto.SupplyTypeRequestDto;
import com.hdmbe.supplyType.dto.SupplyTypeResponseDto;
import com.hdmbe.supplyType.service.SupplyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/supply-type")
@RequiredArgsConstructor
public class SupplyTypeController {

    private final SupplyTypeService supplyTypeService;

    // 등록
    @PostMapping
    public SupplyTypeResponseDto create(@RequestBody SupplyTypeRequestDto requestDto) {
        return supplyTypeService.create(requestDto);
    }

    // 전체 조회
    @GetMapping("search")
    public Page<SupplyTypeResponseDto> search(
            @RequestParam(required = false) String supplyTypeName,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        return supplyTypeService.search(
                supplyTypeName,
                pageable
        );
    }

    // 단일 수정
    @PutMapping("/{id}")
    public SupplyTypeResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody SupplyTypeRequestDto dto
    ) {
        return supplyTypeService.updateSingle(id, dto);
    }

    // 다중 수정
    @PatchMapping("/bulk")
    public List<SupplyTypeResponseDto> updateMultiple(
            @RequestBody List<SupplyTypeRequestDto> dtoList
    ) {
        return supplyTypeService.updateMultiple(dtoList);
    }

    // 단일 삭제
    @DeleteMapping("/{id}")
    public void deleteSingle(@PathVariable Long id) {
        supplyTypeService.deleteSingle(id);
    }

    // 다중 삭제
    @DeleteMapping
    public void deleteMultiple(@RequestBody List<Long> ids) {
        supplyTypeService.deleteMultiple(ids);
    }
}
