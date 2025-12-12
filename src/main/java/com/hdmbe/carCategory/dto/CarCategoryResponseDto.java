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
    private String categoryName;
    private Long parentId;
    private String parentCategoryName;
    private String childCategoryName;

    public static CarCategoryResponseDto fromEntity(CarCategory category) {
        String parentCategoryName = null;
        String childCategoryName = null;

        // 부모가 없으면 이 카테고리는 대분류 (parentCategoryName)
        if (category.getParentCategory() == null) {
            parentCategoryName = category.getCategoryName();
        } else {
            // 부모가 있으면 이 카테고리는 소분류 (childCategoryName)
            parentCategoryName = category.getParentCategory().getCategoryName();
            childCategoryName = category.getCategoryName();
        }

        return CarCategoryResponseDto.builder()
                .id(category.getId())
                .categoryName(category.getCategoryName())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .parentCategoryName(parentCategoryName)
                .childCategoryName(childCategoryName)
                .build();
    }
}
