package com.hdmbe.service;


import com.hdmbe.dto.NiceExcelUpDto;
import com.hdmbe.entity.NiceparkLog;

import com.hdmbe.repository.EmissionDailyRepository;
import com.hdmbe.repository.EmissionMonthlyRepository;
import com.hdmbe.repository.NiceparkLogRepository;
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
import java.time.Month;
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
    private final NiceParkEmissionService niceParkEmissionService; // 계산 서비스 주입!
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;


    // 메인 로직
    @Transactional      // 중간에 에러 나면 삭제된 것도 롤백되어야 함
    public void uploadNiceParkLog(MultipartFile file, int year, int month) throws IOException
    {
        // (1) [삭제] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // (2) [파싱] 엑셀 파일 읽어서 리스트로 변환
        List<NiceparkLog> logList = parseNiceParkExcel(file, year, month);

        // (3) [저장] DB에 일괄 저장
        if (!logList.isEmpty())
        {
            niceparkLogRepository.saveAll(logList);

            // (4) [계산 트리거] "자, 이제 계산해서 일별/월별 장부에 적어!" (✅ 여기가 핵심 연결고리!)
            niceParkEmissionService.process(logList);

            log.info("{}년 {}월 나이스파크 데이터 {}건 저장 및 탄소배출량 계산 완료", year, month, logList.size());
        }
    }

    // 내부 메서드 1: 기존 데이터 삭제
    private void deleteExistingData(int year, int month)
    {
        LocalDateTime startDateTime;    // 나이스파크 원본 데이터 (차량 출입 로그)
        LocalDateTime endDateTime;      // 나이스파크 원본 데이터 (차량 출입 로그)
        LocalDate startDate;            // 일별 탄소배출량 (하룻동안 탄소 배출량)
        LocalDate endDate;              // 일별 탄소배출량 (하룻동안 탄소 배출량)

        if (month == 0)
        {
            // 원본 데이터 (차량 출입 로그)
            // '월-전체' 선택 시 : 해당 연도의 1월1일 ~ 12월 31일 삭제
            startDateTime =  LocalDateTime.of(year,1, 1, 0, 0, 0);
            endDateTime = LocalDateTime.of(year, 12, 31, 23, 59, 59);

            // Daily 탄소 배출량 로그용 (DATE)
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYear(year);
        }
        else
        {
            // '특정 월' 선택시 : 해당 월의 1일 ~ 말일 삭제
            // 해당 월의 1일 00:00:00 부터 ~ 해당 월의 마지막 날 23:59:59 까지 범위 설정
            YearMonth yearMonth = YearMonth.of(year, month);
            startDateTime = yearMonth.atDay(1).atStartOfDay();
            endDateTime = yearMonth.atEndOfMonth().atTime(23, 59, 59);

            // Daily 로그용
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYearAndMonth(year, month);
        }

        niceparkLogRepository.deleteByAccessTimeBetween(startDateTime, endDateTime);

        // 계산된 Daily 로그도 같이 삭제!
        emissionDailyRepository.deleteByOperationDateBetween(startDate, endDate);

        // Monthly 로그는 위 if문 안에서 이미 삭제함
    }

    // 내부 메서드 2: 엑셀 파싱 (Apache POI 사용)
    private List<NiceparkLog> parseNiceParkExcel(MultipartFile file, int targetYear, int targetMonth) throws  IOException
    {
        List<NiceparkLog> resultList = new ArrayList<>();

        //엑셀 파일 열기
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);  // 첫 번째 시트 가져오기

            // i = 1 부터 시작 (0번 줄은 제목(Header) 이니깐 건너뜀)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
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

                // 1. 연도는 무조건 일치해야 함.
                boolean isYearMatch = (accessTime.getYear() == targetYear);

                // 2. 월은 targetMonth가 0이면(전체) 무조건 통과, 아니면 일치해야 함.
                boolean isMonthMatch = (targetMonth == 0) || (accessTime.getMonthValue() == targetMonth);

                // 엑셀 날짜의 연도나 월이, 사용자가 선택한 것과 다르면? -> 에러 뻥!
                if (!isYearMatch || !isMonthMatch) {
                    // 에러 메시지도 상황에 따라 다르게
                    String errorMsg = (targetMonth == 0)
                            ? String.format("선택한 %d년 데이터가 아닙니다.", targetYear)
                            : String.format("선택한 %d년 %d월 데이터가 아닙니다.", targetYear, targetMonth);

                    throw new IllegalArgumentException(
                            String.format("%d번째 줄 오류: %s (엑셀 날짜: %s)", i + 1, errorMsg, dateStr)
                    );
                }

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

            case NUMERIC:
                // 1. 날짜 서식인지 확인
                if (DateUtil.isCellDateFormatted(cell)) {
                    LocalDateTime date = cell.getLocalDateTimeCellValue();

                    // 날짜만 있는지, 시간만 있는지, 둘 다 있는지 판단해서 포맷팅
                    // (나이스파크는 날짜칸/시간칸이 따로 있으니 각각 처리)

                    // 값이 1.0 미만이면 시간(Time) 정보임 (예: 0.72 -> 17:26:20)
                    if (cell.getNumericCellValue() < 1.0) {
                        return date.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
                    }
                    // 값이 1.0 이상이면 날짜(Date) 정보임 (예: 45959 -> 2025-10-29)
                    else {
                        return date.toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    }
                }
                // 2. 진짜 그냥 숫자면 문자열로 변환
                return String.valueOf((int) cell.getNumericCellValue()); // 1234.0 -> "1234"
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
