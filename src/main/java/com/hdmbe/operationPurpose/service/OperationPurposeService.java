package com.hdmbe.operationPurpose.service;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.repository.OperationPurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationPurposeService {

    private final OperationPurposeRepository operationPurposeRepository;

    // 등록
    @Transactional
    public OperationPurposeResponseDto create(OperationPurposeRequestDto dto) {
        OperationPurpose saved = operationPurposeRepository.save(
                OperationPurpose.builder()
                        .purposeName(dto.getPurposeName())
                        .defaultScope(dto.getDefaultScopeId())
                        .build()
        );

        return OperationPurposeResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> getAll() {
        return operationPurposeRepository.findAll().stream()
                .map(OperationPurposeResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> search(OperationPurposeRequestDto dto) {

        List<OperationPurpose> result;

        if (dto.getPurposeNameFilter() != null && !dto.getPurposeNameFilter().isEmpty()) {
            result = operationPurposeRepository.findByPurposeNameContaining(dto.getPurposeNameFilter());
        }
        else if (dto.getScopeFilter() != null) {
            result = operationPurposeRepository.findByDefaultScope(dto.getScopeFilter());
        }
        else {
            throw new IllegalArgumentException("검색 조건을 입력하세요.");
        }

        return result.stream()
                .map(OperationPurposeResponseDto::fromEntity)
                .toList();
    }

}
