package com.hdmbe.SupplyCustomer.dto;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SupplyCustomerResponseDto {
    private Long id;
    private String customerName;
    private String remark;

    public static SupplyCustomerResponseDto fromEntity(SupplyCustomer entity) {
        return SupplyCustomerResponseDto.builder()
                .id(entity.getId())
                .customerName(entity.getCustomerName())
                .remark(entity.getRemark())
                .build();
    }
}
