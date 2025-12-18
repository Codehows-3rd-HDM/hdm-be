package com.hdmbe.supplyType.dto;

import com.hdmbe.supplyType.entity.SupplyType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyTypeResponseDto {
    private Long id;
    private String supplyTypeName;

    public static SupplyTypeResponseDto fromEntity(SupplyType entity) {
        return SupplyTypeResponseDto.builder()
                .id(entity.getId())
                .supplyTypeName(entity.getSupplyTypeName())
                .build();
    }
}
