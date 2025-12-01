package com.hdmbe.service;

import com.hdmbe.dto.OperationPurposeRequestDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.dto.OperationPurposeSearchDto;
import com.hdmbe.entity.OperationPurpose;
import com.hdmbe.repository.OperationPurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationPurposeService {

    private final OperationPurposeRepository repository;

    // 등록
    @Transactional
    public OperationPurposeResponseDto create(OperationPurposeRequestDto dto) {
        OperationPurpose saved = repository.save(
                OperationPurpose.builder()
                        .purposeName(dto.getPurposeName())
                        .defaultScope(dto.getDefaultScope())
                        .build()
        );

        return OperationPurposeResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> getAll() {
        return repository.findAll().stream()
                .map(OperationPurposeResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> search(OperationPurposeSearchDto dto) {

        return repository.findAll().stream()
                .filter(p -> dto.getPurposeName() == null
                        || p.getPurposeName().contains(dto.getPurposeName()))
                .filter(p -> dto.getDefaultScope() == null
                        || p.getDefaultScope().equals(dto.getDefaultScope()))
                .map(OperationPurposeResponseDto::fromEntity)
                .toList();
    }
}
