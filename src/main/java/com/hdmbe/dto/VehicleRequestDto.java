package com.hdmbe.dto;

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

    private String carNumber;
    private String carName;
    private Long carModelId;
    private String driverMemberId;
    private Long companyId;
    private Long purposeId;
    private BigDecimal operationDistance;
    private String remark;
}
