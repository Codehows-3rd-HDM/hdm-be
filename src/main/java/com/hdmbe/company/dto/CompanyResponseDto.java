package com.hdmbe.company.dto;

import java.math.BigDecimal;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.company.entity.Company;
import com.hdmbe.supplyType.entity.SupplyType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
        // 현재 유효한 공급유형 매핑 조회 (endDate가 null이거나 미래인 것)
        SupplyType currentSupplyType = company.getCurrentSupplyType();
        SupplyCustomer currentSupplyCustomer = company.getCurrentSupplyCustomer();

        return CompanyResponseDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .supplyTypeId(currentSupplyType != null ? currentSupplyType.getId() : null)
                .supplyTypeName(currentSupplyType != null ? currentSupplyType.getSupplyTypeName() : null)
                .oneWayDistance(company.getOneWayDistance())
                .supplyCustomerId(currentSupplyCustomer != null ? currentSupplyCustomer.getId() : null)
                .supplyCustomerName(currentSupplyCustomer != null ? currentSupplyCustomer.getCustomerName() : null)
                .address(company.getAddress())
                .remark(company.getRemark())
                .build();
    }
}
