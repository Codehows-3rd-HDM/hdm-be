package com.hdmbe.dto;

import com.hdmbe.entity.ProductClass;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ProductClassResponseDto {
    private Long id;
    private String className;
    private String remark;

    public static ProductClassResponseDto fromEntity(ProductClass pc) {
        return ProductClassResponseDto.builder()
                .id(pc.getId())
                .className(pc.getClassName())
                .remark(pc.getRemark())
                .build();
    }
}
