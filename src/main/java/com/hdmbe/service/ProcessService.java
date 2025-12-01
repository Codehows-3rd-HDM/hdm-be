package com.hdmbe.service;

import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.dto.ProcessSearchDto;
import com.hdmbe.entity.ProcessEntity;
import com.hdmbe.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository repository;

    // 등록
    @Transactional
    public ProcessResponseDto create(ProcessRequestDto requestDto) {
        ProcessEntity saved = repository.save(
                ProcessEntity.builder()
                        .processName(requestDto.getProcessName())
                        .build()
        );
        return ProcessResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(ProcessResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> search(ProcessSearchDto searchDto) {
        if (searchDto == null || searchDto.getProcessName() == null || searchDto.getProcessName().isBlank()) {
            return getAll();
        }

        return repository.findAll()
                .stream()
                .filter(p -> p.getProcessName().contains(searchDto.getProcessName()))
                .map(ProcessResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
