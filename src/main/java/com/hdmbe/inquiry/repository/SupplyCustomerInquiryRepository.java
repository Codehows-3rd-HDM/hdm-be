package com.hdmbe.inquiry.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyCustomerInquiryRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {
    // 파이차트
    @Query("""
        SELECT
            sc.customerName,
            SUM(m.totalEmission),
            SUM(v.operationDistance),
            COUNT(DISTINCT v.id)
        FROM CarbonEmissionMonthlyLog m
        JOIN m.vehicle v
        JOIN v.company c
        JOIN CompanySupplyCustomerMap cmap
            ON cmap.company = c
           AND cmap.endDate IS NULL
        JOIN cmap.supplyCustomer sc
        WHERE m.year = :year
          AND (:month IS NULL OR m.month = :month)
        GROUP BY sc.customerName
        """)
    List<Object[]> findSupplyCustomerSummary(
            @Param("year") Integer year,
            @Param("month") Integer month
    );
    // 그래프
    @Query("""
        SELECT
            sc.customerName,
            m.month,
            SUM(m.totalEmission)
        FROM CarbonEmissionMonthlyLog m
        JOIN m.vehicle v
        JOIN v.company c
        JOIN CompanySupplyCustomerMap cmap
            ON cmap.company = c
           AND cmap.endDate IS NULL
        JOIN cmap.supplyCustomer sc
        WHERE m.year = :year
        GROUP BY sc.customerName, m.month
        ORDER BY m.month
        """)
    List<Object[]> findSupplyCustomerMonthlyTrend(
            @Param("year") Integer year
    );
}
