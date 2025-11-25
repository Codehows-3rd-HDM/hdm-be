package com.hdmbe.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeRequestDto {

    private String purposeName;
    private Integer defaultScope;
}
