package com.hdmbe.inquiry.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ViewPeriodEmissionRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 2025-12-29 형식을 날짜로 변환
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
