package com.hdmbe.controller;

import com.hdmbe.dto.OperationPurposeRequestDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.service.OperationPurposeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operation-purpose")
@RequiredArgsConstructor
public class OperationPurposeController {

    private final OperationPurposeService service;

    @PostMapping
    public ResponseEntity<OperationPurposeResponseDto> create(@RequestBody OperationPurposeRequestDto requestDto) {
        OperationPurposeResponseDto response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }
}
