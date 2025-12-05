package com.hdmbe.niceParkLog.dto;

import com.hdmbe.niceParkLog.entity.NiceparkLog;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class NiceExcelUpDto
{
    // 엑셀 헤더: 차량번호
    private String carNumber;

    // 엑셀 헤더: 입차일자 (2025-10-29)
    private String accessDate;

    // 엑셀 헤더: 입차시간(17:26:20)
    //나중에 Service에서 date + time 합쳐서 LocalDateTime accessTime 으로 변화
    private String accessTime;

    public NiceparkLog toEntity(LocalDateTime parsedDateTime)
    {
        return NiceparkLog.builder()
                .carNumber(this.carNumber)
                .accessTime(parsedDateTime)    // 서비스에서 합쳐서 넘겨줌
                .build();
    }
}
