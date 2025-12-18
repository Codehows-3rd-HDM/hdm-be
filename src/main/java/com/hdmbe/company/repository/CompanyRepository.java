package com.hdmbe.company.repository;

import com.hdmbe.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByCompanyName(String companyName);

//    @Query("""
//        SELECT c FROM Company c
//        WHERE
//            (:companyName IS NULL OR c.companyName LIKE %:companyName%)
//            AND (:supplyTypeId IS NULL OR c.supplyType.id = :supplyTypeId)
//            AND (:supplyCustomerId IS NULL OR c.supplyCustomer.id = :supplyCustomerId)
//            AND (:keyword IS NULL OR (
//                c.companyName LIKE %:keyword%
//                OR c.address LIKE %:keyword%
//                OR c.supplyType.supplyTypeName LIKE %:keyword%
//               OR c.supplyCustomer.customerName LIKE %:keyword%
//            )
//        )
//    """)
//    Page<Company> search(
//            @Param("companyName") String companyName,
//            @Param("supplyTypeId") String supplyTypeId,
//            @Param("supplyCustomerId") String supplyCustomerId,
//            @Param("address") String address,
//            @Param("keyword") String keyword,
//            Pageable pageable
//    );
}

