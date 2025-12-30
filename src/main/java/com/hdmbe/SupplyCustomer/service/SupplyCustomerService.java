package com.hdmbe.SupplyCustomer.service;

import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
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
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SupplyCustomerService {

    private final SupplyCustomerRepository supplyCustomerRepository;

    // 등록
    @Transactional
    public SupplyCustomerResponseDto create(SupplyCustomerRequestDto dto) {
        validateCreate(dto);

        if (supplyCustomerRepository.existsByCustomerName(dto.getCustomerName())) {
            throw new RuntimeException("이미 등록된 제품 분류명입니다: " + dto.getCustomerName());
        }

        SupplyCustomer saved = supplyCustomerRepository.save(
                SupplyCustomer.builder()
                        .customerName(dto.getCustomerName())
                        .remark(dto.getRemark())
                        .build()
        );

        return SupplyCustomerResponseDto.fromEntity(saved);
    }

    // 전체 조회 (드롭다운용)
    @Transactional(readOnly = true)
    public List<SupplyCustomerResponseDto> getAll() {
        return supplyCustomerRepository.findAll().stream()
                .map(SupplyCustomerResponseDto::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    // 전체 조회 + 검색
    @Transactional(readOnly = true)
    public Page<SupplyCustomerResponseDto> search(
            String customerName,
            Pageable pageable
    ) {
        System.out.println("[SupplyCustomerService] 공급고객 검색 요청 - customerName: " + customerName
                + ", pageable: " + pageable);

        Pageable mappedPageable = remapSupplyCustomerSort(pageable);

        Page<SupplyCustomer> result = supplyCustomerRepository.search(
                customerName,
                mappedPageable
        );

        System.out.println("[SupplyCustomerService] 공급고객 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(SupplyCustomerResponseDto::fromEntity);
    }

    // 단일 수정
    @Transactional
    public SupplyCustomerResponseDto updateSingle(Long id, SupplyCustomerRequestDto dto) {
        validateUpdate(dto);

        SupplyCustomer customer = supplyCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공급 고객 없음 id=" + id));

        if (dto.getCustomerName() != null) {
            customer.setCustomerName(dto.getCustomerName());
        }

        if (dto.getRemark() != null) {
            customer.setRemark(dto.getRemark());
        }

        return SupplyCustomerResponseDto.fromEntity(customer);
    }

    // 전체 수정
    @Transactional
    public List<SupplyCustomerResponseDto> updateMultiple(List<SupplyCustomerRequestDto> dtoList) {
        return dtoList.stream()
                .map(dto -> updateSingle(dto.getId(), dto))
                .toList();
    }

    // 단일 삭제
    @Transactional
    public void deleteSingle(Long id) {
        SupplyCustomer customer = supplyCustomerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공급 고객 없음 id=" + id));

        supplyCustomerRepository.delete(customer);
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

    private Pageable remapSupplyCustomerSort(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> mappedOrders = pageable.getSort().stream()
                .map(this::mapSupplyCustomerOrder)
                .filter(Objects::nonNull)
                .toList();

        if (mappedOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(mappedOrders));
    }

    private Sort.Order mapSupplyCustomerOrder(Sort.Order order) {
        String property = order.getProperty();
        Sort.Direction direction = order.getDirection();

        return switch (property) {
            case "customerName" ->
                new Sort.Order(direction, "customerName");
            case "note" ->
                new Sort.Order(direction, "remark");
            default ->
                order;
        };
    }

    // 유효성 검사
    private void validateCreate(SupplyCustomerRequestDto dto) {
        if (dto.getCustomerName() == null || dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("공급 고객명 필수");
        }
    }

    private void validateUpdate(SupplyCustomerRequestDto dto) {
        if (dto.getCustomerName() != null && dto.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("공급 고객명 공백 불가");
        }
    }

    @Transactional
    public SupplyCustomer getOrCreate(String name) {
        return supplyCustomerRepository.findByCustomerName(name)
                .orElseGet(() -> supplyCustomerRepository.save(
                SupplyCustomer.builder().customerName(name).build()
        ));
    }

}
