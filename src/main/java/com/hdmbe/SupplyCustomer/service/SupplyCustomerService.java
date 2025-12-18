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
            int page,
            int size
    ) {
        System.out.println("[SupplyCustomerService] 공급고객 검색 요청 - customerName: " + customerName
                + ", page: " + page + ", size: " + size);

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        Page<SupplyCustomer> result = supplyCustomerRepository.search(
                customerName,
                pageable
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
}
