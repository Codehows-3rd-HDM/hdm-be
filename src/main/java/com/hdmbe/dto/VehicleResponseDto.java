package com.hdmbe.dto;

import com.hdmbe.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VehicleResponseDto {

    private Long id;
    private String carNumber;
    private String carName;
    private Long carModelId;
    private String driverMemberId;
    private Long companyId;
    private Long purposeId;
    private BigDecimal operationDistance;
    private String remark;

    public static VehicleResponseDto fromEntity(Vehicle entity) {
        return VehicleResponseDto.builder()
                .id(entity.getId())
                .carNumber(entity.getCarNumber())
                .carName(entity.getCarName())
                .carModelId(entity.getCarModelId())
                .driverMemberId(entity.getDriverMemberId())
                .companyId(entity.getCompanyId())
                .purposeId(entity.getPurposeId())
                .operationDistance(entity.getOperationDistance())
                .remark(entity.getRemark())
                .build();
    }
}
