package com.hdmbe.dto;

import com.hdmbe.constant.FuelType;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CarModelRequestDto {

    private Long categoryId;
    private FuelType fuelType;
    private BigDecimal customEfficiency;
}
