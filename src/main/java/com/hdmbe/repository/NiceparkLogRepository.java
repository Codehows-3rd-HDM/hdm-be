package com.hdmbe.repository;

import com.hdmbe.entity.NiceparkLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface NiceparkLogRepository extends JpaRepository<NiceparkLog, Long>
{
    // 특정 기간 사이의 데이터 삭제
    void deleteByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}
