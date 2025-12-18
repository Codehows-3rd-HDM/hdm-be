package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("""
    SELECT DISTINCT c
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

    Optional<Company> findByCompanyName(String companyName);

}

