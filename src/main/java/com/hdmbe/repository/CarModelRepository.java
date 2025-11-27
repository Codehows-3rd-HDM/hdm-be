package com.hdmbe.repository;

import com.hdmbe.entity.CarModel;
import com.hdmbe.constant.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {

    List<CarModel> findByCategoryId(Long categoryId);

    List<CarModel> findByFuelType(FuelType fuelType);
}
