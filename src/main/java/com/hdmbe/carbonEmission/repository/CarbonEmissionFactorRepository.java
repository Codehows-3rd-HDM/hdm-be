package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarbonEmissionFactorRepository extends JpaRepository<CarbonEmissionFactor, Long> {
}
