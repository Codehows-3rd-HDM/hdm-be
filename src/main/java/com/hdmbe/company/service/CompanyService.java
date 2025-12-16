package com.hdmbe.company.service;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.entity.Company;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final SupplyTypeRepository supplyTypeRepository;
    private final SupplyCustomerRepository supplyCustomerRepository;

    // 등록
    @Transactional
    public CompanyResponseDto create(CompanyRequestDto request) {
        validateCreate(request);

        // SupplyType 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        SupplyType supplyType;
        if (dto.getSupplyTypeId() != null) {
            supplyType = supplyTypeRepository.findById(dto.getSupplyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다"));
        } else if (dto.getSupplyTypeName() != null && !dto.getSupplyTypeName().isEmpty()) {
            List<SupplyType> types = supplyTypeRepository.findAll();
            supplyType = types.stream()
                    .filter(t -> t.getSupplyTypeName().equals(dto.getSupplyTypeName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다: " + dto.getSupplyTypeName()));
        } else {
            throw new IllegalArgumentException("공급 유형 ID 또는 이름이 필요합니다");
        }

        // SupplyCustomer 찾기: ID가 있으면 ID로, 없으면 이름으로 찾기
        SupplyCustomer supplyCustomer;
        if (dto.getCustomerId() != null) {
            supplyCustomer = supplyCustomerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다"));
        } else if (dto.getCustomerName() != null && !dto.getCustomerName().isEmpty()) {
            List<SupplyCustomer> customers = supplyCustomerRepository.findAll();
            supplyCustomer = customers.stream()
                    .filter(c -> c.getCustomerName().equals(dto.getCustomerName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다: " + dto.getCustomerName()));
        } else {
            throw new IllegalArgumentException("공급 고객 ID 또는 이름이 필요합니다");
        }

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .supplyType(supplyType)
                .supplyCustomer(supplyCustomer)
                .oneWayDistance(request.getOneWayDistance())
                .address(request.getAddress())
                .remark(request.getRemark())
                .build();

        companyRepository.save(company);
        return CompanyResponseDto.fromEntity(company);
    }

    // 전체 조회, 검색
    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> search(
            String companyName,
            String supplyTypeId,
            String supplyCustomerId,
            String address,
            String keyword,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<Company> result = companyRepository.search(
                companyName,
                supplyTypeId,
                supplyCustomerId,
                address,
                keyword,
                pageable);

        return result.map(CompanyResponseDto::fromEntity);
    }

    // 단일 수정
    public CompanyResponseDto updateSingle(Long id, CompanyRequestDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("협력사 없음"));

        validateUpdate(dto);

        if (dto.getCompanyName() != null) {
            company.setCompanyName(dto.getCompanyName());
        }

        if (dto.getSupplyTypeId() != null) {
            SupplyType supplyType = supplyTypeRepository.findById(dto.getSupplyTypeId())
                    .orElseThrow(() -> new EntityNotFoundException("공급 유형 없음"));
            company.setSupplyType(supplyType);
        }

        if (dto.getSupplyCustomerId() != null) {
            SupplyCustomer supplyCustomer = supplyCustomerRepository.findById(dto.getSupplyCustomerId())
                    .orElseThrow(() -> new EntityNotFoundException("공급 고객 없음"));
            company.setSupplyCustomer(supplyCustomer);
        }

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

        if (request.getCompanyName() == null || request.getCompanyName().isBlank())
            throw new IllegalArgumentException("업체명 필수");

        if (request.getSupplyTypeId() == null)
            throw new IllegalArgumentException("공급유형 필수");

        if (request.getSupplyCustomerId() == null)
            throw new IllegalArgumentException("공급고객 필수");

        if (request.getOneWayDistance() == null)
            throw new IllegalArgumentException("편도거리 필수");

        if (request.getAddress() == null || request.getAddress().isBlank())
            throw new IllegalArgumentException("주소 필수");
    }

    private void validateUpdate(CompanyRequestDto request) {

        if (request.getCompanyName() != null && request.getCompanyName().isBlank())
            throw new IllegalArgumentException("업체명 공백 불가");

        if (request.getOneWayDistance() != null
                && request.getOneWayDistance().signum() <= 0)
            throw new IllegalArgumentException("편도거리는 0보다 커야 함");

        if (request.getAddress() != null && request.getAddress().isBlank())
            throw new IllegalArgumentException("주소 공백 불가");
    }
}