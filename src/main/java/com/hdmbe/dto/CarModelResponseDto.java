package com.hdmbe.dto;

import com.hdmbe.entity.CarModel;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CarModelResponseDto {
    private Long id;
    private String categoryName;
    private String fuelType;
    private BigDecimal customEfficiency;

    public static CarModelResponseDto fromEntity(CarModel cm) {
        return CarModelResponseDto.builder()
                .id(cm.getId())
                .categoryName(cm.getCarCategory() != null ? cm.getCarCategory().getCategoryName() : null)
                .fuelType(cm.getFuelType() != null ? cm.getFuelType().name() : null)
                .customEfficiency(cm.getCustomEfficiency())
                .build();
    }

}
