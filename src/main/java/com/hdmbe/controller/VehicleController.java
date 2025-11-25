package com.hdmbe.controller;

import com.hdmbe.dto.VehicleRequestDto;
import com.hdmbe.dto.VehicleResponseDto;
import com.hdmbe.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService service;

    @PostMapping
    public VehicleResponseDto create(@RequestBody VehicleRequestDto dto) {
        return service.create(dto);
    }
}
