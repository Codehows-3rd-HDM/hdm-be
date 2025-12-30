package com.hdmbe.reductionActivity.controller;

import com.hdmbe.reductionActivity.dto.CreateActivityDto;
import com.hdmbe.reductionActivity.entity.ReductionActivity;
import com.hdmbe.reductionActivity.service.ReductionActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ReductionActivityController {
    private final ReductionActivityService reductionActivityService;

    @PostMapping("/create")
    public ResponseEntity<?> createReductionActivity(@Valid @ModelAttribute CreateActivityDto createActivityDto, @RequestPart(value = "files", required = false) List<MultipartFile> files) throws Exception {
        Long id = reductionActivityService.createActivity(createActivityDto, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }
}
