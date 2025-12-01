package com.hdmbe.service;

import com.hdmbe.dto.CompanyRequestDto;
import com.hdmbe.dto.CompanyResponseDto;
import com.hdmbe.dto.CompanySearchDto;
import com.hdmbe.entity.Company;
import com.hdmbe.entity.ProcessEntity;
import com.hdmbe.entity.ProductClass;
import com.hdmbe.repository.CompanyRepository;
import com.hdmbe.repository.ProcessRepository;
import com.hdmbe.repository.ProductClassRepository;
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
    public List<CompanyResponseDto> search(CompanySearchDto dto) {

        return companyRepository.findAll().stream()
                .filter(c -> dto.getCompanyName() == null
                        || c.getCompanyName().contains(dto.getCompanyName()))
                .filter(c -> dto.getProcessId() == null
                        || c.getProcess().getId().equals(dto.getProcessId()))
                .filter(c -> dto.getProductClassId() == null
                        || c.getProductClass().getId().equals(dto.getProductClassId()))
                .filter(c -> dto.getAddress() == null
                        || c.getAddress().contains(dto.getAddress()))
                .map(CompanyResponseDto::fromEntity)
                .toList();
    }
}
