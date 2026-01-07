package com.hdmbe.inquiry.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationPurposeInquiryRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {

    @Query("""
SELECT
    CASE
        WHEN s.id IS NOT NULL THEN '출퇴근'
        ELSE op.purposeName
    END,
    SUM(d.dailyEmission),
    COUNT(d.id),
    SUM(v.operationDistance)
FROM CarbonEmissionDailyLog d
JOIN d.vehicle v
JOIN VehicleOperationPurposeMap vmap ON vmap.vehicle = v
JOIN vmap.operationPurpose op
LEFT JOIN S1Log s
    ON s.memberId = v.driverMemberId
   AND s.accessDate = d.operationDate
WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
  AND (:month IS NULL OR MONTH(d.operationDate) = :month)
  AND (:defaultScope IS NULL OR op.defaultScope = :defaultScope)
  AND vmap.endDate IS NULL
  AND vmap.id = (
      SELECT MIN(m2.id)
      FROM VehicleOperationPurposeMap m2
      WHERE m2.vehicle = v AND m2.endDate IS NULL
  )
GROUP BY
    CASE
        WHEN s.id IS NOT NULL THEN '출퇴근'
        ELSE op.purposeName
    END
""")
    List<Object[]> findPurposeSummary(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("defaultScope") Integer defaultScope
    );

    // 그래프
    @Query("""
SELECT
    CASE
        WHEN s.id IS NOT NULL THEN '출퇴근'
        ELSE op.purposeName
    END,
    MONTH(d.operationDate),
    SUM(d.dailyEmission)
FROM CarbonEmissionDailyLog d
JOIN d.vehicle v
JOIN VehicleOperationPurposeMap vmap ON vmap.vehicle = v
JOIN vmap.operationPurpose op
LEFT JOIN S1Log s
    ON s.memberId = v.driverMemberId
   AND s.accessDate = d.operationDate
WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
  AND (:defaultScope IS NULL OR op.defaultScope = :defaultScope)
  AND vmap.endDate IS NULL
  AND vmap.id = (
      SELECT MIN(m2.id)
      FROM VehicleOperationPurposeMap m2
      WHERE m2.vehicle = v AND m2.endDate IS NULL
  )
GROUP BY
    CASE
        WHEN s.id IS NOT NULL THEN '출퇴근'
        ELSE op.purposeName
    END,
    MONTH(d.operationDate)
ORDER BY MONTH(d.operationDate)
""")
    List<Object[]> findPurposeMonthlyTrend(
            @Param("year") Integer year,
            @Param("defaultScope") Integer defaultScope
    );
}
