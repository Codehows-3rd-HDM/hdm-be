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

    // CREATE / UPDATE / SEARCH 공통
    private Long id;
    private Long categoryId;    // CarCategory 연관관계 위해 필요
    private FuelType fuelType;
    private BigDecimal customEfficiency;

    // 상위 카테고리명 검색
    private String CategoryName;

    // 하위 카테고리명 검색
    private String childCategoryName;

    // 필터링 전체 검색 (All)
    private String keyword;



}
