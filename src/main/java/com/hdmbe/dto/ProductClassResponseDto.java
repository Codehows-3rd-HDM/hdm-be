package com.hdmbe.dto;

import com.hdmbe.entity.ProductClass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ProductClassResponseDto {

    private Long id;
    private String className;
    private String remark;

    public static ProductClassResponseDto fromEntity(ProductClass entity) {
        return ProductClassResponseDto.builder()
                .id(entity.getId())
                .className(entity.getClassName())
                .remark(entity.getRemark())
                .build();
    }
}
