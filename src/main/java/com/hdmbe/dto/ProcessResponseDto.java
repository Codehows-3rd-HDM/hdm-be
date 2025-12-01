package com.hdmbe.dto;

import com.hdmbe.entity.ProcessEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessResponseDto {
    private Long id;
    private String processName;

    public static ProcessResponseDto fromEntity(ProcessEntity entity) {
        return ProcessResponseDto.builder()
                .id(entity.getId())
                .processName(entity.getProcessName())
                .build();
    }
}
