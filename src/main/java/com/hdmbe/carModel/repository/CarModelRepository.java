package com.hdmbe.carModel.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hdmbe.carCategory.entity.CarCategory;
import com.hdmbe.carModel.entity.CarModel;
import com.hdmbe.commonModule.constant.FuelType;

public interface CarModelRepository
        extends JpaRepository<CarModel, Long> {

    Optional<CarModel> findByCarCategoryIdAndFuelType(Long categoryId, FuelType fuelType);

    Optional<CarModel> findByCarCategoryId(Long carCategoryId);

    @Query("""
                select cm
                from CarModel cm
                join cm.carCategory cc
                left join cc.parentCategory pc
                WHERE
            (:parentCategoryId IS NULL OR pc.id = :parentCategoryId)
        AND (:carCategoryId IS NULL OR cc.id = :carCategoryId)
        AND (:fuelType IS NULL OR cm.fuelType = :fuelType)
        AND (
            :keyword IS NULL OR
            LOWER(cc.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(pc.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
            LOWER(str(cm.fuelType)) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<CarModel> search(
            @Param("parentCategoryId") Long parentCategoryId,
            @Param("carCategoryId") Long carCategoryId,
            @Param("fuelType") FuelType fuelType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<CarModel> findByCarCategoryAndFuelType(
            CarCategory carCategory,
            FuelType fuelType
    );

}
