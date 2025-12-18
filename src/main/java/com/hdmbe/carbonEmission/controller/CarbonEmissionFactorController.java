package com.hdmbe.carbonEmission.controller;

import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorResponse;
import com.hdmbe.carbonEmission.dto.CarbonEmissionFactorUpdateRequest;
import com.hdmbe.carbonEmission.service.CarbonEmissionFactorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/emission-factor")
@CrossOrigin
public class CarbonEmissionFactorController {

    private final CarbonEmissionFactorService service;

    @GetMapping
    public List<CarbonEmissionFactorResponse> getAll() {
        return service.getAll();
    }

    @PutMapping("/{id}")
    public CarbonEmissionFactorResponse update(
            @PathVariable Long id,
            @RequestBody CarbonEmissionFactorUpdateRequest dto
    ) {
        return service.update(id, dto);
    }
}