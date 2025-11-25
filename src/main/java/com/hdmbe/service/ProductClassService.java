package com.hdmbe.service;

import com.hdmbe.dto.ProductClassRequestDto;
import com.hdmbe.dto.ProductClassResponseDto;
import com.hdmbe.entity.ProductClass;
import com.hdmbe.repository.ProductClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductClassService {

    private final ProductClassRepository repository;

    @Transactional
    public ProductClassResponseDto create(ProductClassRequestDto dto) {

        // 클래스명 중복 검사
        if (repository.existsByClassName(dto.getClassName())) {
            throw new RuntimeException("이미 등록된 제품 분류명입니다: " + dto.getClassName());
        }

        ProductClass saved = repository.save(
                ProductClass.builder()
                        .className(dto.getClassName())
                        .build()
        );

        return ProductClassResponseDto.builder()
                .id(saved.getId())
                .className(saved.getClassName())
                .build();
    }
}
