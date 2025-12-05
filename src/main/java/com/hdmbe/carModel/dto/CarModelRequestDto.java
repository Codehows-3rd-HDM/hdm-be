package com.hdmbe.carModel.dto;

import com.hdmbe.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelRequestDto {

    // CREATE / UPDATE / SEARCH 공통
    private Long id;
    private Long categoryId;    // CarCategory 연관관계 위해 필요
    private String categoryName;
    private FuelType fuelType;

    // CREATE / UPDATE 전용
    private BigDecimal customEfficiency;

    private String keyword;



}
