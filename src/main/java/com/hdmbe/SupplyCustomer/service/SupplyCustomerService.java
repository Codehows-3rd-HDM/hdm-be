package com.hdmbe.SupplyCustomer.service;

import com.hdmbe.SupplyCustomer.dto.SupplyCustomerRequestDto;
import com.hdmbe.SupplyCustomer.dto.SupplyCustomerResponseDto;
import com.hdmbe.SupplyCustomer.entity.SupplyCustomer;
import com.hdmbe.SupplyCustomer.repository.SupplyCustomerRepository;
import lombok.RequiredArgsConstructor;
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

    // 전체 조회
    @Transactional(readOnly = true)
    public List<SupplyCustomerResponseDto> getAll() {
        return supplyCustomerRepository.findAll().stream()
                .map(SupplyCustomerResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<SupplyCustomerResponseDto> search(SupplyCustomerRequestDto dto) {
        List<SupplyCustomer> result;

        if (dto.getCustomerNameFilter() != null && !dto.getCustomerNameFilter().isEmpty()) {
            result = supplyCustomerRepository.findByCustomerNameContaining(dto.getCustomerNameFilter());
        } else {
            throw new IllegalArgumentException("검색 조건이 필요합니다.");
        }

        return result.stream().map(SupplyCustomerResponseDto::fromEntity).toList();
    }

}
