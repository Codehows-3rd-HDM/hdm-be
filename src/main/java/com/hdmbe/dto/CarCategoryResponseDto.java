package com.hdmbe.dto;

import com.hdmbe.entity.CarCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategoryResponseDto {
    private Long id;
    private String categoryName;
    private Long parentId;

    public static CarCategoryResponseDto fromEntity(CarCategory category) {
        return CarCategoryResponseDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .build();
    }
}
