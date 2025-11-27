package com.hdmbe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarModelSearchDto {

    private String type;      // 검색 기준: "all", "category", "subCategory", "fuelType"
    private String keyword;   // 검색 키워드 (차종, 소분류, 연료종류)
}
