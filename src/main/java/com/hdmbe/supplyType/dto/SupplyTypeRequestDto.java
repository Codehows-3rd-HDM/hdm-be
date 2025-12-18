package com.hdmbe.supplyType.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplyTypeRequestDto {

    // 등록용
    private Long id;
    private String supplyTypeName;


}
