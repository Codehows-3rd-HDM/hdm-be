package com.hdmbe.reductionActivity.controller;

import com.hdmbe.reductionActivity.dto.CreateActivityDto;
import com.hdmbe.reductionActivity.dto.ResponseActivityDto;
import com.hdmbe.reductionActivity.service.ReductionActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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

    @GetMapping("/list")
    public ResponseEntity<List<ResponseActivityDto>> getActivities(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodStart,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate periodEnd
    ) {
        return ResponseEntity.ok(reductionActivityService.getActivities(periodStart, periodEnd));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseActivityDto> getActivity(@PathVariable Long id) {
        return ResponseEntity.ok(reductionActivityService.getActivity(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> updateActivity(
            @PathVariable Long id,
            @Valid @ModelAttribute CreateActivityDto createActivityDto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        Long updatedId = reductionActivityService.updateActivity(id, createActivityDto, files);
        return ResponseEntity.ok(updatedId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        reductionActivityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
