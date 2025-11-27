package com.hdmbe.service;

import com.hdmbe.dto.ProcessRequestDto;
import com.hdmbe.dto.ProcessResponseDto;
import com.hdmbe.entity.ProcessEntity;
import com.hdmbe.repository.ProcessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessService {

    private final ProcessRepository repository;
        // 등록
        @Transactional
        public ProcessResponseDto create(ProcessRequestDto requestDto) {

            ProcessEntity process = ProcessEntity.builder()
                    .processName(requestDto.getProcessName())
                    .build();

            ProcessEntity saved = repository.save(process);
            return ProcessResponseDto.fromEntity(saved);
        }
        // 조회
        @Transactional(readOnly = true)
        public List<ProcessResponseDto> findAll() {
            return repository.findAll()
                    .stream()
                    .map(ProcessResponseDto::fromEntity)
                    .toList();
        }
        // 검색
        @Transactional(readOnly = true)
        public List<ProcessResponseDto> search() {
            return repository.findAll().stream()
                    .map(ProcessResponseDto::fromEntity)
                    .toList();
        }

}
