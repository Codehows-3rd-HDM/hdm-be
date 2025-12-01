package com.hdmbe.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeSearchDto {
    private String purposeName;
    private Integer defaultScope;
}
