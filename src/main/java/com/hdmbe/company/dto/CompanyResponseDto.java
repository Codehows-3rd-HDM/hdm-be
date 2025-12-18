package com.hdmbe.company.dto;

import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
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
    private String region;
    private String detailAddress;
    private String address;
    private String remark;

    public static CompanyResponseDto fromEntity(
            Company company,
            CompanySupplyTypeMap supplyTypeMap,
            CompanySupplyCustomerMap supplyCustomerMap
    ) {
        String address = company.getAddress();

        String region = null;
        String detailAddress = null;

        if (address != null && address.contains(" ")) {
            int idx = address.indexOf(" ");
            region = address.substring(0, idx);
            detailAddress = address.substring(idx + 1);
        } else {
            region = address;
        }

        return CompanyResponseDto.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())

                // 공급 유형
                .supplyTypeId(
                        supplyTypeMap != null
                                ? supplyTypeMap.getSupplyType().getId()
                                : null
                )
                .supplyTypeName(
                        supplyTypeMap != null
                                ? supplyTypeMap.getSupplyType().getSupplyTypeName()
                                : null
                )

                // 편도 거리
                .oneWayDistance(company.getOneWayDistance())

                // 공급 고객
                .supplyCustomerId(
                        supplyCustomerMap != null
                                ? supplyCustomerMap.getSupplyCustomer().getId()
                                : null
                )
                .supplyCustomerName(
                        supplyCustomerMap != null
                                ? supplyCustomerMap.getSupplyCustomer().getCustomerName()
                                : null
                )

                // 주소 (표현용 분리)
                .region(region)
                .detailAddress(detailAddress)

                .remark(company.getRemark())
                .build();
    }
}