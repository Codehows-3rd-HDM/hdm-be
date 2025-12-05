package com.hdmbe.repository;

import com.hdmbe.entity.CarbonEmissionMonthlyLog;
import com.hdmbe.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface EmissionMonthlyRepository extends JpaRepository<CarbonEmissionMonthlyLog, Long> {
    // 기간으로 삭제
    // 특정 월 삭제
    void deleteByYearAndMonth(int year, int month);
    // 특정 연도 전체 삭제
    void deleteByYear(int year);

    // 차량, 연도, 월 조건으로 데이터 단건 조회
    Optional<CarbonEmissionMonthlyLog> findByVehicleAndYearAndMonth(Vehicle vehicle, int year, int month);
}
