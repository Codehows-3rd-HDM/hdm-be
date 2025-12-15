package com.hdmbe.supplyType.repository;

import com.hdmbe.supplyType.entity.SupplyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplyTypeRepository extends JpaRepository<SupplyType, Long> {

    List<SupplyType> findBySupplyTypeNameContaining(String processName);


}