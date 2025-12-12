package com.hdmbe.excelUpBaseInfo.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExcelUpBaseInfoDto {

    // 1. 차종 분류
    private  String bigCategory;    // 차종 대분류
    private  String smallCategory;  // 차종 소분류

    // 2. 운행 목적
    private String purposeName;     // 운행목적
    private String scope;           // Scope 1, 3

    // 3. 공급 유형 (구 process)
    private String supplyTypeName;

    // 4. 공급 고객 (구 productclass)
    private String customerName;

    // 5. 배출 계수
    private String fuelName;
    private BigDecimal emissionFactor;
}
