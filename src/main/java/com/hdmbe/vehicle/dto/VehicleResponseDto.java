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
    // 모델명
    private String carName;
    // 사원번호
    private String driverMemberId;
    // 협렵사
    private String companyName;
    // 협력사 Id
    private Long companyId;
    // 운행목적
    private String operationPurposeName;
    // 운행목적 Id
    private Long purposeId;
    // scope
    private Integer defaultScope;
    // 편도거리
    private BigDecimal operationDistance;
    // 대분류
    private String parentCategoryName;
    // 소분류
    private String carCategoryName;
    // 소분류 ID
    private Long carCategoryId;
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

        BigDecimal effectiveDistance =
                vehicle.getOperationDistance() != null
                        ? vehicle.getOperationDistance()
                        : company.getOneWayDistance();

        return VehicleResponseDto.builder()
                .id(vehicle.getId())
                .carNumber(vehicle.getCarNumber())
                .carName(vehicle.getCarName())
                .driverMemberId(vehicle.getDriverMemberId())
                .companyName(company.getCompanyName())
                .companyId(company.getId())
                .operationPurposeName(purpose != null ? purpose.getPurposeName() : null)
                .purposeId(purpose != null ? purpose.getId() : null)
                .defaultScope(purpose != null ? purpose.getDefaultScope() : null)
                .operationDistance(effectiveDistance)
                .parentCategoryName(parentCategory != null ? parentCategory.getCategoryName() : null)
                .carCategoryName(carCategory.getCategoryName())
                .carCategoryId(carCategory.getId())
                .fuelType(carModel.getFuelType())
                .remark(vehicle.getRemark())
                .build();
    }
}

