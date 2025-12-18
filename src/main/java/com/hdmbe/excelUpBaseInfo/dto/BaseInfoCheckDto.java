package com.hdmbe.excelUpBaseInfo.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BaseInfoCheckDto {
    private Integer idx;
    private String carNumber;
    private String status;
    private String message;
}
