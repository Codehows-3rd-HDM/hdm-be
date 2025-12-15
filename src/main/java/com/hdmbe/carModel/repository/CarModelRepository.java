package com.hdmbe.carModel.repository;

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

    List<CarModel> findByCarCategoryId(Long categoryId);

    List<CarModel> findByFuelType(FuelType fuelType);

    Optional<CarModel> findByCarCategoryIdAndFuelType(Long categoryId, FuelType fuelType);

    @Query("""
        SELECT m
        FROM CarModel m
        JOIN m.carCategory c
        LEFT JOIN c.parentCategory p
        WHERE 
            (:keyword IS NULL OR :keyword = '' OR 
                p.categoryName LIKE %:keyword% OR
                c.categoryName LIKE %:keyword% OR 
                CAST(m.fuelType AS string) LIKE %:keyword%)
        AND (:parentCategoryName IS NULL OR p.categoryName LIKE %:parentCategoryName%)
        AND (:childCategoryName IS NULL OR c.categoryName LIKE %:childCategoryName%)
        AND (:fuelType IS NULL OR m.fuelType = :fuelType)
    """)
    Page<CarModel> search(
            @Param("keyword") String keyword,
            @Param("parentCategoryName") String parentCategoryName,
            @Param("childCategoryName") String childCategoryName,
            @Param("fuelType") FuelType fuelType,
            Pageable pageable
    );
}
