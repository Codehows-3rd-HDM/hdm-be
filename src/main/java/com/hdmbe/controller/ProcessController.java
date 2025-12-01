package com.hdmbe.controller;

import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.dto.ProcessSearchDto;
import com.hdmbe.service.ProcessService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
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
    @PostMapping("/search")
    public List<ProcessResponseDto> search(@RequestBody ProcessSearchDto searchDto) {
        return processService.search(searchDto);
    }
}
