package com.hdmbe.SupplyCustomer.service;

import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
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
}
