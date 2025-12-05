package com.hdmbe.process.controller;

import com.hdmbe.process.dto.ProcessRequestDto;
import com.hdmbe.process.dto.ProcessResponseDto;
import com.hdmbe.process.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/process")
@RequiredArgsConstructor
public class ProcessController {

    private final ProcessService processService;

    // 등록
    @PostMapping
    public ProcessResponseDto create(@RequestBody ProcessRequestDto requestDto) {
        return processService.create(requestDto);
    }

    // 전체 조회
    @GetMapping
    public List<ProcessResponseDto> getAll() {
        return processService.getAll();
    }

    // 검색
    @GetMapping("/search")
    public ResponseEntity<List<ProcessResponseDto>> search(
            @RequestParam(required = false) String processName
    ) {

        ProcessRequestDto dto = ProcessRequestDto.builder()
                .processNameFilter(processName)
                .build();

        return ResponseEntity.ok(processService.search(dto));
    }
}
