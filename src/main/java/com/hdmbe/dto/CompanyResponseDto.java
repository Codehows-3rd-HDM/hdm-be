package com.hdmbe.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CompanyResponseDto {

    private Long id;
    private String companyName;
    private BigDecimal oneWayDistance;
    private String address;
    private Long processId;
    private Long classId;
    private String remark;
}
