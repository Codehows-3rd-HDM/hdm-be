package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface EmissionDailyRepository extends JpaRepository<CarbonEmissionDailyLog, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM CarbonEmissionDailyLog d WHERE d.operationDate BETWEEN :startDate AND :endDate AND d.emissionSource = :source")
    void deleteByDateAndSource(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("source") String source);

    // 기간 내 '모든' 로그 조회
    @Query("SELECT d FROM CarbonEmissionDailyLog d JOIN FETCH d.vehicle WHERE d.operationDate BETWEEN :startDate AND :endDate")
    List<CarbonEmissionDailyLog> findAllByOperationDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // 특정 차량의 로그 존재 여부 확인
    boolean existsByVehicle(Vehicle vehicle);
}
