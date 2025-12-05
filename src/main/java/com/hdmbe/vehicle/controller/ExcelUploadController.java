package com.hdmbe.vehicle.controller;

import com.hdmbe.niceParkLog.service.NiceExcelUpService;
import com.hdmbe.s1Log.service.S1ExcelUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/excel")
@RequiredArgsConstructor
public class ExcelUploadController {

    private final NiceExcelUpService niceExcelUpService;
    private final S1ExcelUpService s1ExcelUpService;

    // ✅ [보안 설정] SUPERADMIN 또는 ADMIN만 실행 가능
    // (VIEWER가 시도하면 403 Forbidden 에러가 자동으로 뜹니다)
    @PostMapping("/nicepark/excel/upload/")
    public ResponseEntity<?> uploadNiceParkLog(
            @RequestParam("file") MultipartFile file,
            @RequestParam("year") int year,
            @RequestParam("month") int month
            )
    {
        // 1. 파일이 비어있는지 체크
        if (file.isEmpty())
        {
            return ResponseEntity.ok("업로드할 엑셀 파일이 없습니다.");
        }

        try
        {
            // 2. 서비스 호출 (삭제->파싱->저장)
            niceExcelUpService.uploadNiceParkLog(file, year, month);

            return ResponseEntity.ok("나이스파크 데이터 업로드가 성공적으로 완료되었습니다.");
        }

        catch (IOException e)
        {
            // 파일 읽기 실패 시
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("엑셀 파일 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        catch (Exception e)
        {
            // 그 외 에러 (DB 오류 등)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/s1/excel/upload")
    public ResponseEntity<?> uploadS1Log(
            @RequestParam("file") MultipartFile file,
            @RequestParam("year") int year,
            @RequestParam("month") int month )
    {
        // 1. 파일 비어있는지 체크
        if (file.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업로드할 엑셀 파일이 없습니다.");
        }

        try
        {
            // 2. 서비스 호출 (삭제 -> 파싱&필터링 -> 저장)
            s1ExcelUpService.uploadS1Log(file, year, month);

            return ResponseEntity.ok("에스원 데이터(출근 기준) 업로드가 성공적으로 완료되었습니다.");
        }
        catch (Exception e)
        {
            // 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에스원 업로드 실패: " + e.getMessage());
        }
    }
}
