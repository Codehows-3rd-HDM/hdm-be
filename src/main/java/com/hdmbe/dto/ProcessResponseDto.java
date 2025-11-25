package com.hdmbe.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ProcessResponseDto {

    private Long id;
    private String processName;
    private String remark;
    private LocalDateTime createdAt;
}
