package com.hdmbe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeResponseDto {

    private Long id;
    private String purposeName;
    private Integer defaultScope;
}
