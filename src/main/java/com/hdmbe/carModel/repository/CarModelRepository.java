package com.hdmbe.carModel.repository;


import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.constant.FuelType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface CarModelRepository
        extends JpaRepository<CarModel, Long> {

    List<CarModel> findByCarCategoryId(Long categoryId);

    List<CarModel> findByFuelType(FuelType fuelType);

    @Query("""
            SELECT m 
            FROM CarModel m 
            JOIN m.carCategory c 
            WHERE c.categoryName LIKE %:categoryName%
            """)
    List<CarModel> findByCategoryNameLike(@Param("categoryName") String categoryName);


    @Query("""
    SELECT m FROM CarModel m
    JOIN m.carCategory c
    WHERE c.categoryName LIKE %:keyword%
       OR CAST(m.fuelType AS string) LIKE %:keyword%
           """)
    List<CarModel> searchByKeyword(@Param("keyword") String keyword);

}


