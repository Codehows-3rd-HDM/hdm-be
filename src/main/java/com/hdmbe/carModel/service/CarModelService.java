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
        CarCategory category = carCategoryRepository.findById(dto.getCarCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));

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
        System.out.println("[CarModelService] 차종 검색 요청 - parentCategoryId: " + parentCategoryId
                + ", carCategoryId: " + carCategoryId + ", fuelType: " + fuelType
                + ", keyword: " + keyword + ", page: " + page + ", size: " + size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<CarModel> result = carModelRepository.search(
                parentCategoryId,
                carCategoryId,
                fuelType,
                keyword,
                pageable
        );

        System.out.println("[CarModelService] 차종 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(CarModelResponseDto::fromEntity);
    }

    // 단일 수정
    @Transactional
    public CarModelResponseDto updateSingle(Long id, CarModelRequestDto dto) {
        validateUpdate(dto);

        if (dto.getCarCategoryId() == null) {
            throw new IllegalArgumentException("carCategoryId 필수");
        }

        CarModel model = carModelRepository.findByCarCategoryId(dto.getCarCategoryId())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "차종 없음 (carCategoryId=" + dto.getCarCategoryId() + ")"));


        if (dto.getFuelType() != null) {
            model.setFuelType(dto.getFuelType());
        }
        if (dto.getCustomEfficiency() != null) {
            model.setCustomEfficiency(dto.getCustomEfficiency());
        }

        return CarModelResponseDto.fromEntity(model);
    }

    // 전체 수정
    @Transactional
    public List<CarModelResponseDto> updateMultiple(List<CarModelRequestDto> dtoList) {
        return dtoList.stream()
                .map(dto -> {

                    if (dto.getCarCategoryId() == null) {
                        throw new IllegalArgumentException("carCategoryId 필수");
                    }

                    CarModel model = carModelRepository.findByCarCategoryId(dto.getCarCategoryId())
                            .orElseThrow(() ->
                                    new EntityNotFoundException(
                                            "차종 없음 (carCategoryId=" + dto.getCarCategoryId() + ")"));

                    if (dto.getFuelType() != null) {
                        model.setFuelType(dto.getFuelType());
                    }

                    if (dto.getCustomEfficiency() != null) {
                        model.setCustomEfficiency(dto.getCustomEfficiency());
                    }

                    return CarModelResponseDto.fromEntity(model);
                })
                .collect(Collectors.toList());
    }

    // 필수값 검증
    private void validateCreate(CarModelRequestDto dto) {
        if (dto.getCarCategoryId() == null) {
            throw new IllegalArgumentException("카테고리Id 필수");
        }
        if (dto.getFuelType() == null) {
            throw new IllegalArgumentException("연료종류 필수");
        }
        if (dto.getCustomEfficiency() == null) {
            throw new IllegalArgumentException("연비 필수");
        }
    }

    private void validateUpdate(CarModelRequestDto dto) {
        if (dto.getCarCategoryId() != null && dto.getCarCategoryId() <= 0) {
            throw new IllegalArgumentException("카테고리Id 유효하지 않음");
        }
    }
    private void validateDelete(CarModelRequestDto dto) {

    }
}
