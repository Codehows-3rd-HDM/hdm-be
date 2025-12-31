package com.hdmbe.inquiry.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationPurposeInquiryRequestDto {

    private Integer year;

    private Integer month;

    private Integer defaultScope;

    private String purposeName;
}
