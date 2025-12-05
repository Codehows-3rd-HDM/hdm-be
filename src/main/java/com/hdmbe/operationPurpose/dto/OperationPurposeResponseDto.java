package com.hdmbe.operationPurpose.dto;

import com.hdmbe.operationPurpose.entity.OperationPurpose;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
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
