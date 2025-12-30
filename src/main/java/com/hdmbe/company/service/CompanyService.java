package com.hdmbe.company.service;

import java.util.List;

import com.hdmbe.company.dto.CompanyRequestDto;
import com.hdmbe.company.dto.CompanyResponseDto;
import com.hdmbe.company.entity.Company;
import com.hdmbe.company.entity.CompanySupplyCustomerMap;
import com.hdmbe.company.entity.CompanySupplyTypeMap;
import com.hdmbe.company.repository.CompanySupplyCustomerMapRepository;
import com.hdmbe.company.repository.CompanySupplyTypeMapRepository;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.company.repository.CompanyRepository;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanySupplyTypeMapRepository companySupplyTypeMapRepository;
    private final CompanySupplyCustomerMapRepository companySupplyCustomerMapRepository;
    private final SupplyTypeRepository supplyTypeRepository;
    private final SupplyCustomerRepository supplyCustomerRepository;
    private final VehicleRepository vehicleRepository;

    // 등록
    @Transactional
    public CompanyResponseDto create(CompanyRequestDto request) {
        validateCreate(request);

        // SupplyType 찾기
        SupplyType supplyType;
        if (request.getSupplyTypeId() != null) {
            supplyType = supplyTypeRepository.findById(request.getSupplyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다"));
        } else if (request.getSupplyTypeName() != null && !request.getSupplyTypeName().isEmpty()) {
            supplyType = supplyTypeRepository.findAll().stream()
                    .filter(t -> t.getSupplyTypeName().equals(request.getSupplyTypeName()))
                    .findFirst()
                    .orElseThrow(
                            () -> new IllegalArgumentException("공급 유형을 찾을 수 없습니다: " + request.getSupplyTypeName()));
        } else {
            throw new IllegalArgumentException("공급 유형 ID 또는 이름 필요");
        }

        // SupplyCustomer 찾기
        SupplyCustomer supplyCustomer;
        if (request.getCustomerId() != null) {
            supplyCustomer = supplyCustomerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다"));
        } else if (request.getCustomerName() != null && !request.getCustomerName().isEmpty()) {
            supplyCustomer = supplyCustomerRepository.findAll().stream()
                    .filter(c -> c.getCustomerName().equals(request.getCustomerName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("공급 고객을 찾을 수 없습니다: " + request.getCustomerName()));
        } else {
            throw new IllegalArgumentException("공급 고객 ID 또는 이름 필요");
        }

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .oneWayDistance(request.getOneWayDistance())
                .address(request.getAddress())
                .remark(request.getRemark())
                .build();

        companyRepository.save(company);

        // SupplyTypeMap 생성
        CompanySupplyTypeMap typeMap = CompanySupplyTypeMap.builder()
                .company(company)
                .supplyType(supplyType)
                .build();
        companySupplyTypeMapRepository.save(typeMap);

        // SupplyCustomerMap 생성
        CompanySupplyCustomerMap customerMap = CompanySupplyCustomerMap.builder()
                .company(company)
                .supplyCustomer(supplyCustomer)
                .build();
        companySupplyCustomerMapRepository.save(customerMap);

        return CompanyResponseDto.fromEntity(company, typeMap, customerMap);
    }

    // 전체 조회, 검색
    @Transactional(readOnly = true)
    public Page<CompanyResponseDto> search(
            String companyName,
            String supplyTypeName,
            String supplyCustomerName,
            String address,
            String keyword,
            Pageable pageable) {
        System.out.println("[CompanyService] 협력사 검색 요청 - companyName: " + companyName
                + ", supplyTypeName: " + supplyTypeName
                + ", supplyCustomerName: " + supplyCustomerName
                + ", address: " + address
                + ", keyword: " + keyword
                + ", pageable: " + pageable);

        Pageable mappedPageable = remapCompanySort(pageable);

        Page<Company> result = companyRepository.search(
                companyName,
                supplyTypeName,
                supplyCustomerName,
                address,
                keyword,
                mappedPageable);

        System.out.println("[CompanyService] 협력사 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(company -> {
            CompanySupplyTypeMap typeMap = companySupplyTypeMapRepository.findByCompanyAndEndDateIsNull(company)
                    .orElse(null);
            CompanySupplyCustomerMap customerMap = companySupplyCustomerMapRepository
                    .findByCompanyAndEndDateIsNull(company).orElse(null);
            return CompanyResponseDto.fromEntity(company, typeMap, customerMap);
        });
    }

    // 전체 조회 (드롭다운용)
    @Transactional(readOnly = true)
    public List<CompanyResponseDto> getAll() {
        return companyRepository.findAll().stream()
                .map(company -> {
                    CompanySupplyTypeMap typeMap = companySupplyTypeMapRepository.findByCompanyAndEndDateIsNull(company)
                            .orElse(null);
                    CompanySupplyCustomerMap customerMap = companySupplyCustomerMapRepository
                            .findByCompanyAndEndDateIsNull(company).orElse(null);
                    return CompanyResponseDto.fromEntity(company, typeMap, customerMap);
                })
                .toList();
    }

    // 단일 수정
    @Transactional
    public CompanyResponseDto updateSingle(Long id, CompanyRequestDto dto) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회사 없음"));

        if (dto.getCompanyName() != null) {
            company.setCompanyName(dto.getCompanyName());
        }
        if (dto.getOneWayDistance() != null) {
            company.setOneWayDistance(dto.getOneWayDistance());
        }

        List<Vehicle> vehicles = vehicleRepository.findByCompany(company);
        for (Vehicle v : vehicles) {
            v.setOperationDistance(dto.getOneWayDistance());
        }

        // 비고
        if (dto.getRemark() != null) {
            company.setRemark(dto.getRemark());
        }

        if (dto.getRegion() != null || dto.getDetailAddress() != null) {
            company.setAddress(dto.getRegion() + " " + dto.getDetailAddress());
        }

        // 공급유형 변경 → endDate 처리
        if (dto.getSupplyTypeId() != null) {
            companySupplyTypeMapRepository.findByCompanyAndEndDateIsNull(company)
                    .ifPresent(m -> m.setEndDate(LocalDate.now()));

            SupplyType type = supplyTypeRepository.findById(dto.getSupplyTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("공급유형 없음"));

            companySupplyTypeMapRepository.save(
                    CompanySupplyTypeMap.builder()
                            .company(company)
                            .supplyType(type)
                            .build());
        }

        // 공급고객 변경 → endDate 처리
        if (dto.getCustomerId() != null) {
            companySupplyCustomerMapRepository.findByCompanyAndEndDateIsNull(company)
                    .ifPresent(m -> m.setEndDate(LocalDate.now()));

            SupplyCustomer customer = supplyCustomerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new IllegalArgumentException("공급고객 없음"));

            companySupplyCustomerMapRepository.save(
                    CompanySupplyCustomerMap.builder()
                            .company(company)
                            .supplyCustomer(customer)
                            .build());
        }

        return CompanyResponseDto.fromEntity(
                company,
                companySupplyTypeMapRepository.findByCompanyAndEndDateIsNull(company).orElse(null),
                companySupplyCustomerMapRepository.findByCompanyAndEndDateIsNull(company).orElse(null));
    }

    // 전체 수정
    @Transactional
    public List<CompanyResponseDto> updateMultiple(List<CompanyRequestDto> requests) {
        return requests.stream()
                .peek(req -> {
                    if (req.getId() == null) {
                        throw new IllegalArgumentException("전체 수정 시 id 필수");
                    }
                    validateUpdate(req);
                })
                .map(req -> updateSingle(req.getId(), req))
                .toList();
    }

    // 단일 삭제
    @Transactional
    public void deleteSingle(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("회사 없음"));

        if (vehicleRepository.existsByCompany(company)) {
            throw new IllegalStateException("차량이 존재하는 회사는 삭제할 수 없습니다.");
        }

        companySupplyTypeMapRepository.deleteByCompany(company);
        companySupplyCustomerMapRepository.deleteByCompany(company);
        companyRepository.delete(company);
    }

    // 다중 삭제
    @Transactional
    public void deleteMultiple(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        for (Long id : ids) {
            deleteSingle(id);
        }
    }

    private Pageable remapCompanySort(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> mappedOrders = pageable.getSort().stream()
                .map(this::mapCompanyOrder)
                .filter(Objects::nonNull)
                .toList();

        if (mappedOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(mappedOrders));
    }

    private Sort.Order mapCompanyOrder(Sort.Order order) {
        String property = order.getProperty();
        Sort.Direction direction = order.getDirection();

        return switch (property) {
            case "supplyTypeName", "customerName" ->
                null; // Collection 필드로 직렬화 불가능하므로 정렬 제외
            case "region" ->
                new Sort.Order(direction, "address");
            default ->
                order;
        };
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

        if (request.getRegion() != null && request.getRegion().isBlank()) {
            throw new IllegalArgumentException("지역 공백 불가");
        }

        if (request.getDetailAddress() != null && request.getDetailAddress().isBlank()) {
            throw new IllegalArgumentException("상세주소 공백 불가");
        }
    }
}
