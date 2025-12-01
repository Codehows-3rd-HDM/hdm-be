package com.hdmbe.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDto {
    private String companyName;
    private BigDecimal oneWayDistance;
    private String address;
    private Long processId;      // ProcessEntity 연관
    private Long productClassId; // ProductClass 연관
    private String remark;
}
