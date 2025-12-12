package com.hdmbe.excelUpNiceS1.service;


import com.hdmbe.excelUpNiceS1.repository.NiceparkLogRepository;
import com.hdmbe.excelUpNiceS1.repository.S1LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class LogCheckService {
    private final NiceparkLogRepository niceparkLogRepo;
    private final S1LogRepository s1Repo;

    public boolean checkDataExists(int year, int month)
    {
        LocalDateTime startDt;
        LocalDateTime endDt;
        LocalDate startD;
        LocalDate endD;

        if (month == 0)
        {
            startDt = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            endDt = LocalDateTime.of(year, 12, 31, 23, 59, 59);
            startD = LocalDate.of(year, 1, 1);
            endD = LocalDate.of(year, 12, 31);
        }
        else
        {
            YearMonth ym = YearMonth.of(year, month);
            startDt = ym.atDay(1).atStartOfDay();
            endDt = ym.atEndOfMonth().atTime(23, 59, 59);
            startD = ym.atDay(1);
            endD = ym.atEndOfMonth();
        }

        // 나이스파크 또는 에스원 둘 중 하나라도 있으면 true
        boolean niceExists = niceparkLogRepo.existsByAccessTimeBetween(startDt, endDt);
        boolean s1Exists = s1Repo.existsByAccessDateBetween(startD, endD);

        return niceExists || s1Exists;
    }
}
