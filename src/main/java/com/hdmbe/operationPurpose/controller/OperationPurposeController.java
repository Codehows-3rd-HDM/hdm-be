package com.hdmbe.operationPurpose.controller;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    @GetMapping("/search")
    public Page<OperationPurposeResponseDto> search(
            @RequestParam(required = false) String purposeName,
            @RequestParam(required = false) Integer defaultScope,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 15) Pageable pageable
    ) {
        return operationPurposeService.search(
                purposeName,
                defaultScope,
                keyword,
                pageable
        );
    }

    // 단일 수정
    @PutMapping("/{id}")
    public OperationPurposeResponseDto updateSingle(
            @PathVariable Long id,
            @RequestBody OperationPurposeRequestDto dto
    ) {
        return operationPurposeService.updateSingle(id, dto);
    }

    // 전체 수정
    @PatchMapping("/bulk")
    public List<OperationPurposeResponseDto> updateMultiple(
            @RequestBody List<OperationPurposeRequestDto> dtoList
    ) {
        return operationPurposeService.updateMultiple(dtoList);
    }

    // 단일 삭제
    @DeleteMapping("/{id}")
    public void deleteSingle(@PathVariable Long id) {
        operationPurposeService.deleteSingle(id);
    }

    // 다중 삭제
    @DeleteMapping
    public void deleteMultiple(@RequestBody List<Long> ids) {
        operationPurposeService.deleteMultiple(ids);
    }
}
