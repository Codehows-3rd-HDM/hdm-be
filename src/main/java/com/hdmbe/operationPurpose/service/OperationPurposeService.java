package com.hdmbe.operationPurpose.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hdmbe.operationPurpose.dto.OperationPurposeRequestDto;
import com.hdmbe.operationPurpose.dto.OperationPurposeResponseDto;
import com.hdmbe.operationPurpose.entity.OperationPurpose;
import com.hdmbe.operationPurpose.repository.OperationPurposeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

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
                        .defaultScope(dto.getDefaultScope())
                        .build());

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
            int size) {
        System.out.println("[OperationPurposeService] 운행목적 검색 요청 - purposeName: " + purposeName
                + ", scope: " + scope + ", keyword: " + keyword
                + ", page: " + page + ", size: " + size);

        int pageSize = Math.min(size, 50);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return operationPurposeRepository.search(
                purposeName,
                scope,
                keyword,
                pageable)
                .map(OperationPurposeResponseDto::fromEntity);
    }

    // 단일 수정
    @Transactional
    public OperationPurposeResponseDto updateSingle(Long id, OperationPurposeRequestDto dto) {
        validateUpdate(dto);

        OperationPurpose purpose = operationPurposeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("운행목적 없음 id=" + id));

        if (dto.getPurposeName() != null) {
            purpose.setPurposeName(dto.getPurposeName());
        }

        if (dto.getDefaultScope() != null) {
            purpose.setDefaultScope(dto.getDefaultScope());
        }

        return OperationPurposeResponseDto.fromEntity(purpose);
    }

    // 전체 수정
    @Transactional
    public List<OperationPurposeResponseDto> updateMultiple(
            List<OperationPurposeRequestDto> dtoList) {
        return dtoList.stream()
                .map(dto -> updateSingle(dto.getId(), dto))
                .toList();
    }

    // 단일 삭제
    @Transactional
    public void deleteSingle(Long id) {
        OperationPurpose purpose = operationPurposeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("운행목적 없음 id=" + id));

        operationPurposeRepository.delete(purpose);
    }

    // 다중 삭제
    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            deleteSingle(id);
        }
    }

    // 유효성 검사
    private void validateCreate(OperationPurposeRequestDto dto) {
        if (dto.getPurposeName() == null || dto.getPurposeName().isBlank()) {
            throw new IllegalArgumentException("운행목적명 필수");
        }

        if (dto.getDefaultScope() == null) {
            throw new IllegalArgumentException("Scope 필수");
        }

        validateScope(dto.getDefaultScope());
    }

    private void validateUpdate(OperationPurposeRequestDto dto) {
        if (dto.getPurposeName() != null && dto.getPurposeName().isBlank()) {
            throw new IllegalArgumentException("운행목적명 공백 불가");
        }

        if (dto.getDefaultScope() != null) {
            validateScope(dto.getDefaultScope());
        }
    }

    private void validateScope(Integer scope) {
        if (scope < 1 || scope > 4)
            throw new IllegalArgumentException("Scope 값이 올바르지 않습니다.");
    }

    @Transactional
    public OperationPurpose getOrCreate(String name, String scopeStr) {
        // Scope 파싱 (문자열 "1" -> 숫자 1)
        int scope = parseScope(scopeStr);

        return operationPurposeRepository.findByPurposeName(name)
                .orElseGet(() -> operationPurposeRepository.save(
                        OperationPurpose.builder().purposeName(name).defaultScope(scope).build()));
    }

    private int parseScope(String scopeStr) {
        // 1. 빈 값 체크 (엄격 모드)
        if (scopeStr == null || scopeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Scope 값은 필수입니다. (입력된 값: 공백)");
        }

        try {
            // 2. 숫자 변환
            int scope = Integer.parseInt(scopeStr.trim());

            // 3. 유효 값 체크 (1, 3, 4만 허용! 2는 안 됨!)
            if (scope != 1 && scope != 3 && scope != 4) {
                throw new IllegalArgumentException(
                        "지원하지 않는 Scope입니다. (입력된 값: " + scope + ")\n" +
                                "※ 허용된 값: 1, 3, 4 / Scope 2는 지원하지 않습니다.");
            }

            return scope;

        } catch (NumberFormatException e) {
            // 4. 숫자가 아닐 때
            throw new IllegalArgumentException("Scope는 숫자(1, 3, 4)로만 입력해야 합니다. (입력된 값: " + scopeStr + ")");
        }
    }

}
