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
    private Long customerId;      // customerEntity 연관
    private Long supplyTypeId;    // supplyType 연관
    private String remark;

    // 등록용 (이름 기반) - 프론트엔드에서 이름으로 보낼 때 사용
    private String supplyTypeName;    // 공급 유형 이름
    private String customerName;      // 공급 고객 이름

    // 검색용
    private String keyword; // all
    private String companyNameFilter; // 업체명
    private Long customerIdFilter;     // 공정
    private Long supplyTypeIdFilter;   // 납품구분
    private String addressFilter;      // 주소
}
