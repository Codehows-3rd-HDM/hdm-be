package com.hdmbe.company.service;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.service.SupplyCustomerService;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.service.SupplyTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CompanyExcelService {

    private final CompanyRepository companyRepository;
    private final SupplyTypeService supplyTypeService;
    private final SupplyCustomerService supplyCustomerService;

    @Transactional
    public Company getOrCreate(String companyName,
                               String address,
                               String supplyTypeName,
                               String customerName,
                               BigDecimal distance,
                               String remark)
    {
        // 1. Level 1 데이터(부모) 먼저 확보 (없으면 만들어서 가져옴)
        SupplyType type = supplyTypeService.getOrCreate(supplyTypeName);
        SupplyCustomer customer = supplyCustomerService.getOrCreate(customerName);

        // 2. 업체 조회 및 처리
        return companyRepository.findByCompanyName(companyName)
                .map(existing -> {
                    // [이미 있는 경우] -> 업데이트
                    existing.setAddress(address);
                    existing.setOneWayDistance(distance);
                    existing.setSupplyType(type);         // 유형이 바뀌었을 수도 있음
                    existing.setSupplyCustomer(customer); // 고객군이 바뀌었을 수도 있음
                    existing.setRemark(remark);
                    return existing;
                })
                .orElseGet(() ->
                        // [없는 경우] -> 신규 저장
                        companyRepository.save(
                                Company.builder()
                                        .companyName(companyName)
                                        .address(address)
                                        .oneWayDistance(distance) // 거리 저장
                                        .supplyType(type)       // 연결
                                        .supplyCustomer(customer) // 연결
                                        .remark(remark != null ? remark : "") // 비고 없으면 공백
                                        .build()
                        )
                );
    }
}
