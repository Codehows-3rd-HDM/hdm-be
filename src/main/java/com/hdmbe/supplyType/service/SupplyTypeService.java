package com.hdmbe.supplyType.service;

import com.hdmbe.company.repository.CompanySupplyTypeMapRepository;
import com.hdmbe.supplyType.dto.SupplyTypeRequestDto;
import com.hdmbe.supplyType.dto.SupplyTypeResponseDto;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
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
public class SupplyTypeService {

    private final SupplyTypeRepository supplyTypeRepository;
    private final CompanySupplyTypeMapRepository companySupplyTypeMapRepository;

    // 등록
    @Transactional
    public SupplyTypeResponseDto create(SupplyTypeRequestDto dto) {
        validateCreate(dto);
        SupplyType saved = supplyTypeRepository.save(
                SupplyType.builder()
                        .supplyTypeName(dto.getSupplyTypeName())
                        .build());
        return SupplyTypeResponseDto.fromEntity(saved);
    }

    // 전체 조회 (드롭다운용)
    @Transactional(readOnly = true)
    public List<SupplyTypeResponseDto> getAll() {
        return supplyTypeRepository.findAll().stream()
                .map(SupplyTypeResponseDto::fromEntity)
                .collect(java.util.stream.Collectors.toList());
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public Page<SupplyTypeResponseDto> search(
            String supplyTypeName,
            Pageable pageable) {
        System.out.println("[SupplyTypeService] 공급유형 검색 요청 - supplyTypeName: " + supplyTypeName
                + ", pageable: " + pageable);

        Pageable mappedPageable = remapSupplyTypeSort(pageable);

        Page<SupplyType> result = supplyTypeRepository.search(
                supplyTypeName,
                mappedPageable);

        System.out.println("[SupplyTypeService] 공급유형 검색 결과 - 총 개수: " + result.getTotalElements()
                + ", 현재 페이지 개수: " + result.getNumberOfElements());

        return result.map(SupplyTypeResponseDto::fromEntity);
    }

    // 단일 수정
    @Transactional
    public SupplyTypeResponseDto updateSingle(Long id, SupplyTypeRequestDto dto) {
        validateUpdate(dto);

        SupplyType supplyType = supplyTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공급 유형 없음 id=" + id));

        if (dto.getSupplyTypeName() != null) {
            supplyType.setSupplyTypeName(dto.getSupplyTypeName());
        }

        return SupplyTypeResponseDto.fromEntity(supplyType);
    }

    // 다중 수정
    @Transactional
    public List<SupplyTypeResponseDto> updateMultiple(List<SupplyTypeRequestDto> dtoList) {
        return dtoList.stream()
                .map(dto -> updateSingle(dto.getId(), dto))
                .toList();
    }

    // 단일 삭제
    @Transactional
    public void deleteSingle(Long id) {
        SupplyType supplyType = supplyTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("공급 유형 없음 id=" + id));
        // 사용 중인 ID 체크
        boolean isUsed = companySupplyTypeMapRepository
                .existsBySupplyTypeIdAndEndDateIsNull(id);

        if (isUsed) {
            throw new IllegalStateException("사용 중인 공급 유형은 삭제할 수 없습니다.");
        }

        supplyTypeRepository.delete(supplyType);
    }

    // 다중 삭제 (통일된 방식)
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
    private void validateCreate(SupplyTypeRequestDto dto) {
        if (dto.getSupplyTypeName() == null || dto.getSupplyTypeName().isBlank()) {
            throw new IllegalArgumentException("공급 유형명 필수");
        }
    }

    private void validateUpdate(SupplyTypeRequestDto dto) {
        if (dto.getSupplyTypeName() != null && dto.getSupplyTypeName().isBlank()) {
            throw new IllegalArgumentException("공급 유형명 공백 불가");
        }
    }

    @Transactional
    public SupplyType getOrCreate(String name) {
        return supplyTypeRepository.findBySupplyTypeName(name)
                .orElseGet(() -> supplyTypeRepository.save(
                SupplyType.builder().supplyTypeName(name).build()));
    }

    private Pageable remapSupplyTypeSort(Pageable pageable) {
        if (pageable == null || pageable.getSort().isUnsorted()) {
            return pageable;
        }

        List<Sort.Order> mappedOrders = pageable.getSort().stream()
                .map(this::mapSupplyTypeOrder)
                .filter(Objects::nonNull)
                .toList();

        if (mappedOrders.isEmpty()) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(mappedOrders));
    }

    private Sort.Order mapSupplyTypeOrder(Sort.Order order) {
        String property = order.getProperty();
        Sort.Direction direction = order.getDirection();

        return switch (property) {
            case "supplyType" ->
                new Sort.Order(direction, "supplyTypeName");
            default ->
                order;
        };
    }
}
