package com.hdmbe.carModel.repository;


import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.commonModule.constant.FuelType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query("""
        SELECT m
        FROM carCategory m
        WHERE 
            (:parent_id IS NULL)
        AND (:category_name = :LIKE %:keyword%)
    """)

    @Query("""
        SELECT m
        FROM carCategory m
        WHERE 
            (:parent_id IS NOT NULL)
        AND (:category_name = :LIKE %:keyword%)
    """)

    @Query("""
        SELECT m
        FROM carCategory m
        WHERE 
        (:category_name = :LIKE %:keyword%)
    """)

}



