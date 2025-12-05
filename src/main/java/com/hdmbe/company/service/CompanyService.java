package com.hdmbe.company.service;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.entity.Company;
import com.hdmbe.process.entity.ProcessEntity;
import com.hdmbe.productClass.entity.ProductClass;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.process.repository.ProcessRepository;
import com.hdmbe.productClass.repository.ProductClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ProcessRepository processRepository;
    private final ProductClassRepository productClassRepository;

    // 등록
    @Transactional
    public CompanyResponseDto create(CompanyRequestDto dto) {

        ProcessEntity process = processRepository.findById(dto.getProcessId())
                .orElseThrow(() -> new IllegalArgumentException("생산공정을 찾을 수 없습니다"));

        ProductClass productClass = productClassRepository.findById(dto.getProductClassId())
                .orElseThrow(() -> new IllegalArgumentException("생산품목을 찾을 수 없습니다"));

        Company saved = companyRepository.save(
                Company.builder()
                        .companyName(dto.getCompanyName())
                        .oneWayDistance(dto.getOneWayDistance())
                        .address(dto.getAddress())
                        .process(process)
                        .productClass(productClass)
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
        } else if (dto.getProcessIdFilter() != null) {
            result = companyRepository.findByProcessId(dto.getProcessIdFilter());
        } else if (dto.getProductClassIdFilter() != null) {
            result = companyRepository.findByProductClassId(dto.getProductClassIdFilter());
        } else if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            result = companyRepository.searchByKeyword(dto.getKeyword());
        } else {
            throw new IllegalArgumentException("최소 하나의 검색 조건이 필요합니다.");
        }

        return result.stream().map(CompanyResponseDto::fromEntity).toList();
    }
}
