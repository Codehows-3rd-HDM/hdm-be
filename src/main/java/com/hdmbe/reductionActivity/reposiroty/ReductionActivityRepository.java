package com.hdmbe.reductionActivity.reposiroty;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hdmbe.reductionActivity.entity.ReductionActivity;

public interface ReductionActivityRepository extends JpaRepository<ReductionActivity, Long> {

    @Query("select r from ReductionActivity r where (:start is null or r.periodEnd >= :start) and (:end is null or r.periodStart <= :end) order by r.periodStart desc")
    List<ReductionActivity> findByPeriodRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
