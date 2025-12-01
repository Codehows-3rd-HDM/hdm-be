package com.hdmbe.repository;

import com.hdmbe.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // 차량번호 중복 체크
    boolean existsByCarNumber(String carNumber);

}
