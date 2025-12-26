package com.hdmbe.inquiry.controller;

import com.hdmbe.inquiry.dto.FuelTypeRequestDto;
import com.hdmbe.inquiry.dto.FuelTypeResponseDto;
import com.hdmbe.inquiry.service.FuelTypeService;
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
