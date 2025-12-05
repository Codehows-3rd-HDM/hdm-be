package com.hdmbe.operationPurpose.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeRequestDto {

    // 등록용
    private String purposeName;
    private Integer defaultScope;

    // 검색용
    private String purposeNameFilter;
    private Integer scopeFilter;

}
