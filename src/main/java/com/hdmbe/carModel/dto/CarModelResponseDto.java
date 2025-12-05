package com.hdmbe.carModel.dto;

import com.hdmbe.carModel.entity.CarModel;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CarModelResponseDto {

    private Long id;
    private String categoryName;
    private String fuelType;
    private BigDecimal customEfficiency;

    public static CarModelResponseDto fromEntity(CarModel model) {
        return CarModelResponseDto.builder()
                .id(model.getId())
                .categoryName(model.getCarCategory().getCategoryName())
                .fuelType(model.getFuelType().name())
                .customEfficiency(model.getCustomEfficiency())
                .build();
    }

}
