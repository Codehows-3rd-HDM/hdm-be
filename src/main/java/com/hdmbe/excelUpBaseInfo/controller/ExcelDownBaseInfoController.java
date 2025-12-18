package com.hdmbe.excelUpBaseInfo.controller;

import com.hdmbe.excelUpBaseInfo.dto.ExcelDownBaseInfoDto;
import com.hdmbe.excelUpBaseInfo.service.ExcelDownBaseInfoService;
import com.hdmbe.excelUpBaseInfo.service.ExcelUpBaseInfoService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/admin/excel")
@RequiredArgsConstructor
public class ExcelDownBaseInfoController {
    private final ExcelDownBaseInfoService excelDownBaseInfoService;

    @GetMapping("/download/base-info")
    public ResponseEntity<ByteArrayResource> downloadBaseInfoExcel() {

        // 2. 엑셀 생성 (byte[])
        byte[] excelBytes = excelDownBaseInfoService.downloadBaseInfoExcel();

        // 2-1. 파일명 설정 (한글 깨짐 방지 인코딩)
        String fileName = URLEncoder.encode("기준정보_리스트.xlsx", StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백 처리

        // 3. 응답 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=base-info.xlsx"
        );

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(new ByteArrayResource(excelBytes));
    }
}
