package com.hdmbe.carCategory.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategoryRequestDto {
    private String categoryId;
    private Long parentId;
}
