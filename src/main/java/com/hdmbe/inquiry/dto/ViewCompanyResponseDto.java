package com.hdmbe.inquiry.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ViewCompanyResponseDto {

    private Long id;
    private String companyName;
    private String address;
    private BigDecimal totalEmission;
    private Double ratio = 0.0;

    // JPQL이 이 생성자를 호출해서 객체를 만듭니다.
    // 순서: id, name, address, sum, 0.0
    public ViewCompanyResponseDto(Long id, String companyName, String address, BigDecimal totalEmission) {
        this.id = id;
        this.companyName = companyName;
        this.address = address;
        this.totalEmission = totalEmission;
        // this.ratio는 위에서 이미 0.0으로 초기화됨
        //this.ratio = ratio;
    }
}
