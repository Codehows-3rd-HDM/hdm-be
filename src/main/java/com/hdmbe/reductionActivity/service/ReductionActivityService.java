package com.hdmbe.reductionActivity.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.hdmbe.reductionActivity.dto.CreateActivityDto;
import com.hdmbe.reductionActivity.dto.ResponseActivityDto;
import com.hdmbe.reductionActivity.entity.ReductionActivity;
import com.hdmbe.reductionActivity.reposiroty.ReductionActivityRepository;
import com.hdmbe.reductionActivityPhoto.entity.ReductionActivityPhoto;
import com.hdmbe.reductionActivityPhoto.service.ReductionActivityPhotoService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ReductionActivityService {

    private final ReductionActivityRepository reductionActivityRepository;
    private final ReductionActivityPhotoService photoService;

    public Long createActivity(CreateActivityDto createActivityDto, List<MultipartFile> files) throws Exception {
        ReductionActivity reductionActivity = ReductionActivity.builder()
                .periodStart(createActivityDto.getPeriodStart())
                .periodEnd(createActivityDto.getPeriodEnd())
                .activityName(createActivityDto.getActivityName())
                .activityDetails(createActivityDto.getActivityDetails())
                .costAmount(createActivityDto.getCostAmount())
                .expectedEffect(createActivityDto.getExpectedEffect())
                .build();

        ReductionActivity savedActivity = reductionActivityRepository.save(reductionActivity);

        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                ReductionActivityPhoto photo = new ReductionActivityPhoto();
                photo.setReductionActivity(savedActivity);
                photoService.uploadPhoto(photo, file);
            }
        }
        return savedActivity.getId();
    }

    @Transactional(readOnly = true)
    public List<ResponseActivityDto> getActivities(LocalDate periodStart, LocalDate periodEnd) {
        List<ReductionActivity> activities = reductionActivityRepository.findByPeriodRange(periodStart, periodEnd);
        return activities.stream()
                .map(this::toResponseDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResponseActivityDto getActivity(Long id) {
        ReductionActivity activity = reductionActivityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "활동을 찾을 수 없습니다."));
        return toResponseDto(activity);
    }

    public Long updateActivity(Long id, CreateActivityDto dto, List<MultipartFile> files) throws Exception {
        ReductionActivity activity = reductionActivityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "활동을 찾을 수 없습니다."));

        activity.setPeriodStart(dto.getPeriodStart());
        activity.setPeriodEnd(dto.getPeriodEnd());
        activity.setActivityName(dto.getActivityName());
        activity.setActivityDetails(dto.getActivityDetails());
        activity.setCostAmount(dto.getCostAmount());
        activity.setExpectedEffect(dto.getExpectedEffect());

        // 새 파일이 있을 경우 추가 업로드 (기존 사진은 유지)
        if (files != null) {
            for (MultipartFile file : files) {
                if (file == null || file.isEmpty()) {
                    continue;
                }

                ReductionActivityPhoto photo = new ReductionActivityPhoto();
                photo.setReductionActivity(activity);
                photoService.uploadPhoto(photo, file);
            }
        }

        return activity.getId();
    }

    public void deleteActivity(Long id) {
        ReductionActivity activity = reductionActivityRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "활동을 찾을 수 없습니다."));

        reductionActivityRepository.delete(activity);
    }

    private ResponseActivityDto toResponseDto(ReductionActivity activity) {
        ResponseActivityDto dto = new ResponseActivityDto();
        dto.setId(activity.getId());
        dto.setPeriodStart(activity.getPeriodStart());
        dto.setPeriodEnd(activity.getPeriodEnd());
        dto.setActivityName(activity.getActivityName());
        dto.setActivityDetails(activity.getActivityDetails());
        dto.setCostAmount(activity.getCostAmount());
        dto.setExpectedEffect(activity.getExpectedEffect());

        Optional<String> firstPhotoUrl = activity.getPhotos()
                .stream()
                .map(ReductionActivityPhoto::getPhotoUrl)
                .filter(url -> url != null && !url.isBlank())
                .findFirst();
        dto.setImageUrl(firstPhotoUrl.orElse(null));
        return dto;
    }
}
