package com.hdmbe.SupplyCustomer.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyCustomerRequestDto {

    private Long id;

    // 등록용
    private String customerName;
    private String remark;

    // 검색용
    private String customerNameFilter;
}
