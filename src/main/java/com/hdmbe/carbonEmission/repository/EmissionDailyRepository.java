package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EmissionDailyRepository extends JpaRepository<CarbonEmissionDailyLog, Long> {
    // 기간으로 삭제
    void deleteByOperationDateBetween(LocalDate start, LocalDate end);
}
