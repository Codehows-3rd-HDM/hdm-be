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

    // 등록
    @Transactional
    public ProductClassResponseDto create(ProductClassRequestDto dto) {

        if (repository.existsByClassName(dto.getClassName())) {
            throw new RuntimeException("이미 등록된 제품 분류명입니다: " + dto.getClassName());
        }

        ProductClass saved = repository.save(
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
        return repository.findAll().stream()
                .map(ProductClassResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<ProductClassResponseDto> search(ProductClassSearchDto searchDto) {

        if (searchDto == null || searchDto.getClassName() == null || searchDto.getClassName().isBlank()) {
            return getAll();
        }

        String keyword = searchDto.getClassName().toLowerCase();

        return repository.findAll().stream()
                .filter(pc -> pc.getClassName() != null &&
                        pc.getClassName().toLowerCase().contains(keyword))
                .map(ProductClassResponseDto::fromEntity)
                .toList();
    }
}
