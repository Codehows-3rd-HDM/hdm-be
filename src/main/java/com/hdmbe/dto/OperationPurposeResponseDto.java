package com.hdmbe.dto;

import com.hdmbe.entity.OperationPurpose;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class OperationPurposeResponseDto {
    private Long id;
    private String purposeName;
    private Integer defaultScope;

    public static OperationPurposeResponseDto fromEntity(OperationPurpose op) {
        return OperationPurposeResponseDto.builder()
                .id(op.getId())
                .purposeName(op.getPurposeName())
                .defaultScope(op.getDefaultScope())
                .build();
    }
}
