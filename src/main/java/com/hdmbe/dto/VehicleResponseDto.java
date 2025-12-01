package com.hdmbe.dto;

import com.hdmbe.entity.Vehicle;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleResponseDto {
    private Long id;
    private String carNumber;
    private String carName;
    private String carModelName;
    private String driverMemberId;
    private String companyName;
    private String purposeName;
    private BigDecimal operationDistance;
    private String remark;

    public static VehicleResponseDto fromEntity(Vehicle v) {
        return VehicleResponseDto.builder()
                .id(v.getId())
                .carNumber(v.getCarNumber())
                .carName(v.getCarName())
                .driverMemberId(v.getDriverMemberId())
                .companyName(v.getCompany() != null ? v.getCompany().getCompanyName() : null)
                .purposeName(v.getOperationPurpose() != null ? v.getOperationPurpose().getPurposeName() : null)
                .operationDistance(v.getOperationDistance())
                .remark(v.getRemark())
                .build();
    }
}
