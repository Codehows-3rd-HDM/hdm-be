package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
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
    List<MonthlySumView> sumByYear(@Param("year") int year);

    // 실적이 존재하는 연도 목록 조회
    @Query("select distinct m.year from CarbonEmissionMonthlyLog m order by m.year")
    List<Integer> findDistinctYears();

    interface MonthlySumView {

        Integer getMonth();

        BigDecimal getTotal();
    }
}