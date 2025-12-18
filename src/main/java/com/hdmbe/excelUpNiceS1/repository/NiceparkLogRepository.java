package com.hdmbe.excelUpNiceS1.repository;

import com.hdmbe.excelUpNiceS1.entity.NiceparkLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NiceparkLogRepository extends JpaRepository<NiceparkLog, Long>
{
    // 특정 기간 사이의 데이터 삭제
    void deleteByAccessTimeBetween(LocalDateTime start, LocalDateTime end);

    boolean existsByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}
