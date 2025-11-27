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
        if (repository.existsByPurposeName(dto.getPurposeName())) {
            throw new RuntimeException("이미 등록된 목적명입니다: " + dto.getPurposeName());
        }

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

    // 검색 (필터링)
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> search(OperationPurposeSearchDto searchDto) {

        if (searchDto.getType() == null || searchDto.getType().equals("all")) {
            return repository.findAll()
                    .stream()
                    .map(OperationPurposeResponseDto::fromEntity)
                    .toList();
        }

        if ("purposeName".equals(searchDto.getType()) && searchDto.getKeyword() != null) {
            return repository.findByPurposeNameContaining(searchDto.getKeyword())
                    .stream()
                    .map(OperationPurposeResponseDto::fromEntity)
                    .toList();
        }

        if ("scope".equals(searchDto.getType()) && searchDto.getScope() != null) {
            return repository.findByDefaultScope(searchDto.getScope())
                    .stream()
                    .map(OperationPurposeResponseDto::fromEntity)
                    .toList();
        }

        return List.of(); // 조건에 맞는 검색 없으면 빈 리스트 반환
    }

}
