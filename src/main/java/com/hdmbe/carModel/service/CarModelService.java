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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    // 조회, 검색
    @Transactional(readOnly = true)
    public Page<CarModelResponseDto> findAll(CarModelRequestDto dto, Pageable pageable) {

        Page<CarModel> page = carModelRepository.search(
                dto.getKeyword(),
                dto.getParentCategoryName(),
                dto.getChildCategoryName(),
                dto.getFuelType(),
                pageable
        );

        return page.map(CarModelResponseDto::fromEntity);
    }


    // 단일 수정
    @Transactional
    public CarModelResponseDto updateOne(Long id, CarModelRequestDto dto) {

        CarModel model = carModelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("수정할 차량 모델을 찾을 수 없습니다."));

        if (dto.getCategoryId() != null) {
            CarCategory category = carCategoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다."));
            model.setCarCategory(category);
        }

        if (dto.getFuelType() != null) {
            model.setFuelType(dto.getFuelType());
        }

        if (dto.getCustomEfficiency() != null) {
            model.setCustomEfficiency(dto.getCustomEfficiency());
        }

        return CarModelResponseDto.fromEntity(model);
    }
    // 전체 수정
//    @Transactional
//    public List<CarModelResponseDto> updateBulk(List<CarModelRequestDto> requestList) {
//
//        List<CarModelResponseDto> responses = new ArrayList<>();
//
//        for (CarModelRequestDto req : requestList) {
//
//            CarModel model = carModelRepository.findById(req.getId())
//                    .orElseThrow(() -> new EntityNotFoundException("없는 carModel ID: " + req.getId()));
//
//            // --- 🔥 중요: null-safe set 로직 시작 ---
//            if (req.getFuelType() != null) {
//                model.setFuelType(req.getFuelType());
//            }
//
//            if (req.getCustomEfficiency() != null) {
//                model.setCustomEfficiency(req.getCustomEfficiency());
//            }
//
//            if (req.getChildCategoryId() != null) {
//                CarCategory newChild = categoryRepository.findById(req.getChildCategoryId())
//                        .orElseThrow(() -> new EntityNotFoundException("없는 카테고리 ID: " + req.getChildCategoryId()));
//                model.setCarCategory(newChild);
//            }
//            // --- 🔥 null-safe set 로직 끝 ---
//
//            responses.add(CarModelResponseDto.fromEntity(model));
//        }
//
//        return responses;
//    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        if (!carModelRepository.existsById(id)) {
            throw new EntityNotFoundException("삭제할 차량 모델을 찾을 수 없습니다.");
        }
        carModelRepository.deleteById(id);
    }

}
