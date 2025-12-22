package com.hdmbe.vehicle.dto;

import java.math.BigDecimal;

import com.hdmbe.commonModule.constant.FuelType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleRequestDto {

    private Long id;

    // 차량번호
    private String carNumber;
    // 운행목적 ID
    private Long purposeId;
    // 협력사 ID
    private Long companyId;
    // 사원번호
    private String driverMemberId;
    // 편도 거리
    private BigDecimal operationDistance;
    // 차종 대분류 ID
    private Long parentCategoryId;
    // 차종 소분류 ID
    private Long carCategoryId;
    // 차종 ID
    private Long carModelId;
    // 차량 모델명
    private String carName;
    // 연료 종류
    private FuelType fuelType;
    // 비고
    private String remark;

    // 등록용 (이름 기반) - 프론트엔드에서 이름으로 보낼 때 사용
    private String purposeName; // 운행목적 이름
    private String companyNameForCreation; // 업체 이름 (생성용)

    // 등록용 (ID 기반)
    private Long childCategoryId; // 소분류 카테고리 ID

}
