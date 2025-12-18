package com.hdmbe.carModel.dto;

import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CarModelResponseDto {

    private Long id;

    private Long carCategoryId;
    private String carCategoryName;
    private Long parentCategoryId;
    private String parentCategoryName;
    private FuelType fuelType;
    private BigDecimal customEfficiency;

    public static CarModelResponseDto fromEntity(CarModel carModel) {
        return CarModelResponseDto.builder()
                .id(carModel.getId())

                .parentCategoryId(
                        carModel.getCarCategory().getParentCategory() != null
                                ? carModel.getCarCategory().getParentCategory().getId()
                                : null
                )
                .parentCategoryName(
                        carModel.getCarCategory().getParentCategory() != null
                                ? carModel.getCarCategory().getParentCategory().getCategoryName()
                                : null
                )

                .carCategoryId(carModel.getCarCategory().getId())
                .carCategoryName(carModel.getCarCategory().getCategoryName())

                .fuelType(carModel.getFuelType())
                .customEfficiency(carModel.getCustomEfficiency())
                .build();
    }
}
