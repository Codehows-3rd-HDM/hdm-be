package com.hdmbe.excelUpNiceS1.service;

import com.hdmbe.carbonEmission.repository.EmissionDailyRepository;
import com.hdmbe.carbonEmission.repository.EmissionMonthlyRepository;
import com.hdmbe.carbonEmission.service.S1EmissionService;
import com.hdmbe.excelUpNiceS1.dto.S1ExcelCheckDto;
import com.hdmbe.excelUpNiceS1.dto.S1ExcelUpDto;
import com.hdmbe.excelUpNiceS1.entity.S1Log;
import com.hdmbe.excelUpNiceS1.repository.S1LogRepository;
import com.hdmbe.vehicle.entity.Vehicle;
import com.hdmbe.vehicle.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S1ExcelUpService {

    private final S1LogRepository s1LogRepository;
    private final S1EmissionService s1EmissionService; // 계산 서비스 주입!
    private final EmissionDailyRepository emissionDailyRepository;
    private final EmissionMonthlyRepository emissionMonthlyRepository;
    private final VehicleRepository vehicleRepository;

    // 에스원 업로드 메인 로직
    @Transactional
    public void uploadS1Log(List<S1ExcelUpDto> dtoList, int year, int month) {
        // 1. [청소] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // [수정-최적화] DB에 등록된 모든 사번을 한번에 가져와서 Set 으로 만듬 (null 제외)
        Set<String> validMemberIds = new HashSet<>(vehicleRepository.findAllDriverMemberIds());

        // 2. [검증 및 변환] DTO 리스트 -> Entity 리스트
        List<S1Log> logList = convertAndValidate(dtoList, year, month, validMemberIds);

        // ✅ [추가] 데이터가 하나도 없으면 에러 발생시키기!
        if (logList.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일에서 유효한 데이터를 하나도 찾을 수 없습니다. (파일 양식이나 내용을 확인해주세요)");
        }

        // 3. [저장]
        s1LogRepository.saveAll(logList);

        // 4. [계산]
        s1EmissionService.process(logList);

        log.info("{}년 {}월 S1 데이터 업로드 완료 (저장: {}건)", year, month, logList.size());
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

    // 내부 메서드 2: Entity 변환 및 검증 (메인 컨트롤러 역할 + 유효한 놈만 골라 담기)
    private List<S1Log> convertAndValidate(List<S1ExcelUpDto> dtoList, int targetYear, int targetMonth, Set<String> validMemberIds) {
        List<S1Log> resultList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();

        for (int i = 0; i < dtoList.size(); i++) {
            S1ExcelUpDto dto = dtoList.get(i);

            try {
                // 1. 날짜 검증 (아까 만든 validateDate 사용)
                LocalDate accessDate = validateDate(dto.getAccessDate(), targetYear, targetMonth, i);

                // 2. [수정] 사번으로 차량 조회, Set에 사번이 있나?
                if (!validMemberIds.contains(dto.getMemberId())) {
                    log.warn("미등록 사원 데이터 제외됨: {}", dto.getMemberId());
                    continue;
                }

                // 3. Entity 생성
                S1Log entity = S1Log.builder()
                        .accessDate(accessDate)
                        .memberId(dto.getMemberId())
                        .employeeName(dto.getEmployeeName())
                        .build();

                resultList.add(entity);

            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
                errorList.add(e.getMessage());
                // 형님 스타일대로 에러 있으면 멈춤!
                throw e;
            }
        }
        return resultList;
    }
    //날짜 검증 메서드
    // 날짜 파싱 및 연/월 검증 로직 분리
    private LocalDate validateDate(String dateStr, int targetYear, int targetMonth, int rowIndex) {
        // 1. 파싱 (기존 parseDate 활용)
        LocalDate date = parseDate(dateStr);

        // 2. 검증
        boolean isYearMatch = (date.getYear() == targetYear);
        boolean isMonthMatch = (targetMonth == 0) || (date.getMonthValue() == targetMonth);

        if (!isYearMatch || !isMonthMatch) {
            String errorMsg = (targetMonth == 0)
                    ? String.format("선택한 %d년 데이터가 아닙니다.", targetYear)
                    : String.format("선택한 %d년 %d월 데이터가 아닙니다.", targetYear, targetMonth);

            throw new IllegalArgumentException(
                    String.format("%d번째 줄 날짜 오류: %s (입력값: %s)", rowIndex + 1, errorMsg, dateStr)
            );
        }
        return date;
    }

    // ✅ [신규] 사번(MemberId)으로 차량 찾기
//    private void validateVehicleByMemberId(String memberId) {
//        vehicleRepository.findByDriverMemberId(memberId)
//                .orElseThrow(() -> new IllegalArgumentException(
//                        "차량이 등록되지 않은 사원입니다. (사번: " + memberId + ")\n" +
//                                "※ 차량 관리 메뉴에서 해당 사원의 차량을 먼저 등록해주세요."
//                ));
//    }

    // 유틸: 날짜 파싱 (엑셀에서 "2025-10-29" 형태로 온다고 가정)
    private LocalDate parseDate(String dateStr) {
        // 혹시 엑셀 서식 때문에 "2025-10-29"가 아니라 다른 포맷으로 올 수도 있으니 예외처리나 로그 확인 필요
        // 일단 기본 포맷으로 가정
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public List<S1ExcelCheckDto> getInvalidLogList(List<S1ExcelCheckDto> dtoList) {
        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
                .filter((v) -> v.getDriverMemberId() != null)
                .toList();
        return dtoList.stream()
                .filter((e) ->
                        vehicles.stream().noneMatch(v -> v.getDriverMemberId().equals(e.getMemberId()))
        ).toList();
    }
}
