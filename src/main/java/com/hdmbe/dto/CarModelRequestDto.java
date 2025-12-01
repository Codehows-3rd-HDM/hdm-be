package com.hdmbe.dto;

import com.hdmbe.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelRequestDto {
    private Long categoryId;          // CarCategory 연관관계 위해 필요
    private FuelType fuelType;
    private BigDecimal customEfficiency;

}
