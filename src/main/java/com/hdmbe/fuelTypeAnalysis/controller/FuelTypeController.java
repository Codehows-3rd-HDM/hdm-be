package com.hdmbe.fuelTypeAnalysis.controller;

import com.hdmbe.fuelTypeAnalysis.dto.FuelTypeRequestDto;
import com.hdmbe.fuelTypeAnalysis.dto.FuelTypeResponseDto;
import com.hdmbe.fuelTypeAnalysis.service.FuelTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/view/fuel")
@RequiredArgsConstructor
public class FuelTypeController {

    private final FuelTypeService fuelTypeService;

    @GetMapping
    public List<FuelTypeResponseDto> search(
            FuelTypeRequestDto requestDto
    ) {
        return fuelTypeService.getFuelEmission(requestDto);
    }
}
