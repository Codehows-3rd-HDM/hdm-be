package com.hdmbe.emissionTarget.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hdmbe.emissionTarget.entity.EmissionTarget;
import org.springframework.data.jpa.repository.Query;

public interface EmissionTargetRepository extends JpaRepository<EmissionTarget, Long> {

    List<EmissionTarget> findByYearAndTargetTypeOrderByMonthAsc(Integer year, String targetType);

    void deleteByYearAndTargetType(Integer year, String targetType);

    // 목표가 설정된 연도들만 중복 없이 가져오기
    @Query("SELECT DISTINCT e.year FROM EmissionTarget e")
    List<Integer> findDistinctYears();
}
