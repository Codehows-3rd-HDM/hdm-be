package com.hdmbe.carModel.service;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carCategory.repository.CarCategoryRepository;
import com.hdmbe.carModel.dto.CarModelRequestDto;
import com.hdmbe.carModel.dto.CarModelResponseDto;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carModel.repository.CarModelRepository;
import jakarta.persistence.EntityNotFoundException;
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
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        CarModel saved = carModelRepository.save(
                CarModel.builder()
                        .carCategory(category)
                        .fuelType(dto.getFuelType())
                        .customEfficiency(dto.getCustomEfficiency())
                        .build()
        );

        return CarModelResponseDto.fromEntity(saved);
    }

    // 조회
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> getAll() {
        return carModelRepository.findAll().stream()
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> search(CarModelRequestDto dto) {

        List<CarModel> result;

        if (dto.getCategoryId() != null) {
            result = carModelRepository.findByCarCategoryId(dto.getCategoryId());
        }

        else if (dto.getCategoryName() != null && !dto.getCategoryName().isEmpty()) {
            // 🔥 여기 수정됨
            result = carModelRepository.findByCategoryNameLike(dto.getCategoryName());
        }

        else if (dto.getFuelType() != null) {
            result = carModelRepository.findByFuelType(dto.getFuelType());
        }

        else if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            result = carModelRepository.searchByKeyword(dto.getKeyword());
        }

        else {
            throw new IllegalArgumentException("최소 하나의 검색 조건이 필요합니다.");
        }

        return result.stream()
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }
}
