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
                        .defaultScope(dto.getDefaultScope())
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

    @Transactional
    public OperationPurpose getOrCreate(String name, String scopeStr)
    {
        // Scope 파싱 (문자열 "1" -> 숫자 1)
        int scope = parseScope(scopeStr);

        return operationPurposeRepository.findByPurposeName(name)
                .orElseGet(() -> operationPurposeRepository.save(
                        OperationPurpose.builder().purposeName(name).defaultScope(scope).build()
                ));
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
                                "※ 허용된 값: 1, 3, 4 / Scope 2는 지원하지 않습니다."
                );
            }

            return scope;

        } catch (NumberFormatException e) {
            // 4. 숫자가 아닐 때
            throw new IllegalArgumentException("Scope는 숫자(1, 3, 4)로만 입력해야 합니다. (입력된 값: " + scopeStr + ")");
        }
    }

}
