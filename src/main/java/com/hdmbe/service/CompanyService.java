package com.hdmbe.service;

import com.hdmbe.dto.CompanyRequestDto;
import com.hdmbe.dto.CompanyResponseDto;
import com.hdmbe.entity.Company;
import com.hdmbe.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository repository;

    @Transactional
    public CompanyResponseDto create(CompanyRequestDto dto) {

        // 업체명 중복 방지
        if (repository.existsByCompanyName(dto.getCompanyName())) {
            throw new RuntimeException("이미 등록된 업체명입니다: " + dto.getCompanyName());
        }

        Company saved = repository.save(
                Company.builder()
                        .companyName(dto.getCompanyName())
                        .oneWayDistance(dto.getOneWayDistance())
                        .address(dto.getAddress())
                        .processId(dto.getProcessId())
                        .classId(dto.getClassId())
                        .remark(dto.getRemark())
                        .build()
        );

        return new CompanyResponseDto(
                saved.getId(),
                saved.getCompanyName(),
                saved.getOneWayDistance(),
                saved.getAddress(),
                saved.getProcessId(),
                saved.getClassId(),
                saved.getRemark()
        );
    }
}
