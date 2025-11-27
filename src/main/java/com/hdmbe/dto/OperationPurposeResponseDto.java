package com.hdmbe.dto;

import com.hdmbe.entity.OperationPurpose;
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

    public static OperationPurposeResponseDto fromEntity(OperationPurpose entity) {
        return OperationPurposeResponseDto.builder()
                .id(entity.getId())
                .purposeName(entity.getPurposeName())
                .defaultScope(entity.getDefaultScope())
                .build();
    }
}
