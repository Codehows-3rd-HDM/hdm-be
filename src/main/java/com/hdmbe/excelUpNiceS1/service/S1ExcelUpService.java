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
import java.util.*;
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
    public List<String> uploadS1Log(List<S1ExcelUpDto> dtoList, int year, int month) {
        // 1. [청소] 해당 연/월의 기존 데이터 삭제
        deleteExistingData(year, month);

        // [수정] findAllDriverMemberIds()가 만약 distinct 처리가 되어 있다면
        // 12월 1일자 소나타가 누락될 수 있음.
        // 안전하게 findAll()로 가져와서 필터링하는 것이 확실함.
        List<Vehicle> allVehicles = vehicleRepository.findAll().stream()
                .filter(v -> v.getDriverMemberId() != null)
                .collect(Collectors.toList());

        // Set으로 변환 (인자를 Set으로 넘기게 되어있으므로)
        Set<Vehicle> validMembers = new HashSet<>(allVehicles);

        // 2. [검증 및 변환] DTO 리스트 -> Entity 리스트
        List<S1Log> logList = convertAndValidate(dtoList, year, month, validMembers);

        // [추가] 데이터가 하나도 없으면 에러
        if (logList.isEmpty()) {
            throw new IllegalArgumentException("업로드된 파일에서 유효한 데이터를 하나도 찾을 수 없습니다. (파일 양식이나 내용을 확인해주세요)");
        }

        // 3. [저장]
        s1LogRepository.saveAll(logList);

        // 저장 즉시 반영
        s1LogRepository.flush();

        // 4. [계산 트리거]
        // [수정 3] year, month 파라미터 전달 & 결과 리턴
        log.info("{}년 {}월 S1 데이터 업로드 완료 (원본 저장: {}건)", year, month, logList.size());

        return s1EmissionService.process(logList, year, month);
    }

    // 내부 메서드 1: 기존 데이터 삭제 (연간, 월간 분기 처리)
    private void deleteExistingData(int year, int month) {
        LocalDate startDate;                 // 일별 탄소배출량 (하룻동안 탄소 배출량)
        LocalDate endDate;                   // 일별 탄소배출량 (하룻동안 탄소 배출량)

        if (month == 0) {
            // Daily 로그용 (DATE)
            startDate = LocalDate.of(year, 1, 1);
            endDate = LocalDate.of(year, 12, 31);

        } else {
            YearMonth yearMonth = YearMonth.of(year, month);

            // Daily 로그용
            startDate = yearMonth.atDay(1);
            endDate = yearMonth.atEndOfMonth();
        }

        s1LogRepository.deleteByAccessDateBetween(startDate, endDate);

        // 계산된 Daily 로그도 같이 삭제!
        // 수정 S1EmissionService 에서 삭제
      //  emissionDailyRepository.deleteByOperationDateBetween(startDate, endDate);

        // 3. Monthly 로그는 위 if문 안에서 이미 삭제함
    }

    // 내부 메서드 2: Entity 변환 및 검증 (메인 컨트롤러 역할 + 유효한 놈만 골라 담기)
    private List<S1Log> convertAndValidate(List<S1ExcelUpDto> dtoList, int targetYear, int targetMonth, Set<Vehicle> validMembers) {
        List<S1Log> resultList = new ArrayList<>();

        // 1. 사번으로 바로 찾을 수 있게 Map으로 세팅
        // [수정 핵심 1] 사번 하나에 여러 차량이 있을 수 있으므로 List로 묶어야 함
        // 기존: Map<String, Vehicle> -> 사번 중복 시 1개만 남아서 문제 발생
        // 변경: Map<String, List<Vehicle>>
        Map<String, List<Vehicle>> memberHistoryMap = validMembers.stream()
                .collect(Collectors.groupingBy(Vehicle::getDriverMemberId));

        for (int i = 0; i < dtoList.size(); i++) {
            S1ExcelUpDto dto = dtoList.get(i);

            try {
                // 1. 날짜 검증 (아까 만든 validateDate 사용)
                LocalDate accessDate = validateDate(dto.getAccessDate(), targetYear, targetMonth, i);

                // 2. Map에서 사번으로 차량 정보 가져오기
                //Vehicle validMember = memberMap.get(dto.getMemberId());

                // 2. [수정 핵심 2] 이력 리스트 가져오기
                List<Vehicle> historyList = memberHistoryMap.get(dto.getMemberId());

                if (historyList == null || historyList.isEmpty()) {
                    log.warn("미등록 사원 데이터 제외됨: {}", dto.getMemberId());
                    continue;
                }

                // 3. [수정 핵심 3] "그 날짜(accessDate)에 유효했던 차"가 있는지 확인
                // S1EmissionService에 있는 로직과 동일하게 검증해야 함
                Vehicle validVehicle = findValidVehicleForUpload(historyList, accessDate);

                if (validVehicle == null) {
                    // 이력은 있지만, 로그 날짜가 모든 차량의 등록일보다 과거인 경우
                    log.warn("기준일 이전 데이터 제외됨: 사번 {}, 로그날짜 {}", dto.getMemberId(), accessDate);
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
                //  에러 있으면 멈춤
                throw e;
            }
        }
        return resultList;
    }

    // [신규 추가] 업로드 검증용 차량 찾기 메서드 (S1EmissionService 로직과 동일)
    private Vehicle findValidVehicleForUpload(List<Vehicle> historyList, LocalDate logDate) {
        return historyList.stream()
                // 로그 날짜보다 '이전' 혹은 '같은' 날짜에 등록된 차들만 필터링
                .filter(v -> !v.getCalcBaseDate().isAfter(logDate))
                // 최신 등록일 순 정렬
                .sorted((v1, v2) -> v2.getCalcBaseDate().compareTo(v1.getCalcBaseDate()))
                // 첫 번째 녀석 리턴 (없으면 null)
                .findFirst()
                .orElse(null);
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

    // [신규] 사번(MemberId)으로 차량 찾기
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
//        List<Vehicle> vehicles = vehicleRepository.findAll().stream()
//                .filter((v) -> v.getDriverMemberId() != null)
//                .toList();
//        return dtoList.stream()
//                .filter((e) ->
//                        vehicles.stream().noneMatch(v -> v.getDriverMemberId().equals(e.getMemberId()))
//        ).toList();

        // 모든 유효 사번을 Set으로 미리 뽑아둠 (검색 속도 비약적 상승)
        Set<String> validMemberIds = vehicleRepository.findAllDriverMemberIds().stream()
                .map(Vehicle::getDriverMemberId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return dtoList.stream()
                .filter(e -> !validMemberIds.contains(e.getMemberId()))
                .toList();
    }
}
