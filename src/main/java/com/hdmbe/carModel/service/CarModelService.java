package com.hdmbe.carModel.service;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carCategory.repository.CarCategoryRepository;
import com.hdmbe.carModel.dto.CarModelRequestDto;
import com.hdmbe.carModel.dto.CarModelResponseDto;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carModel.repository.CarModelRepository;
import com.hdmbe.commonModule.constant.FuelType;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class CarModelService {

    private final CarModelRepository carModelRepository;
    private final CarCategoryRepository carCategoryRepository;

    // 등록
    @Transactional
    public CarModelResponseDto createCarModel(CarModelRequestDto dto) {
        validateCreate(dto);
        CarCategory category = carCategoryRepository.findByCategoryName(dto.getCarCategoryName())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

        // CarCategory category = carCategoryRepository.findById(dto.getCarCategoryId())
        // .orElseThrow(() -> new EntityNotFoundException("CarCategory not found id=" +
        // dto.getCarCategoryId()));

        CarModel model = CarModel.builder()
                .carCategory(category)
                .fuelType(dto.getFuelType())
                .customEfficiency(dto.getCustomEfficiency())
                .build();

        carModelRepository.save(model);
        return CarModelResponseDto.fromEntity(model);
    }

    // 조회, 검색
    @Transactional(readOnly = true)
    public Page<CarModelResponseDto> search(
            Long parentCategoryId,
            Long carCategoryId,
            FuelType fuelType,
            String keyword,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return carModelRepository.search(
                        parentCategoryId,
                        carCategoryId,
                        fuelType,
                        keyword,
                        pageable
                )
                .map(CarModelResponseDto::fromEntity);
    }

    // 단일 수정
    @Transactional
    public CarModelResponseDto updateSingle(Long id, CarModelRequestDto dto) {
        validateUpdate(dto);

        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("차종 id 없음 =" + id));

        if (dto.getCarCategoryId() != null) {
            CarCategory category = carCategoryRepository.findById(dto.getCarCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리 id 없음 =" + dto.getCarCategoryId()));
            model.setCarCategory(category);
        }

        if (dto.getFuelType() != null)
            model.setFuelType(dto.getFuelType());
        if (dto.getCustomEfficiency() != null)
            model.setCustomEfficiency(dto.getCustomEfficiency());

        return CarModelResponseDto.fromEntity(model);
    }

    // 전체 수정
    @Transactional
    public List<CarModelResponseDto> updateMultiple(List<CarModelRequestDto> dtoList) {
        return dtoList.stream()
                .map(dto -> updateSingle(dto.getId(), dto))
                .collect(Collectors.toList());
    }

    // 삭제
    @Transactional
    public void deleteCarModel(Long id) {
        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("차종 id 없음 =" + id));
        carModelRepository.delete(model);
    }

    // 필수값 검증
    private void validateCreate(CarModelRequestDto dto) {
        if (dto.getCarCategoryId() == null)
            throw new IllegalArgumentException("카테고리Id 필수");
        if (dto.getFuelType() == null)
            throw new IllegalArgumentException("연료종류 필수");
        if (dto.getCustomEfficiency() == null)
            throw new IllegalArgumentException("연비 필수");
    }

    private void validateUpdate(CarModelRequestDto dto) {
        if (dto.getCarCategoryId() != null && dto.getCarCategoryId() <= 0)
            throw new IllegalArgumentException("카테고리Id 유효하지 않음");
    }
}
