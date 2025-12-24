package com.hdmbe.operationPurpose.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeRequestDto {

    private Long id;
    // 등록용
    private String purposeName;
    private Integer defaultScopeId;

}
