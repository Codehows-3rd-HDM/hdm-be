package com.hdmbe.carCategory.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hdmbe.carCategory.dto.CarCategoryRequestDto;
import com.hdmbe.carCategory.dto.CarCategoryResponseDto;
import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carCategory.repository.CarCategoryRepository;

import lombok.RequiredArgsConstructor;

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
                .categoryName(requestDto.getCategoryId())
                .parentCategory(parent)
                .build();

        return CarCategoryResponseDto.fromEntity(carCategoryRepository.save(category));
    }

    public List<CarCategoryResponseDto> findAll() {
        return carCategoryRepository.findAll().stream()
                .map(CarCategoryResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<CarCategoryResponseDto> getAll() {
        return findAll();
    }

    public CarCategoryResponseDto findById(Long id) {
        CarCategory category = carCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + id));
        return CarCategoryResponseDto.fromEntity(category);
    }
}
