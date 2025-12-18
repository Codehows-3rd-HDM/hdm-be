package com.hdmbe.excelUpBaseInfo.service;

import com.hdmbe.excelUpBaseInfo.dto.ExcelDownBaseInfoDto;
import java.io.IOException;
import java.util.List;

public interface ExcelDownBaseInfoService {

    // 2. 데이터를 엑셀 파일(byte[])로 변환하는 메서드
    byte[] downloadBaseInfoExcel();
}
