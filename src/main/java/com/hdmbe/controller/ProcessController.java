package com.hdmbe.controller;

import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService service;

    @PostMapping
    public ResponseEntity<ProcessResponseDto> create(@RequestBody ProcessRequestDto requestDto) {
        ProcessResponseDto response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }
}
