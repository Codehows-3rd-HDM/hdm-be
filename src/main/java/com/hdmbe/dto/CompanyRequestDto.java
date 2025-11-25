package com.hdmbe.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CompanyRequestDto {

    private String companyName;
    private BigDecimal oneWayDistance;
    private String address;
    private Long processId;
    private Long classId;
    private String remark;
}
