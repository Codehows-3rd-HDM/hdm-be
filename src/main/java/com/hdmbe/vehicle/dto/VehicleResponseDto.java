package com.hdmbe.vehicle.dto;

import com.hdmbe.commonModule.constant.FuelType;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {
    // 차량
    private Long id;
    private String carNumber;
    // 운행목적
    private Long operationPurposeId;
    private String operationPurposeName;
    // scope
    private Integer defaultScope;
    // 협렵사
    private Long companyId;
    private String companyName;
    // 사원번호
    private String driverMemberId;
    // 편도거리
    private BigDecimal operationDistance;
    // 대분류
    private Long parentCategoryId;
    private String parentCategoryName;
    // 소분류
    private Long carCategoryId;
    private String carCategoryName;
    // 차량 모델
    private Long carModelId;
    private String carModelName;
    // 연료
    private FuelType fuelType;
    // 비고
    private String remark;

    public static VehicleResponseDto fromEntity(Vehicle vehicle,
        VehicleOperationPurposeMap purposeMap
    ) {
        var company = vehicle.getCompany();
        var carModel = vehicle.getCarModel();
        var carCategory = carModel.getCarCategory();
        var parentCategory = carCategory.getParentCategory();
        var purpose = purposeMap != null ? purposeMap.getOperationPurpose() : null;

        return VehicleResponseDto.builder()
                // 차량
                .id(vehicle.getId())
                .carNumber(vehicle.getCarNumber())

                // 운행 목적
                .operationPurposeId(
                        purpose != null ? purposeMap.getOperationPurpose().getId() : null
                )
                .operationPurposeName(
                        purpose != null ? purposeMap.getOperationPurpose().getPurposeName() : null
                )
                .defaultScope(
                        purpose != null ? purposeMap.getOperationPurpose().getDefaultScope() : null
                )

                // 협력사
                .companyId(company.getId())
                .companyName(company.getCompanyName())

                // 사원번호
                .driverMemberId(vehicle.getDriverMemberId())

                // 편도거리 
                .operationDistance(company.getOneWayDistance())

                // 대분류
                .parentCategoryId(
                        parentCategory != null ? parentCategory.getId() : null
                )
                .parentCategoryName(
                        parentCategory != null ? parentCategory.getCategoryName() : null
                )

                .carCategoryId(carCategory.getId())
                .carCategoryName(carCategory.getCategoryName())
                .carModelId(carModel.getId())
                .carModelName(vehicle.getCarName())
                .fuelType(carModel.getFuelType())
                .remark(vehicle.getRemark())
                .build();
    }
}

