package com.hdmbe.carModel.service;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carCategory.service.CarCategoryService;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.carModel.repository.CarModelRepository;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CarModelExcelService {
    private final CarModelRepository carModelRepository;
    private final CarCategoryService carCategoryService;    // 상위데이터의 서비스 호출

    @Transactional
    public CarModel getOrCreate(String bigCategoryName,
                                String smallCategoryName,
                                String fuelStr,
                                BigDecimal efficiency
                                )
    {
        // 1. [부모 찾기] 카테고리 서비스에게 없으면 만들어 오라고 시킴
        CarCategory category = carCategoryService.getOrCreate(bigCategoryName, smallCategoryName);

        // 2. [enum 변환] 엑셀의 글자 string 타입을 Enum 객체(FuelType.디젤)로 변경
        // (이름이 똑같으므로 바로 valueOf 사용 가능!)
        FuelType fuelType;
        try {
            fuelType = FuelType.valueOf(fuelStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("지원하지 않는 연료 타입입니다: " + fuelStr);
        }

        // 3. [차종 저장] (카테고리 + 연료) 조합으로 조회
        return  carModelRepository.findByCarCategoryAndFuelType(category, fuelType)
                .map(existing -> {
            // 이미 있으면 연비 정보 업데이트
            // 엑셀에 있는 연비가 최실일 수 있으니깐
            existing.setCustomEfficiency(efficiency);
            return  existing;
            })
                .orElseGet(() ->
                  // 없으면 신규 등록
                  carModelRepository.save(
                          carModelRepository.save(
                                  CarModel.builder()
                                          .carCategory(category)    //부모 연결
                                          .fuelType(fuelType)    // 연료 연결
                                          .customEfficiency(efficiency) // 연비 저장
                                          .build()
                          )
                        )
                );
    }
}
