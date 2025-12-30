package com.hdmbe.reductionActivity.service;

import com.hdmbe.reductionActivity.dto.CreateActivityDto;
import com.hdmbe.reductionActivity.entity.ReductionActivity;
import com.hdmbe.reductionActivity.reposiroty.ReductionActivityRepository;
import com.hdmbe.reductionActivityPhoto.entity.ReductionActivityPhoto;
import com.hdmbe.reductionActivityPhoto.service.ReductionActivityPhotoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReductionActivityService {
    private final ReductionActivityRepository reductionActivityRepository;
    private final ReductionActivityPhotoService photoService;

    public Long createActivity(CreateActivityDto  createActivityDto, List<MultipartFile> files) throws Exception {
        ReductionActivity reductionActivity = ReductionActivity.builder()
                .periodStart(createActivityDto.getPeriodStart())
                .periodEnd(createActivityDto.getPeriodEnd())
                .activityName(createActivityDto.getActivityName())
                .activityDetails(createActivityDto.getActivityDetails())
                .costAmount(createActivityDto.getCostAmount())
                .expectedEffect(createActivityDto.getExpectedEffect())
                .build();


        ReductionActivity savedActivity = reductionActivityRepository.save(reductionActivity);

        if(files != null){
            for(MultipartFile file : files){
                if(file == null || file.isEmpty()) continue;

                ReductionActivityPhoto photo = new ReductionActivityPhoto();
                photo.setReductionActivity(savedActivity);
                photoService.uploadPhoto(photo, file);
            }
        }
        return savedActivity.getId();
    }
}
