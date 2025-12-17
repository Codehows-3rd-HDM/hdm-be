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

    public static CarModelResponseDto fromEntity(CarModel cm) {
        return CarModelResponseDto.builder()
                .id(cm.getId())

                .parentCategoryId(
                        cm.getCarCategory().getParentCategory() != null
                                ? cm.getCarCategory().getParentCategory().getId()
                                : null
                )
                .parentCategoryName(
                        cm.getCarCategory().getParentCategory() != null
                                ? cm.getCarCategory().getParentCategory().getCategoryName()
                                : null
                )

                .carCategoryId(cm.getCarCategory().getId())
                .carCategoryName(cm.getCarCategory().getCategoryName())

                .fuelType(cm.getFuelType())
                .customEfficiency(cm.getCustomEfficiency())
                .build();
    }
}
