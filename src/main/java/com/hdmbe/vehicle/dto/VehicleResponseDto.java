package com.hdmbe.vehicle.dto;

import com.hdmbe.vehicle.entity.Vehicle;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponseDto {

    private Long id;
    private String carNumber;
    private String operationPurposeName;
    private Integer scope;
    private String companyName;
    private String driverMemberId;
    private BigDecimal operationDistance;
    private String remark;

    public static VehicleResponseDto fromEntity(Vehicle vehicle) {
        return VehicleResponseDto.builder()
                .id(vehicle.getId())
                .carNumber(vehicle.getCarNumber())
                .operationPurposeName(vehicle.getOperationPurpose().getPurposeName())
                .scope(vehicle.getOperationPurpose().getDefaultScope())
                .companyName(vehicle.getCompany().getCompanyName())
                .driverMemberId(vehicle.getDriverMemberId())
                .operationDistance(vehicle.getOperationDistance())
                .remark(vehicle.getRemark())
                .build();
    }

}

