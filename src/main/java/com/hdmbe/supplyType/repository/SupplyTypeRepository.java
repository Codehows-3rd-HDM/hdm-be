package com.hdmbe.supplyType.repository;

import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.supplyType.entity.SupplyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SupplyTypeRepository extends JpaRepository<SupplyType, Long> {

    @Query("""
        SELECT st
        FROM SupplyType st
        WHERE
            (:supplyTypeName IS NULL OR st.supplyTypeName LIKE %:supplyTypeName%)
    """)
    Page<SupplyType> search(
            @Param("supplyTypeName") String supplyTypeName,
            Pageable pageable
    );

}