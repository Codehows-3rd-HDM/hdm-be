package com.hdmbe.excelUpNiceS1.repository;

import com.hdmbe.excelUpNiceS1.entity.S1Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface S1LogRepository extends JpaRepository<S1Log, Long>
{
    void deleteByAccessDateBetween(LocalDate start, LocalDate end);

    boolean existsByAccessDateBetween(LocalDate start, LocalDate end);
}
