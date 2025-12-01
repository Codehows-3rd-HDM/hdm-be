package com.hdmbe.service;

import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.dto.CarModelSearchDto;
import com.hdmbe.entity.CarCategory;
import com.hdmbe.entity.CarModel;
import com.hdmbe.repository.CarCategoryRepository;
import com.hdmbe.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarCategoryRepository carCategoryRepository;

    // 등록
    @Transactional
    public CarModelResponseDto create(CarModelRequestDto dto) {
        CarCategory category = carCategoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다"));

        CarModel saved = carModelRepository.save(
                CarModel.builder()
                        .carCategory(category)
                        .fuelType(dto.getFuelType())
                        .customEfficiency(dto.getCustomEfficiency())
                        .build()
        );

        return CarModelResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> getAll() {
        return carModelRepository.findAll().stream()
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> search(CarModelSearchDto dto) {
        return carModelRepository.findAll().stream()
                .filter(m -> dto.getCategoryId() == null
                        || m.getCarCategory().getId().equals(dto.getCategoryId()))
                .filter(m -> dto.getFuelType() == null
                        || m.getFuelType().equals(dto.getFuelType()))
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }
}
