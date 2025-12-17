package com.hdmbe.operationPurpose.service;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.repository.OperationPurposeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // 전체 조회 (드롭다운용)
    @Transactional(readOnly = true)
    public List<OperationPurposeResponseDto> getAll() {
        return operationPurposeRepository.findAll().stream()
                .map(OperationPurposeResponseDto::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    // 전체 조회+ 검색
    @Transactional(readOnly = true)
    public Page<OperationPurposeResponseDto> search(
            String purposeName,
            Integer scope,
            String keyword,
            int page,
            int size
    ) {
        System.out.println("[OperationPurposeService] 운행목적 검색 요청 - purposeName: " + purposeName
                + ", scope: " + scope + ", keyword: " + keyword
                + ", page: " + page + ", size: " + size);

        int pageSize = Math.min(size, 50);

        Pageable pageable = PageRequest.of(
                page,
                pageSize,
                Sort.by("id").ascending()
        );

        Page<OperationPurpose> result
                = operationPurposeRepository.search(
                        purposeName,
                        scope,
                        keyword,
                        pageable
                );

        System.out.println("[OperationPurposeService] 운행목적 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(OperationPurposeResponseDto::fromEntity);
    }

}
