package com.hdmbe.company.service;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.entity.Company;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final SupplyTypeRepository supplyTypeRepository;
    private final SupplyCustomerRepository supplyCustomerRepository;

    // 등록
    @Transactional
    public CompanyResponseDto create(CompanyRequestDto dto) {

        SupplyType supplyType = supplyTypeRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("생산공정을 찾을 수 없습니다"));

        SupplyCustomer supplyCustomer = supplyCustomerRepository.findById(dto.getSupplyTypeId())
                .orElseThrow(() -> new IllegalArgumentException("생산품목을 찾을 수 없습니다"));

        Company saved = companyRepository.save(
                Company.builder()
                        .companyName(dto.getCompanyName())
                        .oneWayDistance(dto.getOneWayDistance())
                        .address(dto.getAddress())
                        .supplyCustomer(supplyCustomer)
                        .supplyType(supplyType)
                        .remark(dto.getRemark())
                        .build()
        );

        return CompanyResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAll() {
        return companyRepository.findAll().stream()
                .map(CompanyResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> search(CompanyRequestDto dto) {

        List<Company> result;

        if (dto.getCompanyNameFilter() != null && !dto.getCompanyNameFilter().isEmpty()) {
            result = companyRepository.findByCompanyNameContaining(dto.getCompanyNameFilter());
        } else if (dto.getAddressFilter() != null && !dto.getAddressFilter().isEmpty()) {
            result = companyRepository.findByAddressContaining(dto.getAddressFilter());
        } else if (dto.getCustomerIdFilter() != null) {
            result = companyRepository.findBySupplyCustomerId(dto.getCustomerId());
        } else if (dto.getSupplyTypeIdFilter() != null) {
            result = companyRepository.findBySupplyTypeId(dto.getSupplyTypeIdFilter());
        } else if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            result = companyRepository.searchByKeyword(dto.getKeyword());
        } else {
            throw new IllegalArgumentException("최소 하나의 검색 조건이 필요합니다.");
        }

        return result.stream().map(CompanyResponseDto::fromEntity).toList();
    }
}
