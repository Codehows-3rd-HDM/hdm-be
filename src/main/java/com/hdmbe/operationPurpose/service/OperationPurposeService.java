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

    // 전체 조회+ 검색
    @Transactional(readOnly = true)
    public Page<OperationPurposeResponseDto> search(
            String purposeName,
            Integer scope,
            String keyword,
            int page,
            int size
    ) {
        int pageSize = Math.min(size, 50);

        Pageable pageable = PageRequest.of(
                page,
                pageSize,
                Sort.by("id").ascending()
        );

        Page<OperationPurpose> result =
                operationPurposeRepository.search(
                        purposeName,
                        scope,
                        keyword,
                        pageable
                );

        return result.map(OperationPurposeResponseDto::fromEntity);
    }

}
