package com.hdmbe.vehicle.repository;

import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.entity.VehicleOperationPurposeMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleOperationPurposeMapRepository
        extends JpaRepository<VehicleOperationPurposeMap, Long> {

    Optional<VehicleOperationPurposeMap> findByVehicleAndEndDateIsNull(Vehicle vehicle);
}
