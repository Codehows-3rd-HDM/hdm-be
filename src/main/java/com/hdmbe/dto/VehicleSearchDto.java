package com.hdmbe.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehicleSearchDto {

    private String type;    // 검색 기준: 전체, 차량번호, 업체명ID, 사원번호
    private String keyword; // 검색어
}
