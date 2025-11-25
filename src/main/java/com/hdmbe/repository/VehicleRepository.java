package com.hdmbe.repository;

import com.hdmbe.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByCarNumber(String carNumber); // 중복 체크용
}
