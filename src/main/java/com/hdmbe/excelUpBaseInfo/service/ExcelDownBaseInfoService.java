package com.hdmbe.excelUpBaseInfo.service;

public interface ExcelDownBaseInfoService {

    // 2. 데이터를 엑셀 파일(byte[])로 변환하는 메서드
    byte[] downloadBaseInfoExcel();
}
