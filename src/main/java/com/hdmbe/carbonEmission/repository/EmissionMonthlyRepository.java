package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmissionMonthlyRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {

    // 조회 : 연도 + 출처로 존재 여부 확인
    boolean existsByYearAndEmissionSource(int year, String source);

    // 조회 : 연도 + 월 + 출처로 존재 여부 확인
    boolean existsByYearAndMonthAndEmissionSource(int year, int month, String source);

    // 1. 특정 월 데이터 삭제 (꼬리표 기준)
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionMonthlyLog m " +
            "WHERE m.year = :year AND m.month = :month " +
            "AND m.emissionSource = :source")
    void deleteByYearMonthAndSource(@Param("year") int year,
                                    @Param("month") int month,
                                    @Param("source") String source);

    // 2. 특정 연도 전체 데이터 삭제 (꼬리표 기준 - 연간 업로드용)
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionMonthlyLog m " +
            "WHERE m.year = :year " +
            "AND m.emissionSource = :source")
    void deleteByYearAndSource(@Param("year") int year,
                               @Param("source") String source);
}
