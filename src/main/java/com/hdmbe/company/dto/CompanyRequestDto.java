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

    // 등록용
    private String companyName;
    private BigDecimal oneWayDistance;
    private String address;
    private Long processId;      // ProcessEntity 연관
    private Long productClassId; // ProductClass 연관
    private String remark;

    // 검색용
    private String keyword; // all
    private String companyNameFilter; // 업체명
    private Long processIdFilter;     // 공정
    private Long productClassIdFilter; // 납품구분
    private String addressFilter;      // 주소
}
