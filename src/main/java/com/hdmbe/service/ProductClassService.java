package com.hdmbe.service;

import com.hdmbe.dto.ProductClassRequestDto;
import com.hdmbe.dto.ProductClassResponseDto;
import com.hdmbe.dto.ProductClassSearchDto;
import com.hdmbe.entity.ProductClass;
import com.hdmbe.repository.ProductClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClassService {

    private final ProductClassRepository repository;

    @Transactional
    public ProductClassResponseDto create(ProductClassRequestDto dto) {
        // 등록
        if (repository.existsByClassName(dto.getClassName())) {
            throw new RuntimeException("이미 등록된 제품 분류명입니다: " + dto.getClassName());  // 클래스명 중복 검사
        }

        ProductClass saved = repository.save(
                ProductClass.builder()
                        .className(dto.getClassName())
                        .remark(dto.getRemark())
                        .build()
        );
        return ProductClassResponseDto.fromEntity(saved);
    }

    //  조회
    @Transactional(readOnly = true)
    public List<ProductClassResponseDto> getAll() {
        return repository.findAll().stream()
                .map(ProductClassResponseDto::fromEntity)
                .toList();
    }

    // 전체 검색
    @Transactional(readOnly = true)
    public List<ProductClassResponseDto> search(ProductClassSearchDto searchDto) {

        if (searchDto == null ||
                (searchDto.getClassName() == null || searchDto.getClassName().isBlank())) {
            return getAll();
        }
        // 품목명 검색
        if (searchDto.getClassName() != null && !searchDto.getClassName().isBlank()) {
            return repository.findByClassNameContainingIgnoreCase(searchDto.getClassName())
                    .stream()
                    .map(ProductClassResponseDto::fromEntity)
                    .toList();
        }
        return getAll();
    }
}

