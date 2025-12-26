package com.hdmbe.fuelTypeAnalysis.dto;

import com.hdmbe.commonModule.constant.FuelType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuelTypeRequestDto {

    // 조회 기준 연도
    private Integer year;
    // 조회 기준 월
    private Integer month;
    // Scope 1,3,기타
    private Integer scope;
    // 연료타입
    private FuelType fuelType;

}
