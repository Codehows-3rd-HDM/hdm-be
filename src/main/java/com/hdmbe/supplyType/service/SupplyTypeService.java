package com.hdmbe.supplyType.service;

import com.hdmbe.supplyType.dto.SupplyTypeRequestDto;
import com.hdmbe.supplyType.dto.SupplyTypeResponseDto;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyTypeService {

    private final SupplyTypeRepository supplyTypeRepository;

    // 등록
    @Transactional
    public SupplyTypeResponseDto create(SupplyTypeRequestDto requestDto) {
        SupplyType saved = supplyTypeRepository.save(
                SupplyType.builder()
                        .supplyTypeName(requestDto.getSupplyTypeName())
                        .build()
        );
        return SupplyTypeResponseDto.fromEntity(saved);
    }

    // 전체 조회
    @Transactional(readOnly = true)
    public List<SupplyTypeResponseDto> getAll() {
        return supplyTypeRepository.findAll()
                .stream()
                .map(SupplyTypeResponseDto::fromEntity)
                .toList();
    }

    // 검색
    @Transactional(readOnly = true)
    public List<SupplyTypeResponseDto> search(SupplyTypeRequestDto dto) {

        if (dto.getSupplyTypeNameFilter() == null || dto.getSupplyTypeNameFilter().isEmpty()) {
            throw new IllegalArgumentException("검색 조건을 입력하세요.");
        }

        return supplyTypeRepository
                .findBySupplyTypeNameContaining(dto.getSupplyTypeNameFilter())
                .stream()
                .map(SupplyTypeResponseDto::fromEntity)
                .toList();
    }

    @Transactional
    public SupplyType getOrCreate(String name) {
        return supplyTypeRepository.findBySupplyTypeName(name)
                .orElseGet(() -> supplyTypeRepository.save(
                        SupplyType.builder().supplyTypeName(name).build()
                ));
    }
}