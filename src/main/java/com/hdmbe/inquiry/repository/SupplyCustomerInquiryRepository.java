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
            SUM(d.dailyEmission),
            SUM(v.operationDistance),
            COUNT(d.id)
        FROM CarbonEmissionDailyLog d
        JOIN d.vehicle v
        JOIN v.company c
        JOIN CompanySupplyCustomerMap cmap ON cmap.company = c
        JOIN cmap.supplyCustomer sc
        WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
          AND (:month IS NULL OR MONTH(d.operationDate) = :month)
          AND cmap.endDate IS NULL
          AND sc.customerName = (
              SELECT MIN(sc2.customerName)
              FROM CompanySupplyCustomerMap cm2
              JOIN cm2.supplyCustomer sc2
              WHERE cm2.company = c AND cm2.endDate IS NULL
          )
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
            MONTH(d.operationDate),
            SUM(d.dailyEmission)
        FROM CarbonEmissionDailyLog d
        JOIN d.vehicle v
        JOIN v.company c
        JOIN CompanySupplyCustomerMap cmap ON cmap.company = c
        JOIN cmap.supplyCustomer sc
        WHERE (:year IS NULL OR YEAR(d.operationDate) = :year)
          AND cmap.endDate IS NULL
          AND sc.customerName = (
              SELECT MIN(sc2.customerName)
              FROM CompanySupplyCustomerMap cm2
              JOIN cm2.supplyCustomer sc2
              WHERE cm2.company = c AND cm2.endDate IS NULL
          )
        GROUP BY sc.customerName, MONTH(d.operationDate)
        ORDER BY MONTH(d.operationDate)
        """)
    List<Object[]> findSupplyCustomerMonthlyTrend(
            @Param("year") Integer year
    );
}
