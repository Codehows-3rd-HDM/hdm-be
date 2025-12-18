package com.hdmbe.carModel.dto;

import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelRequestDto {

    private Long id;

    private Long carCategoryId;
    private String carCategoryName;
    private FuelType fuelType;
    private BigDecimal customEfficiency;

    // 필터링 전체 검색 (All)
    private String keyword;

}
