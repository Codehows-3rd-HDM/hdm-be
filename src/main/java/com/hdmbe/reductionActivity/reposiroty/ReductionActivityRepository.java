package com.hdmbe.reductionActivity.reposiroty;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hdmbe.reductionActivity.entity.ReductionActivity;

public interface ReductionActivityRepository extends JpaRepository<ReductionActivity, Long> {

    @Query("select distinct r from ReductionActivity r left join fetch r.photos p where (:start is null or r.periodEnd >= :start) and (:end is null or r.periodStart <= :end) order by r.periodStart desc")
    List<ReductionActivity> findWithPhotosByPeriodRange(@Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("select r from ReductionActivity r left join fetch r.photos p where r.id = :id")
    ReductionActivity findWithPhotosById(@Param("id") Long id);
}
