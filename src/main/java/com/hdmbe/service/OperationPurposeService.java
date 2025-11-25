package com.hdmbe.service;

import com.hdmbe.dto.OperationPurposeRequestDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.entity.OperationPurpose;
import com.hdmbe.repository.OperationPurposeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperationPurposeService {

    private final OperationPurposeRepository repository;

    @Transactional
    public OperationPurposeResponseDto create(OperationPurposeRequestDto dto) {

        // 목적명 중복 방지
        if (repository.existsByPurposeName(dto.getPurposeName())) {
            throw new RuntimeException("이미 등록된 목적명입니다: " + dto.getPurposeName());
        }

        OperationPurpose saved = repository.save(
                OperationPurpose.builder()
                        .purposeName(dto.getPurposeName())
                        .defaultScope(dto.getDefaultScope())
                        .build()
        );

        return new OperationPurposeResponseDto(
                saved.getId(),
                saved.getPurposeName(),
                saved.getDefaultScope()
        );
    }
}
