package com.hdmbe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeRequestDto {

    private String purposeName;
    private Integer defaultScope;
}
