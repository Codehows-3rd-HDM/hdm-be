package com.hdmbe.inquiry.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyTypeInquiryRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {
    // 파이 차트
    @Query("""
    SELECT
        st.supplyTypeName,
        SUM(d.dailyEmission),
        SUM(v.operationDistance),
        COUNT(d.id)
    FROM CarbonEmissionDailyLog d
    JOIN d.vehicle v
    JOIN v.company c
    JOIN CompanySupplyTypeMap cmap ON cmap.company = c
    JOIN cmap.supplyType st
    WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
      AND (:month IS NULL OR MONTH(d.operationDate) = :month)
      AND cmap.endDate IS NULL
      AND cmap.id = (
          SELECT MIN(cm2.id)
          FROM CompanySupplyTypeMap cm2
          WHERE cm2.company = c AND cm2.endDate IS NULL
      )
    GROUP BY st.supplyTypeName
    """)
    List<Object[]> findSupplyTypeSummary(
            @Param("year") Integer year,
            @Param("month") Integer month
    );
    // 그래프
    @Query("""
    SELECT
        st.supplyTypeName,
        MONTH(d.operationDate),
        SUM(d.dailyEmission)
    FROM CarbonEmissionDailyLog d
    JOIN d.vehicle v
    JOIN v.company c
    JOIN CompanySupplyTypeMap cmap ON cmap.company = c
    JOIN cmap.supplyType st
    WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
      AND cmap.endDate IS NULL
      AND cmap.id = (
          SELECT MIN(cm2.id)
          FROM CompanySupplyTypeMap cm2
          WHERE cm2.company = c AND cm2.endDate IS NULL
      )
    GROUP BY st.supplyTypeName, MONTH(d.operationDate)
    ORDER BY MONTH(d.operationDate)
    """)
    List<Object[]> findSupplyTypeMonthlyTrend(
            @Param("year") Integer year
    );
}
