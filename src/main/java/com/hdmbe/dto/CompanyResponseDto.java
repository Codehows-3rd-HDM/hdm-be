package com.hdmbe.dto;

import com.hdmbe.entity.Company;
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

    public static CompanyResponseDto fromEntity(Company c) {
        return CompanyResponseDto.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .oneWayDistance(c.getOneWayDistance())
                .address(c.getAddress())
                .processName(c.getProcess() != null ? c.getProcess().getProcessName() : null)
                .productClassName(c.getProductClass() != null ? c.getProductClass().getClassName() : null)
                .remark(c.getRemark())
                .build();
    }
}
