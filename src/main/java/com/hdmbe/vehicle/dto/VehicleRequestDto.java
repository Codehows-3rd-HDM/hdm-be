package com.hdmbe.vehicle.dto;

import com.hdmbe.commonModule.constant.FuelType;
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

    // 등록용 (이름 기반) - 프론트엔드에서 이름으로 보낼 때 사용
    private String purposeName; // 운행목적 이름
    private String companyNameForCreation; // 업체 이름 (생성용)

    // 검색용
    private String keyword;              // 통합검색
    private String carNumberFilter;      // 차량번호 검색
    private String companyNameFilter;    // 업체명 검색
    private String driverMemberIdFilter; // 사원번호 검색
}
