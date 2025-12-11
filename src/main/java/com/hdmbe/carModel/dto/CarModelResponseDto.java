package com.hdmbe.carModel.dto;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CarModelResponseDto {

    private Long id;
    private String parentCategoryName;
    private String childCategoryName;
    private FuelType fuelType;
    private BigDecimal customEfficiency;

    public static CarModelResponseDto fromEntity(CarModel model) {
        CarCategory child = model.getCarCategory();
        CarCategory parent = child.getParentCategory();

        return CarModelResponseDto.builder()
                .id(model.getId())
                .parentCategoryName(parent != null ? parent.getCategoryName() : null)
                .childCategoryName(child.getCategoryName())
                .fuelType(model.getFuelType())
                .customEfficiency(model.getCustomEfficiency())
                .build();
    }

}
