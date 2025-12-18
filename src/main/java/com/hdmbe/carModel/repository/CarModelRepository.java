package com.hdmbe.carModel.repository;


import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.commonModule.constant.FuelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface CarModelRepository
        extends JpaRepository<CarModel, Long> {

    Optional<CarModel> findByCarCategoryIdAndFuelType(Long categoryId, FuelType fuelType);

    @Query("""
                select cm
                from CarModel cm
                join cm.carCategory cc
                left join cc.parentCategory pc
                where (:categoryId is null or cc.id = :categoryId)
                  and (:fuelType is null or cm.fuelType = :fuelType)
                  and (
                      :keyword is null
                           or cc.categoryName like %:keyword%
                           or cast(cm.fuelType as string) like %:keyword%
                           or pc.categoryName like %:keyword%)
            """)
    Page<CarModel> search(
            @Param("categoryId") Long categoryId,
            @Param("fuelType") FuelType fuelType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<CarModel> findByCarCategoryAndFuelType(
        CarCategory carCategory,
        FuelType fuelType
    );           
         
}
