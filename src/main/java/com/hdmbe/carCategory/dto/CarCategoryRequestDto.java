package com.hdmbe.carCategory.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategoryRequestDto {
    private String categoryName;
    private Long parentId;
}
