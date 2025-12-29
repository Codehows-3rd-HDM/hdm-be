package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface EmissionDailyRepository extends JpaRepository<CarbonEmissionDailyLog, Long> {
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionDailyLog d WHERE d.operationDate BETWEEN :startDate AND :endDate AND d.emissionSource = :source")
    void deleteByDateAndSource(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("source") String source);
}
