package com.hdmbe.operationPurpose.controller;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.service.OperationPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/operation-purpose")
@RequiredArgsConstructor
public class OperationPurposeController {

    private final OperationPurposeService operationPurposeService;

    @PostMapping
    public ResponseEntity<OperationPurposeResponseDto> create(@RequestBody OperationPurposeRequestDto dto) {
        return ResponseEntity.ok(operationPurposeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OperationPurposeResponseDto>> getAll() {
        return ResponseEntity.ok(operationPurposeService.getAll());
    }

    @GetMapping("/search")
    public ResponseEntity<List<OperationPurposeResponseDto>> search(
            @RequestParam(required = false) String purposeName,
            @RequestParam(required = false) Integer defaultScope
    ) {

        OperationPurposeRequestDto dto = OperationPurposeRequestDto.builder()
                .purposeNameFilter(purposeName)
                .scopeFilter(defaultScope)
                .build();

        return ResponseEntity.ok(operationPurposeService.search(dto));
    }


}
