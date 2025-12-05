package com.hdmbe.vehicle.dto;

import com.hdmbe.constant.FuelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleRequestDto {

    private Long id;

    // 등록용
    private String carNumber;
    private Long operationPurposeId;
    private Long companyId;
    private String driverMemberId;
    private BigDecimal operationDistance;
    private Long carCategoryId;
    private Long carModelId;
    private String carName;
    private FuelType fuelType;
    private String remark;

    // 검색용
    private String keyword;              // 통합검색
    private String carNumberFilter;      // 차량번호 검색
    private String companyNameFilter;    // 업체명 검색
    private String driverMemberIdFilter; // 사원번호 검색
}