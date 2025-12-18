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
    private Long supplyTypeId; // supplyType 연관
    private BigDecimal oneWayDistance;
    private Long customerId; // customerEntity 연관
    // 지역
    private String region;
    // 상세 주소
    private String detailAddress;
    private String remark;

    //주소
    private String address;

    // 등록용 (이름 기반) - 프론트엔드에서 이름으로 보낼 때 사용
    private String supplyTypeName; // 공급 유형 이름
    private String customerName; // 공급 고객 이름

}

