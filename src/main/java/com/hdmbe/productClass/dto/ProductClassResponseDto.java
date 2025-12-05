package com.hdmbe.productClass.dto;

import com.hdmbe.productClass.entity.ProductClass;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
