package com.hdmbe.company.dto;

import com.hdmbe.company.entity.Company;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyResponseDto {
    private Long id;
    private String companyName;
    private BigDecimal oneWayDistance;
    private String address;
    private String processName;
    private String productClassName;
    private String remark;

    public static CompanyResponseDto fromEntity(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .oneWayDistance(company.getOneWayDistance())
                .address(company.getAddress())
                .processName(company.getProcess().getProcessName())
                .productClassName(company.getProductClass().getClassName())
                .remark(company.getRemark())
                .build();
    }
}
