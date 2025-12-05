package com.hdmbe.process.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessRequestDto {

    // 등록용
    private Long id;
    private String processName;

    // 검색용
    private String processNameFilter;

}
