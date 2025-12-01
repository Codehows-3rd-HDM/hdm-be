package com.hdmbe.controller;

import com.hdmbe.dto.OperationPurposeRequestDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.dto.OperationPurposeSearchDto;
import com.hdmbe.service.OperationPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operation-purpose")
@RequiredArgsConstructor
public class OperationPurposeController {

    private final OperationPurposeService service;

    @PostMapping
    public ResponseEntity<OperationPurposeResponseDto> create(@RequestBody OperationPurposeRequestDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<OperationPurposeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/search")
    public ResponseEntity<List<OperationPurposeResponseDto>> search(@RequestBody OperationPurposeSearchDto dto) {
        return ResponseEntity.ok(service.search(dto));
    }
}
