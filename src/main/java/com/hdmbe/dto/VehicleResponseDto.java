package com.hdmbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
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
}
