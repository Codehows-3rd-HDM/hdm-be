package com.hdmbe.supplyType.service;

import com.hdmbe.supplyType.dto.SupplyTypeRequestDto;
import com.hdmbe.supplyType.dto.SupplyTypeResponseDto;
import com.hdmbe.supplyType.entity.SupplyType;
import com.hdmbe.supplyType.repository.SupplyTypeRepository;
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
    public Page<SupplyTypeResponseDto> search(
            String supplyTypeName,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        return supplyTypeRepository.search(
                        supplyTypeName,
                        pageable
                )
                .map(SupplyTypeResponseDto::fromEntity);
    }
}