package com.hdmbe.carbonEmission.service;

import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorResponse;
import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorUpdateRequest;
import com.hdmbe.carbonEmission.entity.CarbonEmissionFactor;
import com.hdmbe.carbonEmission.repository.CarbonEmissionFactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarbonEmissionFactorService {

    private final CarbonEmissionFactorRepository repo;

    // 조회
    public List<CarbonEmissionFactorResponse> getAll() {
        return repo.findAll()
                .stream()
                .map(CarbonEmissionFactorResponse::fromEntity)
                .toList();
    }

    // 수정
    public CarbonEmissionFactorResponse update(Long id, CarbonEmissionFactorUpdateRequest dto) {
        CarbonEmissionFactor entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 배출계수"));

        entity.setEmissionFactor(dto.getEmissionFactor());
        entity.setRemark(dto.getRemark());

        repo.save(entity);

        return CarbonEmissionFactorResponse.fromEntity(entity);
    }
}