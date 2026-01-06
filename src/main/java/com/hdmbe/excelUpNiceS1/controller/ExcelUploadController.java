package com.hdmbe.excelUpNiceS1.controller;

import com.hdmbe.excelUpNiceS1.dto.NiceExcelCheckDto;
import com.hdmbe.excelUpNiceS1.dto.NiceExcelUpDto;
import com.hdmbe.excelUpNiceS1.dto.S1ExcelCheckDto;
import com.hdmbe.excelUpNiceS1.service.LogCheckService;
import com.hdmbe.excelUpNiceS1.service.NiceExcelUpService;
import com.hdmbe.excelUpNiceS1.dto.S1ExcelUpDto;
import com.hdmbe.excelUpNiceS1.service.S1ExcelUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/excel")
@RequiredArgsConstructor
public class ExcelUploadController {

    private final NiceExcelUpService niceExcelUpService;
    private final S1ExcelUpService s1ExcelUpService;
    private final LogCheckService logCheckService;

    // [보안 설정] SUPERADMIN 또는 ADMIN만 실행 가능
    // (VIEWER가 시도하면 403 Forbidden 에러가 자동으로 뜹니다)
    @PostMapping("/upload/nicepark")
    public ResponseEntity<?> uploadNiceParkLog(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestBody List<NiceExcelUpDto> dtoList
            )
    {
        // 1. 파일이 비어있는지 체크
        if (dtoList == null || dtoList.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("업로드할 데이터가 없습니다.");
        }
        try {
            // 서비스로부터 제외된 차량 리스트를 받습니다.
            List<String> excludedList = niceExcelUpService.uploadNiceParkLog(dtoList, year, month);

            String successMsg = "나이스파크 데이터 업로드가 완료되었습니다.";

            // 제외된 차량이 있다면 메시지 뒤에 덧붙여줍니다.
            if (excludedList != null && !excludedList.isEmpty()) {
                String excludedCarsStr = String.join(", ", excludedList);
                successMsg += "\n(주의: " + excludedCarsStr + " 차량은 임직원 차량으로 판명되어 계산에서 제외되었습니다.)";
            }

            return ResponseEntity.ok(successMsg);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/is-valid/nicepark")
    public ResponseEntity<List<NiceExcelCheckDto>> isValidNiceParkLog(@RequestBody List<NiceExcelCheckDto> dtoList) {
        List<NiceExcelCheckDto> result = niceExcelUpService.getInvalidLogList(dtoList);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/upload/s1")
    public ResponseEntity<?> uploadS1Log(
            @RequestParam("year") int year,
            @RequestParam("month") int month,
            @RequestBody List<S1ExcelUpDto> dtoList)
    {
        // 1. 파일 비어있는지 체크
        if (dtoList == null || dtoList.isEmpty())
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("업로드할 데이터가 없습니다.");
        }
        try
        {
            // 2. 서비스 호출 (삭제 -> 파싱&필터링 -> 저장)
            s1ExcelUpService.uploadS1Log(dtoList, year, month);

            return ResponseEntity.ok("S1 데이터 업로드가 성공적으로 완료되었습니다.");
        }
        catch (Exception e)
        {
            // 에러 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("에스원 업로드 실패: " + e.getMessage());
        }
    }

    @PostMapping("/is-valid/s1")
    public ResponseEntity<List<S1ExcelCheckDto>> isValidS1Log(@RequestBody List<S1ExcelCheckDto> dtoList) {
        List<S1ExcelCheckDto> result = s1ExcelUpService.getInvalidLogList(dtoList);
        return ResponseEntity.ok(result);
    }

    // GET /api/log/check?year=2025&month=0
    @GetMapping("/check")
    public ResponseEntity<?> checkData(@RequestParam("year") int year,
                                       @RequestParam("month") int month,
                                       @RequestParam("source") String source)
    {
        boolean exists = logCheckService.checkDataExists(year, month, source);
        // JSON으로 리턴: { "exists": true }
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
