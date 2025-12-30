package com.hdmbe.reductionActivityPhoto.service;

import com.hdmbe.reductionActivityPhoto.entity.ReductionActivityPhoto;
import com.hdmbe.reductionActivityPhoto.repository.ReductionActivityPhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ReductionActivityPhotoService {
    @Value("${uploadLocation}")
    String uploadLocation;

    private final FileService fileService;
    private final ReductionActivityPhotoRepository reductionActivityPhotoRepository;

    public void uploadPhoto(ReductionActivityPhoto reductionActivityPhoto, MultipartFile file) throws Exception{
        String originalFileName = file.getOriginalFilename();
        String imgName = "";
        String photoUrl = "";

        if(!StringUtils.isEmpty(originalFileName)){
            imgName = fileService.uploadFile(uploadLocation, originalFileName, file.getBytes());
            photoUrl = "/photos/" + imgName;
        }

        reductionActivityPhoto.setPhotoUrl(photoUrl);
        reductionActivityPhoto.setUploadedAt(LocalDateTime.now());

        reductionActivityPhotoRepository.save(reductionActivityPhoto);
    }
}
