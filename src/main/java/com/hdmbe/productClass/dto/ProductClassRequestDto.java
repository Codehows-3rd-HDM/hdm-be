package com.hdmbe.productClass.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductClassRequestDto {

    private Long id;

    // 등록용
    private String className;
    private String remark;

    // 검색용
    private String classNameFilter;
}
