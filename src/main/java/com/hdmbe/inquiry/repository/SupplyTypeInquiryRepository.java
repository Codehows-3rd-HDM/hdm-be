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
        SUM(m.totalEmission),
        SUM(v.operationDistance),
        COUNT(DISTINCT v.id)
    FROM CarbonEmissionMonthlyLog m
    JOIN m.vehicle v
    JOIN v.company c
    JOIN CompanySupplyTypeMap cmap
        ON cmap.company = c
       AND cmap.endDate IS NULL
    JOIN cmap.supplyType st
    WHERE m.year = :year
  AND (:month IS NULL OR m.month = :month)
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
        m.month,
        SUM(m.totalEmission)
    FROM CarbonEmissionMonthlyLog m
    JOIN m.vehicle v
    JOIN v.company c
    JOIN CompanySupplyTypeMap cmap
        ON cmap.company = c
       AND cmap.endDate IS NULL
    JOIN cmap.supplyType st
    WHERE m.year = :year
    GROUP BY st.supplyTypeName, m.month
    ORDER BY m.month
    """)
    List<Object[]> findSupplyTypeMonthlyTrend(
            @Param("year") Integer year
    );
}
