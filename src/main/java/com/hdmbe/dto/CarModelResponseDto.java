package com.hdmbe.dto;

import com.hdmbe.constant.FuelType;
import com.hdmbe.entity.CarModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarModelResponseDto {

    private Long id;
    private Long categoryId;
    private FuelType fuelType;
    private BigDecimal customEfficiency;

    public static CarModelResponseDto fromEntity(CarModel entity) {
        return CarModelResponseDto.builder()
                .id(entity.getId())
                .categoryId(entity.getCategoryId())
                .fuelType(entity.getFuelType())
                .customEfficiency(entity.getCustomEfficiency())
                .build();
        }
}
