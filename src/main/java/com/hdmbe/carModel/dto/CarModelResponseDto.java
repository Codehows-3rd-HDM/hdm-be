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

    public static CarModelResponseDto fromEntity(CarModel model) {
       CarModelResponseDto dto = CarModelResponseDto.builder()
                .id(model.getId())
                .carCategoryId(model.getCarCategory().getId())
                .carCategoryName(model.getCarCategory().getCategoryName())
                .fuelType(model.getFuelType())
                .customEfficiency(model.getCustomEfficiency())
                .build();

        if (model.getCarCategory().getParentCategory() != null) {
            dto.setParentCategoryId(model.getCarCategory().getParentCategory().getId());
            dto.setParentCategoryName(model.getCarCategory().getParentCategory().getCategoryName());
        }

        return dto;
    }

}
