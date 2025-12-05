package com.hdmbe.dto;

import com.hdmbe.entity.S1Log;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class S1ExcelUpDto
{
    // 엑셀 헤더: 사원번호
    private String memberId;

    // 엑셀 헤더: 이름
    private String employeeName;

    // 엑셀 헤더: 근무일자 (2025-10-29)
    private String accessDate;

    // 엑셀 헤더: 출근시간 (예: 06:51)
    // 만약 엑셀에 시간이 없고 날짜만 있다면 이 필드는 빼도 됩니다.
    // 하지만 "가장 빠른 시간(출근)"을 찾으려면 있는 게 좋습니다.
    private String accessTime;

    public S1Log toEntity(LocalDateTime parsedDateTime)
    {
        return S1Log.builder()
                .memberId(this.memberId)
                .employeeName(this.employeeName)
                .accessTime(parsedDateTime)  // 서비스에서 계산된 "진짜 출근 시간"
                .build();
    }
}
