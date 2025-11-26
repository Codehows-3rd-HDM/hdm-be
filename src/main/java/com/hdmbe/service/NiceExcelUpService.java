package com.hdmbe.service;

import com.hdmbe.dto.NiceExcelUpDto;
import com.hdmbe.entity.NiceparkLog;
import com.hdmbe.repository.NiceparkLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NiceExcelUpService
{
    private final NiceparkLogRepository niceparkLogRepository;

    // 메인 로직
    @Transactional      // 중간에 에러 나면 삭제된 것도 롤백되어야 함
    public void uploadNiceParkLog(MultipartFile file, int year, int month) throws IOException
    {
        // (1) [삭제] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // (2) [파싱] 엑셀 파일 읽어서 리스트로 변환
        List<NiceparkLog> logList = parseNiceParkExcel(file);

        // (3) [저장] DB에 일괄 저장
        if (!logList.isEmpty())
        {
            niceparkLogRepository.saveAll(logList);
            log.info("{}년 {}월 나이스파크 데이터 {}건 저장 완료", year, month, logList.size());
        }
    }

    // 내부 메서드 1: 기존 데이터 삭제
    private void deleteExistingData(int year, int month)
    {
        // 해당 월의 1일 00:00:00 부터 ~ 해당 월의 마지막 날 23:59:59 까지 범위 설정
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        niceparkLogRepository.deleteByAccessTimeBetween(start, end);
    }

    // 내부 메서드 2: 엑셀 파싱 (Apache POI 사용)
    private List<NiceparkLog> parseNiceParkExcel(MultipartFile file) throws  IOException
    {
        List<NiceparkLog> resultList = new ArrayList<>();

        //엑셀 파일 열기
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream()))
        {
            Sheet sheet = workbook.getSheetAt(0);  // 첫 번째 시트 가져오기

            // i = 1 부터 시작 (0번 줄은 제목(Header) 이니깐 건너뜀)
            for (int i = 1; i < sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 셀 데이터 읽기 (순서는 엑셀 파일 양식에 맞춰야함. 예시는 가상의 순서)
                // 가정: 0번열=순번, 1번열=차량번호, 2번열 입차일자, 3번열=입차시간
                //실제 엑셀 파일을 보고 이 숫자를 조정해야 합니다!
                String carNumber = getCellValue(row.getCell(1));
                String dateStr = getCellValue(row.getCell(2));      // "2025-10-29"
                String timeStr = getCellValue(row.getCell(3));      // "17:26:20"

                // 데이터가 비어있으면 패스
                if (carNumber.isEmpty() || dateStr.isEmpty() || timeStr.isEmpty()) continue;

                // 날짜와 시간을 합쳐서 LocalDateTime 으로 변환
                // (DTO를 거쳐서 Entity로 만드는 게 정석이지만, 로직이 간단해서 바로 만듦)
                LocalDateTime accessTime = parseDateTime(dateStr, timeStr);

                // DTO 생성 및 Entity 변환
                NiceExcelUpDto dto = NiceExcelUpDto.builder()
                        .carNumber(carNumber)
                        .accessDate(dateStr)
                        .accessTime(timeStr)
                        .build();

                resultList.add(dto.toEntity(accessTime));
            }
        }
        return resultList;
    }

    // 유틸: 셀 값을 무조건 문자열로 가져오기 (숫자나 빈 칸 처리)
    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC: return String.valueOf((int) cell.getNumericCellValue()); // 1234.0 -> "1234"
            default: return "";
        }
    }

    // 유틸: 날짜(String) + 시간(String) -> LocalDateTime 변환
    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        // 엑셀 포맷에 따라 "yyyy-MM-dd HH:mm:ss" 패턴은 수정될 수 있음
        String dateTimeStr = dateStr + " " + timeStr; // "2025-10-29 17:26:20"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTimeStr, formatter);
    }

}
