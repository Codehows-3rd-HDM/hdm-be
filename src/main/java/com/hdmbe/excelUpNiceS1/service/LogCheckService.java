package com.hdmbe.excelUpNiceS1.service;

import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogCheckService {

    // 계산 결과가 저장되는 Repository를 주입받습니다.
    private final EmissionMonthlyRepository emissionMonthlyRepository;

    @Transactional(readOnly = true)
    public boolean checkDataExists(int year, int month, String source)
    {
        // 1. source가 안 넘어왔을 경우(파일 선택 전 등)를 대비한 방어 코드
        if (source == null || source.isEmpty()) return false;

        if (month == 0) {
            // 연간 전체 데이터 중 해당 출처(S1/NICE)가 있는지 확인
            return emissionMonthlyRepository.existsByYearAndEmissionSource(year, source);
        } else {
            // 특정 월 데이터 중 해당 출처(S1/NICE)가 있는지 확인
            return emissionMonthlyRepository.existsByYearAndMonthAndEmissionSource(year, month, source);
        }
    }
}
