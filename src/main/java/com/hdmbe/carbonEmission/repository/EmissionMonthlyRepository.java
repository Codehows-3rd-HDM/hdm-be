package com.hdmbe.carbonEmission.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 특정 연도의 월별 합계 조회 (전체 차량 합산)
    @Query("select m.month as month, sum(m.totalEmission) as total from CarbonEmissionMonthlyLog m where m.year = :year group by m.month order by m.month")
    List<MonthlySumView> sumByYear(@Param("year") int year);

    // Scope별 월별 합계 조회 (운행목적 변경 이력 반영, 월 기준 단일 매핑 선택)
    // 각 월에 대해 해당 차량의 "그 달의 마지막 날"을 포함하는 매핑 중
    // end_date(NULL은 9999-12-31로 간주)가 가장 작은(가까운) 매핑을 유효 매핑으로 선택합니다.
    @Query(value = "SELECT m.month AS month, SUM(m.total_emission) AS total "
            + "FROM CARBON_EMISSION_MONTHLY_LOG m "
            + "WHERE m.year = :year "
            + "AND EXISTS ("
            + "  SELECT 1 FROM VEHICLE_OPERATION_PURPOSE_MAP v "
            + "  JOIN OPERATION_PURPOSE op ON op.purpose_id = v.purpose_id "
            + "  WHERE v.car_id = m.car_id "
            + "    AND op.default_scope = :scope "
            + "    AND COALESCE(v.end_date, DATE('9999-12-31')) >= LAST_DAY(CONCAT(m.year, '-', LPAD(m.month, 2, '0'), '-01')) "
            + "    AND NOT EXISTS ("
            + "      SELECT 1 FROM VEHICLE_OPERATION_PURPOSE_MAP v2 "
            + "      WHERE v2.car_id = m.car_id "
            + "        AND COALESCE(v2.end_date, DATE('9999-12-31')) >= LAST_DAY(CONCAT(m.year, '-', LPAD(m.month, 2, '0'), '-01')) "
            + "        AND COALESCE(v2.end_date, DATE('9999-12-31')) < COALESCE(v.end_date, DATE('9999-12-31'))"
            + "    )"
            + ") "
            + "GROUP BY m.month ORDER BY m.month", nativeQuery = true)
    List<MonthlySumView> sumByYearAndScope(@Param("year") int year, @Param("scope") Integer scope);

    // 실적이 존재하는 연도 목록 조회
    @Query("select distinct m.year from CarbonEmissionMonthlyLog m order by m.year")
    List<Integer> findDistinctYears();

    interface MonthlySumView {

        Integer getMonth();

        BigDecimal getTotal();
    }

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
            @Param("defaultScope") Integer defaultScope
    );

    // 조회 : 연도 + 출처로 존재 여부 확인
    boolean existsByYearAndEmissionSource(int year, String source);

    // 조회 : 연도 + 월 + 출처로 존재 여부 확인
    boolean existsByYearAndMonthAndEmissionSource(int year, int month, String source);

    // 1. 특정 월 데이터 삭제 (꼬리표 기준)
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionMonthlyLog m "
            + "WHERE m.year = :year AND m.month = :month "
            + "AND m.emissionSource = :source")
    void deleteByYearMonthAndSource(@Param("year") int year,
            @Param("month") int month,
            @Param("source") String source);

    // 2. 특정 연도 전체 데이터 삭제 (꼬리표 기준 - 연간 업로드용)
    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionMonthlyLog m "
            + "WHERE m.year = :year "
            + "AND m.emissionSource = :source")
    void deleteByYearAndSource(@Param("year") int year,
            @Param("source") String source);

    }

    
             
             
            
            
             
             
                
    
    
    