package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("""
    SELECT c
    FROM Company c
    LEFT JOIN CompanySupplyTypeMap cstm
        ON cstm.company = c
        AND cstm.endDate IS NULL
    LEFT JOIN cstm.supplyType st
    LEFT JOIN CompanySupplyCustomerMap cscm
        ON cscm.company = c
        AND cscm.endDate IS NULL
    LEFT JOIN cscm.supplyCustomer sc
    WHERE
        (:companyName IS NULL OR c.companyName LIKE %:companyName%)
    AND (:supplyTypeName IS NULL OR st.supplyTypeName LIKE %:supplyTypeName%)
    AND (:supplyCustomerName IS NULL OR sc.customerName LIKE %:supplyCustomerName%)
    AND (:address IS NULL OR c.address LIKE %:address%)
    AND (
        :keyword IS NULL OR
        c.companyName LIKE %:keyword%
        OR c.address LIKE %:keyword%
        OR st.supplyTypeName LIKE %:keyword%
        OR sc.customerName LIKE %:keyword%
    )
""")
    Page<Company> search(
            @Param("companyName") String companyName,
            @Param("supplyTypeName") String supplyTypeName,
            @Param("supplyCustomerName") String supplyCustomerName,
            @Param("address") String address,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공급유형 정렬용: 매핑 테이블 기준 정렬
    @Query("""
    SELECT c
    FROM Company c
    LEFT JOIN CompanySupplyTypeMap cstm
        ON cstm.company = c
        AND cstm.endDate IS NULL
    LEFT JOIN cstm.supplyType st
    LEFT JOIN CompanySupplyCustomerMap cscm
        ON cscm.company = c
        AND cscm.endDate IS NULL
    LEFT JOIN cscm.supplyCustomer sc
    WHERE
        (:companyName IS NULL OR c.companyName LIKE %:companyName%)
    AND (:supplyTypeName IS NULL OR st.supplyTypeName LIKE %:supplyTypeName%)
    AND (:supplyCustomerName IS NULL OR sc.customerName LIKE %:supplyCustomerName%)
    AND (:address IS NULL OR c.address LIKE %:address%)
    AND (
        :keyword IS NULL OR
        c.companyName LIKE %:keyword%
        OR c.address LIKE %:keyword%
        OR st.supplyTypeName LIKE %:keyword%
        OR sc.customerName LIKE %:keyword%
    )
    """)
    Page<Company> searchOrderBySupplyType(
            @Param("companyName") String companyName,
            @Param("supplyTypeName") String supplyTypeName,
            @Param("supplyCustomerName") String supplyCustomerName,
            @Param("address") String address,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 공급고객 정렬용: 매핑 테이블 기준 정렬
    @Query("""
    SELECT c
    FROM Company c
    LEFT JOIN CompanySupplyTypeMap cstm
        ON cstm.company = c
        AND cstm.endDate IS NULL
    LEFT JOIN cstm.supplyType st
    LEFT JOIN CompanySupplyCustomerMap cscm
        ON cscm.company = c
        AND cscm.endDate IS NULL
    LEFT JOIN cscm.supplyCustomer sc
    WHERE
        (:companyName IS NULL OR c.companyName LIKE %:companyName%)
    AND (:supplyTypeName IS NULL OR st.supplyTypeName LIKE %:supplyTypeName%)
    AND (:supplyCustomerName IS NULL OR sc.customerName LIKE %:supplyCustomerName%)
    AND (:address IS NULL OR c.address LIKE %:address%)
    AND (
        :keyword IS NULL OR
        c.companyName LIKE %:keyword%
        OR c.address LIKE %:keyword%
        OR st.supplyTypeName LIKE %:keyword%
        OR sc.customerName LIKE %:keyword%
    )
    """)
    Page<Company> searchOrderByCustomer(
            @Param("companyName") String companyName,
            @Param("supplyTypeName") String supplyTypeName,
            @Param("supplyCustomerName") String supplyCustomerName,
            @Param("address") String address,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Optional<Company> findByCompanyName(String companyName);

}
