package com.hdmbe.company.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.company.repository.CompanySupplyCustomerMapRepository;
import com.hdmbe.company.repository.CompanySupplyTypeMapRepository;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanySupplyTypeMapRepository companySupplyTypeMapRepository;
    private final CompanySupplyCustomerMapRepository companySupplyCustomerMapRepository;
    private final SupplyTypeRepository supplyTypeRepository;
    private final SupplyCustomerRepository supplyCustomerRepository;

    // 등록
    @Transactional
    public CompanyResponseDto create(CompanyRequestDto request) {
        validateCreate(request);

        // SupplyType 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        SupplyType supplyType;
        if (request.getSupplyTypeId() != null) {
            supplyType = supplyTypeRepository.findById(request.getSupplyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다"));
        } else if (request.getSupplyTypeName() != null && !request.getSupplyTypeName().isEmpty()) {
            List<SupplyType> types = supplyTypeRepository.findAll();
            supplyType = types.stream()
                    .filter(t -> t.getSupplyTypeName().equals(request.getSupplyTypeName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다: " + request.getSupplyTypeName()));
        } else {
            throw new IllegalArgumentException("공급 유형 ID 또는 이름이 필요합니다");
        }

        // SupplyCustomer 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        SupplyCustomer supplyCustomer;
        if (request.getCustomerId() != null) {
            supplyCustomer = supplyCustomerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다"));
        } else if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
            List<SupplyCustomer> customers = supplyCustomerRepository.findAll();
            supplyCustomer = customers.stream()
                    .filter(c -> c.getCustomerName().equals(request.getCustomerName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다: " + request.getCustomerName()));
        } else {
            throw new IllegalArgumentException("공급 고객 ID 또는 이름이 필요합니다");
        }

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .oneWayDistance(request.getOneWayDistance())
                .address(request.getAddress())
                .remark(request.getRemark())
                .build();

        companyRepository.save(company);

        // 매핑 데이터 생성
        CompanySupplyTypeMap supplyTypeMap = CompanySupplyTypeMap.builder()
                .company(company)
                .supplyType(supplyType)
                .build();
        companySupplyTypeMapRepository.save(supplyTypeMap);

        CompanySupplyCustomerMap supplyCustomerMap = CompanySupplyCustomerMap.builder()
                .company(company)
                .supplyCustomer(supplyCustomer)
                .build();
        companySupplyCustomerMapRepository.save(supplyCustomerMap);

        return CompanyResponseDto.fromEntity(company);
    }

    // 전체 조회, 검색
    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> search(
            String companyName,
            String supplyTypeName,
            String supplyCustomerName,
            String address,
            String keyword,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Company> result = companyRepository.search(
                companyName,
                supplyTypeName,
                supplyCustomerName,
                address,
                keyword,
                pageable
        );

        return result.map(CompanyResponseDto::fromEntity);
    }

    // 전체 조회(드롭다운용)
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAll() {
        return companyRepository.findAll().stream()
                .map(CompanyResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 단일 수정
    public CompanyResponseDto updateSingle(Long id, CompanyRequestDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("협력사 없음"));

        validateUpdate(dto);

        if (dto.getCompanyName() != null) {
            company.setCompanyName(dto.getCompanyName());
        }

//        if (dto.getSupplyTypeId() != null) {
//            SupplyType supplyType = supplyTypeRepository.findById(dto.getSupplyTypeId())
//                    .orElseThrow(() -> new EntityNotFoundException("공급 유형 없음"));
//            company.setSupplyType(supplyType);
//        }
//
//        if (dto.getCustomerId() != null) {
//            SupplyCustomer supplyCustomer = supplyCustomerRepository.findById(dto.getCustomerId())
//                    .orElseThrow(() -> new EntityNotFoundException("공급 고객 없음"));
//            company.setSupplyCustomer(supplyCustomer);
//        }
        if (dto.getOneWayDistance() != null) {
            company.setOneWayDistance(dto.getOneWayDistance());
        }

        if (dto.getAddress() != null) {
            company.setAddress(dto.getAddress());
        }

        if (dto.getRemark() != null) {
            company.setRemark(dto.getRemark());
        }

        return CompanyResponseDto.fromEntity(company);
    }

    // 전체 수정
    @Transactional
    public List<CompanyResponseDto> updateMultiple(List<CompanyRequestDto> requests) {
        return requests.stream()
                .map(req -> updateSingle(req.getId(), req))
                .toList();
    }

    // 삭제
    @Transactional
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("협력사 id 없음 =" + id));
        companyRepository.delete(company);
    }

    private void validateCreate(CompanyRequestDto request) {

        if (request.getCompanyName() == null || request.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("업체명 필수");
        }

        if (request.getSupplyTypeId() == null) {
            throw new IllegalArgumentException("공급유형 필수");
        }

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("공급고객 필수");
        }

        if (request.getOneWayDistance() == null) {
            throw new IllegalArgumentException("편도거리 필수");
        }

        if (request.getAddress() == null || request.getAddress().isBlank()) {
            throw new IllegalArgumentException("주소 필수");
        }
    }

    private void validateUpdate(CompanyRequestDto request) {

        if (request.getCompanyName() != null && request.getCompanyName().isBlank()) {
            throw new IllegalArgumentException("업체명 공백 불가");
        }

        if (request.getOneWayDistance() != null
                && request.getOneWayDistance().signum() <= 0) {
            throw new IllegalArgumentException("편도거리는 0보다 커야 함");
        }

        if (request.getAddress() != null && request.getAddress().isBlank()) {
            throw new IllegalArgumentException("주소 공백 불가");
        }
    }
}
