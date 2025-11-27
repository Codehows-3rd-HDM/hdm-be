package com.hdmbe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class OperationPurposeSearchDto {
    private String type;    // 검색 조건 구분
    private String keyword; // 목적명 검색값
    private Integer scope;  // Scope 검색값

}
