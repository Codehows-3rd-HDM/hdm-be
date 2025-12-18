package com.hdmbe.excelUpBaseInfo.repository;

import com.hdmbe.excelUpBaseInfo.dto.ExcelDownBaseInfoDto;
import com.hdmbe.vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExcelBaseInfoRepository extends JpaRepository<Vehicle,Long> {
    @Query("""
        SELECT new com.hdmbe.excelUpBaseInfo.dto.ExcelDownBaseInfoDto(
            CAST(v.id AS integer),
            v.carNumber,
            (SELECT MAX(s1.employeeName) FROM S1Log s1 WHERE s1.memberId = v.driverMemberId),
            v.driverMemberId,
            c.companyName,
            st.supplyTypeName,
            sc.customerName,
            op.defaultScope,
            op.purposeName,
            c.address,
            v.operationDistance,
            v.carName,
            pc.categoryName,
            cc.categoryName,
            cm.fuelType,
            cm.customEfficiency,
            cef.emissionFactor
        )
        FROM Vehicle v
   
        LEFT JOIN v.company c
        LEFT JOIN c.supplyTypeMaps cstm
            ON cstm.endDate IS NULL
        LEFT JOIN cstm.supplyType st
        LEFT JOIN c.supplyCustomerMaps cscm
            ON cscm.endDate IS NULL
        LEFT JOIN cscm.supplyCustomer sc
    
        LEFT JOIN v.carModel cm
        LEFT JOIN cm.carCategory cc
        LEFT JOIN cc.parentCategory pc
        LEFT JOIN CarbonEmissionFactor cef
                         ON cef.fuelType = cm.fuelType
    
        LEFT JOIN VehicleOperationPurposeMap map
             ON map.vehicle = v AND map.endDate IS NULL
        LEFT JOIN map.operationPurpose op
    
        ORDER BY v.id DESC
    """)
    List<ExcelDownBaseInfoDto> findBaseInfoForExcel();
}
