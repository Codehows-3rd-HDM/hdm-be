package com.hdmbe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanySearchDto {
    private String companyName;   // 업체명
    private Long processId;       // 생산공정
    private Long productClassId;  // 납품구분
    private String address;       // 주소
}
