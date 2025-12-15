package com.hdmbe.company.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyRequestDto {

    private Long id;

    // 등록, 수정
    private String companyName;
    private Long supplyTypeId;
    private BigDecimal oneWayDistance;
    private Long supplyCustomerId;
    private String address;
    private String remark;

    // 검색용
    private String keyword; // all

}
