package com.hdmbe.controller;

import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.service.ProcessService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor
@Builder
public class ProcessController {

    private final ProcessService service;
    // 등록
    @PostMapping
    public ResponseEntity<ProcessResponseDto> create(@RequestBody ProcessRequestDto requestDto) {
        ProcessResponseDto response = service.create(requestDto);
        return ResponseEntity.ok(response);
    }
    // 조회
    @GetMapping
    public ResponseEntity<List<ProcessResponseDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

}
