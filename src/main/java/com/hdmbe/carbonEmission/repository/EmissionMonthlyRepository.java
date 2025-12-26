package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmissionMonthlyRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {
    // 기간으로 삭제
    // 특정 월 삭제
    void deleteByYearAndMonth(int year, int month);

    // 특정 연도 전체 삭제
    void deleteByYear(int year);

    // 차량, 연도, 월 조건으로 데이터 단건 조회
    Optional<CarbonEmissionMonthlyLog> findByVehicleAndYearAndMonth(Vehicle vehicle, int year, int month);

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
      :scope IS NULL
      OR EXISTS (
          SELECT 1
          FROM VehicleOperationPurposeMap vmap
          WHERE vmap.vehicle = v
            AND vmap.endDate IS NULL
            AND vmap.operationPurpose.defaultScope = :scope
      )
  )
GROUP BY cm.fuelType
""")
    List<Object[]> findFuelEmissionForPie(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("scope") Integer scope
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
      :scope IS NULL
      OR EXISTS (
          SELECT 1
          FROM VehicleOperationPurposeMap vmap
          WHERE vmap.vehicle = v
            AND vmap.endDate IS NULL
            AND vmap.operationPurpose.defaultScope = :scope
      )
  )
GROUP BY cm.fuelType, m.month
ORDER BY m.month
""")
    List<Object[]> findYearlyMonthlyTrend(
            @Param("year") Integer year,
            @Param("scope") Integer scope
    );
}