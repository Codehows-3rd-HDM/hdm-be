package com.hdmbe.inquiry.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter     // GET 파라미터 바인딩을 위해
@ToString
public class ViewCompanyRequestDto {

    private Integer year; // 기본값 설정 (명세서 참고)
    private Integer month = 0;   // 0=전체, 값이 있으면 해당 월
    private String scope;        // 필터링 구분
    private String keyword;      // 검색어 (회사명, 주소 등)

}
