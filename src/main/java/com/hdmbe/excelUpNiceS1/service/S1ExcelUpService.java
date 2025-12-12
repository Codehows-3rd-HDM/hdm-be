package com.hdmbe.excelUpNiceS1.service;

import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;
import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.carbonEmission.service.S1EmissionService;
import com.hdmbe.excelUpNiceS1.dto.S1ExcelUpDto;
import com.hdmbe.excelUpNiceS1.entity.S1Log;
import com.hdmbe.excelUpNiceS1.repository.S1LogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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
    public void uploadS1Log(List<S1ExcelUpDto> dtoList, int year, int month) {
        // 1. [청소] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // 2. [검증 및 변환] DTO 리스트 -> Entity 리스트
        List<S1Log> logList = convertAndValidate(dtoList, year, month);

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
    private void deleteExistingData(int year, int month) {
        LocalDate startDate;                 // 일별 탄소배출량 (하룻동안 탄소 배출량)
        LocalDate endDate;                   // 일별 탄소배출량 (하룻동안 탄소 배출량)

        if (month == 0) {
            // Daily 로그용 (DATE)
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYear(year);
        } else {
            YearMonth yearMonth = YearMonth.of(year, month);

            // Daily 로그용
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();

            // Monthly 로그 삭제
            emissionMonthlyRepository.deleteByYearAndMonth(year, month);
        }

        s1LogRepository.deleteByAccessDateBetween(startDate, endDate);

        // 계산된 Daily 로그도 같이 삭제!
        emissionDailyRepository.deleteByOperationDateBetween(startDate, endDate);

        // 3. Monthly 로그는 위 if문 안에서 이미 삭제함
    }

    // 내부 메서드 2: DTO -> Entity 변환 및 날짜 검증 (POI 제거됨!)
    private List<S1Log> convertAndValidate(List<S1ExcelUpDto> dtoList, int targetYear, int targetMonth) {
        List<S1Log> resultList = new ArrayList<>();

            for (int i = 0; i < dtoList.size(); i++) {
                S1ExcelUpDto dto = dtoList.get(i);

                // 필수값 체크 (데이터가 비어있으면 건너뜀)
                if (dto.getAccessDate() == null || dto.getMemberId() == null || dto.getEmployeeName() == null) continue;

                // 1. 날짜 변환 (String -> LocalDate)
                // 엑셀 날짜 문자열 -> LocalDate로 바로 변환
                LocalDate accessDate = parseDate(dto.getAccessDate());

                // 2. 날짜 검증 (선택한 연/월과 엑셀 데이터가 맞는지)
                boolean isYearMatch = (accessDate.getYear() == targetYear);
                boolean isMonthMatch = (targetMonth == 0) || (accessDate.getMonthValue() == targetMonth);

                if (!isYearMatch || !isMonthMatch) {
                    String errorMsg = (targetMonth == 0)
                            ? String.format("선택한 %d년 데이터가 아닙니다.", targetYear)
                            : String.format("선택한 %d년 %d월 데이터가 아닙니다.", targetYear, targetMonth);
                    throw new IllegalArgumentException(
                            String.format("%d번째 줄 오류: %s (엑셀 날짜: %s)", i + 1, errorMsg, dto.getAccessDate())
                    );
                }

                // 3. Entity로 변환해서 리스트에 추가
                // (DTO에 만들어둔 toEntity 메서드 활용)
                resultList.add(dto.toEntity(accessDate));
            }
            return resultList;
        }
    // 유틸: 날짜 파싱 (엑셀에서 "2025-10-29" 형태로 온다고 가정)
    private LocalDate parseDate(String dateStr) {
        // 혹시 엑셀 서식 때문에 "2025-10-29"가 아니라 다른 포맷으로 올 수도 있으니 예외처리나 로그 확인 필요
        // 일단 기본 포맷으로 가정
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
