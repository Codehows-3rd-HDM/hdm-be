package com.hdmbe.emissionTarget.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hdmbe.emissionTarget.entity.EmissionTarget;

public interface EmissionTargetRepository extends JpaRepository<EmissionTarget, Long> {

    List<EmissionTarget> findByYearAndTargetTypeOrderByMonthAsc(Integer year, String targetType);

    void deleteByYearAndTargetType(Integer year, String targetType);
}
