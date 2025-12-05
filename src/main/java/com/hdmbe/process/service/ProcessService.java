package com.hdmbe.process.service;

import com.hdmbe.process.dto.ProcessRequestDto;
import com.hdmbe.process.dto.ProcessResponseDto;
import com.hdmbe.process.entity.ProcessEntity;
import com.hdmbe.process.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository processRepository;

    // 등록
    @Transactional
    public ProcessResponseDto create(ProcessRequestDto requestDto) {
        ProcessEntity saved = processRepository.save(
                ProcessEntity.builder()
                        .processName(requestDto.getProcessName())
                        .build()
        );
        return ProcessResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> getAll() {
        return processRepository.findAll()
                .stream()
                .map(ProcessResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> search(ProcessRequestDto dto) {

        if (dto.getProcessNameFilter() == null || dto.getProcessNameFilter().isEmpty()) {
            throw new IllegalArgumentException("검색 조건을 입력하세요.");
        }

        return processRepository
                .findByProcessNameContaining(dto.getProcessNameFilter())
                .stream()
                .map(ProcessResponseDto::fromEntity)
                .toList();
    }
}