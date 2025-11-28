package com.hdmbe.repository;

import com.hdmbe.entity.S1Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface S1LogRepository extends JpaRepository<S1Log, Long>
{
    void deleteByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}
