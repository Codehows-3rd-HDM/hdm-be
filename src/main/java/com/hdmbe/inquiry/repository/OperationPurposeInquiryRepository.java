package com.hdmbe.inquiry.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OperationPurposeInquiryRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {

    // 파이차트
    @Query("""
SELECT
    op.purposeName,
    SUM(m.totalEmission),              
    COUNT(m.id),                       
    SUM(v.operationDistance)           
FROM CarbonEmissionMonthlyLog m
JOIN m.vehicle v
JOIN VehicleOperationPurposeMap vmap
    ON vmap.vehicle = v
   AND vmap.endDate IS NULL
JOIN vmap.operationPurpose op
WHERE (:year IS NULL OR m.year = :year)
  AND (:month IS NULL OR m.month = :month)
  AND (:defaultScope IS NULL OR op.defaultScope = :defaultScope)
GROUP BY op.purposeName
""")
    List<Object[]> findPurposeSummary(
            @Param("year") Integer year,
            @Param("month") Integer month,
            @Param("defaultScope") Integer defaultScope
    );

    // 그래프
    @Query("""
    SELECT
        op.purposeName,
        m.month,
        SUM(m.totalEmission)
    FROM CarbonEmissionMonthlyLog m
    JOIN m.vehicle v
    JOIN VehicleOperationPurposeMap vmap
        ON vmap.vehicle = v
       AND vmap.endDate IS NULL
    JOIN vmap.operationPurpose op
    WHERE m.year = :year
      AND (:defaultScope IS NULL OR op.defaultScope = :defaultScope)
    GROUP BY op.purposeName, m.month
    ORDER BY m.month
    """)
    List<Object[]> findPurposeMonthlyTrend(
            @Param("year") Integer year,
            @Param("defaultScope") Integer defaultScope
    );
}
