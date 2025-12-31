package com.hdmbe.excelUpBaseInfo.controller;

import com.hdmbe.excelUpBaseInfo.dto.BaseInfoCheckDto;
import com.hdmbe.excelUpBaseInfo.dto.ExcelUpBaseInfoDto;
import com.hdmbe.excelUpBaseInfo.service.BaseInfoCheckService;
import com.hdmbe.excelUpBaseInfo.service.ExcelUpBaseInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/admin/excel")
@RequiredArgsConstructor
public class ExcelUpBaseInfoController {
    private final ExcelUpBaseInfoService excelUpBaseInfoService;
    private final BaseInfoCheckService baseInfoCheckService;

    @PostMapping("/upload/base-info")
    public ResponseEntity<String> uploadBaseInfo(@RequestBody List<ExcelUpBaseInfoDto> dataList) {

        if (dataList == null || dataList.isEmpty()) {
            return ResponseEntity.badRequest().body("업로드할 데이터가 없습니다.");
        }

        excelUpBaseInfoService.uploadMasterData(dataList);

        return ResponseEntity.ok("기준정보 업로드 완료! (" + dataList.size() + "건)");
    }

    @PostMapping("/upload/base-info/check")
    public List<BaseInfoCheckDto> checkBaseInfo(@RequestBody List<ExcelUpBaseInfoDto> dtoList)
    {
        return baseInfoCheckService.checkDataStatus(dtoList);
    }


}


