package com.hdmbe.vehicle.repository;

import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleOperationPurposeMapRepository extends JpaRepository<VehicleOperationPurposeMap, Long> {

    @Query("SELECT m FROM VehicleOperationPurposeMap m WHERE m.vehicle.id = :vehicleId AND m.endDate IS NULL")
    Optional<VehicleOperationPurposeMap> findCurrentByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT m FROM VehicleOperationPurposeMap m WHERE m.vehicle.id = :vehicleId AND m.endDate IS NULL")
    List<VehicleOperationPurposeMap> findAllCurrentByVehicleId(@Param("vehicleId") Long vehicleId);

    @Query("SELECT m FROM VehicleOperationPurposeMap m WHERE m.operationPurpose.id = :operationPurposeId AND m.endDate IS NULL")
    List<VehicleOperationPurposeMap> findAllCurrentByOperationPurposeId(@Param("operationPurposeId") Long operationPurposeId);

    // 현재 적용 중인(EndDate가 null) 목적 조회
    Optional<VehicleOperationPurposeMap> findFirstByVehicleAndEndDateIsNull(Vehicle vehicle);

    Optional<VehicleOperationPurposeMap> findByVehicleAndEndDateIsNull(Vehicle vehicle);
}