package com.hdmbe.dto;

import com.hdmbe.constant.FuelType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CarModelResponseDto {

    private Long id;
    private Long categoryId;
    private FuelType fuelType;
    private BigDecimal customEfficiency;
}
