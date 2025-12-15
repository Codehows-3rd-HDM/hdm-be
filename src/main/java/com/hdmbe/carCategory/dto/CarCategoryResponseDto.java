package com.hdmbe.carCategory.dto;

import com.hdmbe.carCategory.entity.CarCategory;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarCategoryResponseDto {
    private Long id;
    private String categoryId;
    private Long parentId;
    private String parentName;

    public static CarCategoryResponseDto fromEntity(CarCategory category) {
        return CarCategoryResponseDto.builder()
                .id(category.getId())
                .categoryId(category.getCategoryName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentName(category.getParentCategory() != null ? category.getParentCategory().getCategoryName() : null)
                .build();

    }
}
