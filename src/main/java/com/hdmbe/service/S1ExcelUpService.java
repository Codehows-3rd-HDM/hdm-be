package com.hdmbe.service;

import com.hdmbe.dto.S1ExcelUpDto;
import com.hdmbe.entity.S1Log;
import com.hdmbe.repository.EmissionDailyRepository;
import com.hdmbe.repository.EmissionMonthlyRepository;
import com.hdmbe.repository.S1LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class S1ExcelUpService {

    private final S1LogRepository s1LogRepository;
    private final S1EmissionService s1EmissionService; // 계산 서비스 주입!
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;

    // 에스원 업로드 메인 로직
    @Transactional
    public void uploadS1Log(MultipartFile file, int year, int month) throws IOException
    {
        // 1. [청소] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // 2. [파싱&필터링] 엑셀을 읽으면서 "하루 중 가장 빠른 시간"만 남김 + 날짜 검증
        List<S1Log> logList = parseAndFilterS1Excel(file, year, month);

        // ✅ [추가] 데이터가 하나도 없으면 에러 발생시키기!
        if (logList.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일에서 유효한 데이터를 하나도 찾을 수 없습니다. (파일 양식이나 내용을 확인해주세요)");
        }

        // 3. [저장]
        s1LogRepository.saveAll(logList);

        // 4. [계산]
        s1EmissionService.process(logList);

            log.info("{}년 {}월 에스원 데이터(출근 기준) {}건 저장 및 탄소배출량 계산 완료", year, month, logList.size());

    }

    // 내부 메서드 1: 기존 데이터 삭제 (연간, 월간 분기 처리)
    private void deleteExistingData(int year, int month)
    {
        LocalDateTime startDateTime;         // 에스원 원본 데이터 (차량 출입 로그)
        LocalDateTime endDateTime;           // 에스원 원본 데이터 (차량 출입 로그)
        LocalDate startDate;                 // 일별 탄소배출량 (하룻동안 탄소 배출량)
        LocalDate endDate;                   // 일별 탄소배출량 (하룻동안 탄소 배출량)

        if (month == 0)
        {
            // 전체(0) 선택 시 : 1년 치 삭제
            startDateTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
            endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);

            // Daily 로그용 (DATE)
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYear(year);
        }
        else
        {
            YearMonth yearMonth = YearMonth.of(year, month);
            startDateTime = yearMonth.atDay(1).atStartOfDay();
            endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            // Daily 로그용
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYearAndMonth(year, month);
        }

        s1LogRepository.deleteByAccessTimeBetween(startDateTime, endDateTime);

        // 계산된 Daily 로그도 같이 삭제!
        emissionDailyRepository.deleteByOperationDateBetween(startDate, endDate);

        // 3. Monthly 로그는 위 if문 안에서 이미 삭제함
    }

    // 내부 메서드 2: 엑셀 파싱 + 중복 제거 (핵심 로직!) + 유효성 검사
    private List<S1Log> parseAndFilterS1Excel(MultipartFile file, int targetYear, int targetMonth) throws IOException
    {
        // 중복 제거를 위한 Map (KEY: "사번_날짜", Value: DTO)
        Map<String, S1ExcelUpDto> uniqueMap = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream()))
        {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 1. 엑셀 데이터 읽기
                String dateStr = getCellValue(row.getCell(0));
                String name = getCellValue(row.getCell(2));
                String memberId = getCellValue(row.getCell(3));
                String timeStr = getCellValue(row.getCell(7));

                // 필수 값 없으면 패스
                if (dateStr.isEmpty() || name.isEmpty() || memberId.isEmpty() || timeStr.isEmpty()) continue;

                // 2. 날짜 검증 로직 (나이스파크와 동일하게)
                LocalDateTime tempDateTime = parseDateTime(dateStr, timeStr);
                boolean isYearMatch = (tempDateTime.getYear() == targetYear);
                boolean isMonthMatch = (targetMonth == 0) || (tempDateTime.getMonthValue() == targetMonth);

                if (!isYearMatch || !isMonthMatch)
                {
                    String errorMsg = (targetMonth == 0)
                            ? String.format("선택한 %d년 데이터가 아닙니다.", targetYear)
                            : String.format("선택한 %d년 %d월 데이터가 아닙니다.", targetYear, targetMonth);
                    throw new IllegalArgumentException(
                            String.format("%d번째 줄 오류: %s (엑셀 날짜: %s)", i + 1, errorMsg, dateStr)
                    );
                }

                // 3. DTO 생성
                S1ExcelUpDto currentDto = S1ExcelUpDto.builder()
                        .accessDate(dateStr)
                        .employeeName(name)
                        .memberId(memberId)
                        .accessTime(timeStr)
                        .build();

                // 4. [중복 제거 로직] Map 을 이용해 "가장 빠른 시간" 만 남기기
                String key = memberId + "_" + dateStr;    // 유니크 키 생성 ("1001_2025-10-29")

                if (!uniqueMap.containsKey(key))
                {
                    // 처음 본 데이터면 Map 에 넣음
                    uniqueMap.put(key, currentDto);
                }
                else
                {
                    //이미 데이터가 있다면 -> 시간 비교
                    S1ExcelUpDto existingDto = uniqueMap.get(key);
                    if (isFaster(currentDto.getAccessTime(), existingDto.getAccessTime()))
                    {
                        uniqueMap.put(key, currentDto);
                    }
                }
            }
        }

        // 5. 살아남은 DTO 들을 Entity 로 변환
        List<S1Log> resultList = new ArrayList<>();
        for (S1ExcelUpDto dto : uniqueMap.values())
        {
            LocalDateTime finalDateTime = parseDateTime(dto.getAccessDate(), dto.getAccessTime());

            // DTO 에 만들어둔 toEntity(finalDateTime));
            resultList.add(dto.toEntity(finalDateTime));

        }

        return resultList;
    }

    // 유틸 : 시간 문자열 비교 (time1 이 time2 보다 빠르면 true)
    private boolean isFaster(String time1, String time2)
    {
        //"06:51" vs "08:00" -> 문자열 비교로도 충분
        return time1.compareTo(time2) < 0;
    }

    // 유틸 : 셀 값 읽기 (날짜 서식 대응)
    private  String getCellValue (Cell cell)
    {
        if (cell == null) return "";
        switch (cell.getCellType())
        {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                {
                    LocalDateTime date = cell.getLocalDateTimeCellValue();
                    // 1.0 미만이면 시간(Time), 이상이면 날짜(Date)
                    if (cell.getNumericCellValue() < 1.0) {
                        return date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss")); // 초 포함 확인 필요
                    } else {
                        return date.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                }
                return String.valueOf((int) cell.getNumericCellValue());
            default: return "";
        }
    }

    // 유틸: 날짜 + 시간 합치기
    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        // 엑셀 데이터 포맷에 따라 공백이나 초(:ss) 유무 확인 필요
        // 여기서는 "2025-10-29" + " " + "06:51" (또는 06:51:00) 가정
        String dateTimeStr = dateStr + " " + timeStr;

        // 만약 엑셀 시간이 "06:51" 처럼 초가 없다면 아래 패턴 사용 ("yyyy-MM-dd HH:mm")
        // 만약 "06:51:20" 처럼 초가 있다면 "yyyy-MM-dd HH:mm:ss" 사용
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // (팁) 포맷이 섞여있을 경우를 대비해 try-catch로 두 가지 패턴 다 시도해 볼 수도 있음
        return LocalDateTime.parse(dateTimeStr, formatter);
    }
}
