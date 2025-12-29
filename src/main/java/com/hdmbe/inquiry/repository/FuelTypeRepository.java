package com.hdmbe.inquiry.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FuelTypeRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {

    // 파이 차트
    @Query("""
SELECT
    cm.fuelType,
    SUM(m.totalEmission)
FROM CarbonEmissionMonthlyLog m
JOIN m.vehicle v
JOIN v.carModel cm
WHERE (:year IS NULL OR m.year = :year)
  AND (:month IS NULL OR m.month = :month)
  AND (
      :defaultScope IS NULL
      OR EXISTS (
          SELECT 1
          FROM VehicleOperationPurposeMap vmap
          WHERE vmap.vehicle = v
            AND vmap.endDate IS NULL
            AND vmap.operationPurpose.defaultScope = :defaultScope
      )
  )
GROUP BY cm.fuelType
""")
    List<Object[]> findFuelEmissionForPie(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("defaultScope") Integer defaultScope
    );

    // 그래프
    @Query("""
SELECT
    cm.fuelType,
    m.month,
    SUM(m.totalEmission)
FROM CarbonEmissionMonthlyLog m
JOIN m.vehicle v
JOIN v.carModel cm
WHERE m.year = :year
  AND (
      :defaultScope IS NULL
      OR EXISTS (
          SELECT 1
          FROM VehicleOperationPurposeMap vmap
          WHERE vmap.vehicle = v
            AND vmap.endDate IS NULL
            AND vmap.operationPurpose.defaultScope = :defaultScope
      )
  )
GROUP BY cm.fuelType, m.month
ORDER BY m.month
""")
    List<Object[]> findYearlyMonthlyTrend(
            @Param("year") Integer year,
            @Param("defaultScope") Integer defaultScope
    );
}
