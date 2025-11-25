package com.hdmbe.service;

import com.hdmbe.dto.CarModelRequestDto;
import com.hdmbe.dto.CarModelResponseDto;
import com.hdmbe.entity.CarModel;
import com.hdmbe.repository.CarModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return new CarModelResponseDto(
                saved.getId(),
                saved.getCategoryId(),
                saved.getFuelType(),
                saved.getCustomEfficiency()
        );
    }
}
