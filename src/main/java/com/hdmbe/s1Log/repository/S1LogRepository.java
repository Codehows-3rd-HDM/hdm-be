package com.hdmbe.s1Log.repository;

import com.hdmbe.s1Log.entity.S1Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface S1LogRepository extends JpaRepository<S1Log, Long>
{
    void deleteByAccessTimeBetween(LocalDateTime start, LocalDateTime end);
}
