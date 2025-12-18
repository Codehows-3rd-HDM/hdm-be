package com.hdmbe.excelUpNiceS1.service;


import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;
import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.carbonEmission.service.NiceParkEmissionService;
import com.hdmbe.excelUpNiceS1.dto.NiceExcelCheckDto;
import com.hdmbe.excelUpNiceS1.dto.NiceExcelUpDto;

import com.hdmbe.excelUpNiceS1.entity.NiceparkLog;
import com.hdmbe.excelUpNiceS1.repository.NiceparkLogRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class NiceExcelUpService
{
    private final NiceparkLogRepository niceparkLogRepository;
    private final NiceParkEmissionService niceParkEmissionService; // 계산 서비스 주입!
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;
    private final VehicleRepository vehicleRepository;

    // 메인 로직
    @Transactional      // 중간에 에러 나면 삭제된 것도 롤백되어야 함
    public void uploadNiceParkLog(List<NiceExcelUpDto> dtoList, int year, int month) throws IOException
    {
        // (1) [삭제] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // [수정-최적화] DB에 등록된 모든 차량 번호를 한번에 가져와서 Set 으로 만듬 (null 제외)
        Set<String> validCarNumbers = new HashSet<>(vehicleRepository.findAllCarNumbers());

        // 2. [검증 및 변환] DTO 리스트 -> Entity 리스트
        List<NiceparkLog> logList = convertAndValidate(dtoList, year, month,  validCarNumbers);

        // ✅ [추가] 데이터가 하나도 없으면 에러 발생시키기!
        if (logList.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일에서 유효한 데이터를 하나도 찾을 수 없습니다. (파일 양식이나 내용을 확인해주세요)");
        }

        // (3) [저장] DB에 일괄 저장
        niceparkLogRepository.saveAll(logList);

        // (4) [계산 트리거] "자, 이제 계산해서 일별/월별 장부에 적어!" (✅ 여기가 핵심 연결고리!)
        niceParkEmissionService.process(logList);

        log.info("{}년 {}월 나이스파크 데이터 {}건 저장 및 탄소배출량 계산 완료", year, month, logList.size());

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

    // 내부 메서드 2: DTO -> Entity 변환 및 날짜 검증 (POI 제거됨!)
    private List<NiceparkLog> convertAndValidate(List<NiceExcelUpDto> dtoList, int targetYear, int targetMonth, Set<?> validCarNumbers)
    {
        List<NiceparkLog> resultList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        for (int i = 0; i < dtoList.size(); i++) {
            NiceExcelUpDto dto = dtoList.get(i);

                // 필수값 체크 (데이터가 비어있으면 건너뜀)
                if (dto.getCarNumber() == null || dto.getAccessDate() == null || dto.getAccessTime() == null) continue;

                try {
                    // 1. [검증] 날짜, 시간 확인
                    LocalDateTime accessTime = validateDateTime(dto.getAccessDate(), dto.getAccessTime(), targetYear, targetMonth, i);

                    // 2. [검문] 차량번호 조회
                   // validateVehicleByCarNumber(dto.getCarNumber());
                    if (!validCarNumbers.contains(dto.getCarNumber())) {
                        log.warn("미등록 차량 데이터 제외됨: {}", dto.getCarNumber());
                        continue;
                    }

                    // 3. Entity 생성
                    NiceparkLog entity = NiceparkLog.builder()
                            .accessTime(accessTime)     // 합쳐진 시간
                            .carNumber(dto.getCarNumber())
                            .build();

                    resultList.add(entity);
                }
                catch (IllegalArgumentException e)
                {
                    log.error(e.getMessage());
                    errorList.add(e.getMessage());
                    throw e;    // 에러 발생 시 즉시 중단
                }
        }
        return resultList;
    }

    private LocalDateTime validateDateTime(String dateStr, String timeStr, int targetYear, int targetMonth, int rowIndex)
    {
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
                    String.format("%d번째 줄 오류: %s (엑셀 날짜: %s)", rowIndex + 1, errorMsg, dateStr)
            );
        }
        return accessTime;
    }


    // 유틸: 날짜(String) + 시간(String) -> LocalDateTime 변환
    private LocalDateTime parseDateTime(String dateStr, String timeStr) {
        // 1. 시간이 비어있으면 기본값 00:00:00 처리
        if (timeStr == null || timeStr.trim().isEmpty()) {
            timeStr = "00:00:00";
        }

        // 2. 시간 문자열 정리 (혹시 모를 공백 제거)
        timeStr = timeStr.trim();

        // 3. "HH:mm" 형태라면 ":00"을 붙여서 "HH:mm:ss"로 만듦
        if (timeStr.length() == 5) { // 예: "17:26"
            timeStr += ":00";
        }

        // 4. 합치기
        String dateTimeStr = dateStr + " " + timeStr;

        // 5. 파싱
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(dateTimeStr, formatter);
        } catch (Exception e) {
            // 파싱 실패 시 로그 남기고 에러 던짐 (디버깅용)
            log.error("날짜 파싱 실패: input='{}'", dateTimeStr);
            throw new IllegalArgumentException("날짜/시간 형식이 올바르지 않습니다: " + dateTimeStr);
        }
    }

//    private void validateVehicleByCarNumber(String carNumber)
//    {
//        vehicleRepository.findByCarNumber(carNumber)
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "미등록 차량입니다. 기준정보를 먼저 업로드하세요. (차량번호: " + carNumber + ")\n" +
//                        "※ 차량 관리 메뉴에서 해당 차량을 먼저 등록해주세요."
//                ));
//    }

    public List<NiceExcelCheckDto> getInvalidLogList(List<NiceExcelCheckDto> dtoList) {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        return dtoList.stream().filter((e) ->
            vehicles.stream().noneMatch(v -> v.getCarNumber().equals(e.getCarNumber()))
        ).toList();
    }
}
