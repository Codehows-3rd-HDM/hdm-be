package com.hdmbe.service;

import com.hdmbe.dto.CarCategoryRequestDto;
import com.hdmbe.dto.CarCategoryResponseDto;
import com.hdmbe.entity.CarCategory;
import com.hdmbe.repository.CarCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CarCategoryService {

    private final CarCategoryRepository carCategoryRepository;

    public CarCategoryResponseDto create(CarCategoryRequestDto requestDto) {
        CarCategory parent = null;
        if (requestDto.getParentId() != null) {
            parent = carCategoryRepository.findById(requestDto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("상위 카테고리를 찾을 수 없습니다: " + requestDto.getParentId()));
        }

        CarCategory category = CarCategory.builder()
                .categoryName(requestDto.getCategoryName())
                .parentCategory(parent)
                .build();

        return CarCategoryResponseDto.fromEntity(carCategoryRepository.save(category));
    }

    public List<CarCategoryResponseDto> findAll() {
        return carCategoryRepository.findAll().stream()
                .map(CarCategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public CarCategoryResponseDto findById(Long id) {
        CarCategory category = carCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        return CarCategoryResponseDto.fromEntity(category);
    }
}
