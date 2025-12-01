package com.hdmbe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleSearchDto {

    private String carNumber;      // 차량번호
    private Long companyId;        // 회사 ID
    private String driverMemberId; // 운전자 ID
}
