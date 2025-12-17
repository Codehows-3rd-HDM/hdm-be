package com.hdmbe.operationPurpose.controller;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/operation-purpose")
@RequiredArgsConstructor
public class OperationPurposeController {

    private final OperationPurposeService operationPurposeService;

    // 등록
    @PostMapping
    public ResponseEntity<OperationPurposeResponseDto> create(@RequestBody OperationPurposeRequestDto dto) {
        return ResponseEntity.ok(operationPurposeService.create(dto));
    }

    // 조회+검색
    @GetMapping("search")
    public Page<OperationPurposeResponseDto> search(
            @RequestParam(required = false) String purposeName,
            @RequestParam(required = false) Integer defaultScope,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        return operationPurposeService.search(
                purposeName,
                defaultScope,
                keyword,
                page,
                size
        );
    }


}
