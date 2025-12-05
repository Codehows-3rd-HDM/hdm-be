package com.hdmbe.productClass.service;

import com.hdmbe.productClass.dto.ProductClassRequestDto;
import com.hdmbe.productClass.dto.ProductClassResponseDto;
import com.hdmbe.productClass.entity.ProductClass;
import com.hdmbe.productClass.repository.ProductClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductClassService {

    private final ProductClassRepository productClassRepository;

    // 등록
    @Transactional
    public ProductClassResponseDto create(ProductClassRequestDto dto) {

        if (productClassRepository.existsByClassName(dto.getClassName())) {
            throw new RuntimeException("이미 등록된 제품 분류명입니다: " + dto.getClassName());
        }

        ProductClass saved = productClassRepository.save(
                ProductClass.builder()
                        .className(dto.getClassName())
                        .remark(dto.getRemark())
                        .build()
        );

        return ProductClassResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<ProductClassResponseDto> getAll() {
        return productClassRepository.findAll().stream()
                .map(ProductClassResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<ProductClassResponseDto> search(ProductClassRequestDto dto) {
        List<ProductClass> result;

        if (dto.getClassNameFilter() != null && !dto.getClassNameFilter().isEmpty()) {
            result = productClassRepository.findByClassNameContaining(dto.getClassNameFilter());
        } else {
            throw new IllegalArgumentException("검색 조건이 필요합니다.");
        }

        return result.stream().map(ProductClassResponseDto::fromEntity).toList();
    }

}
