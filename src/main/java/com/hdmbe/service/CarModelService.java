package com.hdmbe.service;

import com.hdmbe.constant.FuelType;
import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.dto.CarModelSearchDto;
import com.hdmbe.dto.OperationPurposeResponseDto;
import com.hdmbe.entity.CarModel;
import com.hdmbe.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarModelService {

    private final CarModelRepository repository;

    @Transactional
    public CarModelResponseDto create(CarModelRequestDto dto) {

        CarModel saved = repository.save(
                CarModel.builder()
                        .categoryId(dto.getCategoryId())
                        .fuelType(dto.getFuelType())
                        .customEfficiency(dto.getCustomEfficiency())
                        .build()
        );
        return CarModelResponseDto.fromEntity(saved);
    }
    // 전체 조회
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> getAll() {
        return repository.findAll()
                .stream()
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }

    // 조건 검색
    @Transactional(readOnly = true)
    public List<CarModelResponseDto> search(CarModelSearchDto searchDto) {
        List<CarModel> list;

        switch (searchDto.getType()) {
            case "category", "subCategory":
                Long categoryId = Long.valueOf(searchDto.getKeyword());
                list = repository.findByCategoryId(categoryId);
                break;
            case "fuelType":
                FuelType fuelType = FuelType.valueOf(searchDto.getKeyword());
                list = repository.findByFuelType(fuelType);
                break;
            default:
                list = repository.findAll();
        }

        return list.stream()
                .map(CarModelResponseDto::fromEntity)
                .toList();
    }
}