package com.hdmbe.supplyType.controller;

import com.hdmbe.supplyType.dto.SupplyTypeRequestDto;
import com.hdmbe.supplyType.dto.SupplyTypeResponseDto;
import com.hdmbe.supplyType.service.SupplyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return supplyTypeService.search(
                supplyTypeName,
                page,
                size
        );
    }
}
