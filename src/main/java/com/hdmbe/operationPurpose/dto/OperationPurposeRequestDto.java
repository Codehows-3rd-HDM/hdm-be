package com.hdmbe.operationPurpose.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeRequestDto {

    private String id;
    // 등록용
    private String purposeName;
    private Integer defaultScope;
    private Integer defaultScopeId;

    // 검색용
    private String purposeNameFilter;
    private Integer scopeFilter;

}
