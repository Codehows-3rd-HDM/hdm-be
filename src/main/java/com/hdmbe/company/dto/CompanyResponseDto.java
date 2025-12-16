package com.hdmbe.company.dto;

import com.hdmbe.company.entity.Company;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CompanyResponseDto {
    private Long id;
    private String companyName;
    private Long supplyTypeId;
    private String supplyTypeName;
    private BigDecimal oneWayDistance;
    private Long supplyCustomerId;
    private String supplyCustomerName;
    private String address;
    private String remark;

    public static CompanyResponseDto fromEntity(Company company) {
        return CompanyResponseDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
//                .supplyTypeId(company.getSupplyType().getId())
//                .supplyTypeName(company.getSupplyType().getSupplyTypeName())
                .oneWayDistance(company.getOneWayDistance())
//                .supplyCustomerId(company.getSupplyCustomer().getId())
//                .supplyCustomerName(company.getSupplyCustomer().getCustomerName())
                .address(company.getAddress())
                .remark(company.getRemark())
                .build();
    }
}
