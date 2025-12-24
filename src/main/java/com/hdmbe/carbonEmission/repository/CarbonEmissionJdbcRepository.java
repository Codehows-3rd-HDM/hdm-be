package com.hdmbe.carbonEmission.repository;

import com.hdmbe.carbonEmission.entity.CarbonEmissionDailyLog;
import com.hdmbe.carbonEmission.entity.CarbonEmissionMonthlyLog;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CarbonEmissionJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    // ✅ [추가] 일별(Daily) 데이터 대량 저장 메서드
    @Transactional
    public void saveAllDailyBatch(List<CarbonEmissionDailyLog> logs) {
        // 1. SQL 준비 (테이블명, 컬럼명 확인 필수!)
        // 예: carbon_emission_daily_log 테이블이라고 가정
        String sql = "INSERT INTO carbon_emission_daily_log " +
                "(created_at,daily_calculated_emission,operation_date,updated_at,car_id) " + // 컬럼명 확인!
                "VALUES (?,?,?,?,?)";

        // 2. 배치 실행
        jdbcTemplate.batchUpdate(sql, logs, 1000, // 1000개씩 묶음
                (PreparedStatement ps, CarbonEmissionDailyLog log) -> {
                    // 3. 파라미터 매핑 (순서 중요!)
                    LocalDateTime created = (log.getCreatedAt() != null) ? log.getCreatedAt() : LocalDateTime.now();
                    ps.setTimestamp(1, Timestamp.valueOf(created));
                    ps.setBigDecimal(2, log.getDailyEmission());
                    ps.setDate(3, java.sql.Date.valueOf(log.getOperationDate()));
                    LocalDateTime updated = (log.getUpdatedAt() != null) ? log.getUpdatedAt() : LocalDateTime.now();
                    ps.setTimestamp(4, Timestamp.valueOf(updated));
                    ps.setLong(5, log.getVehicle().getId());
                });
    }

    @Transactional
    public void saveAllMonthlyBatch(List<CarbonEmissionMonthlyLog> logs)
    {


         // 1. 쿼리문
        String sql = "INSERT INTO carbon_emission_monthly_log " +
                     "(created_at, month, total_emission, updated_at, car_id, year) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        // 2. 배치 실행 (1000개씩 끊어서 날려도 되고, 한 번에 다 날려도 됨)
        jdbcTemplate.batchUpdate(sql,
                logs,
                1000, // 한 번에 묶을 사이즈 (보통 1000~2000 추천)
                (PreparedStatement ps, CarbonEmissionMonthlyLog log) -> {
                    // 3. 파라미터 매핑 (순서 중요!)
                    LocalDateTime created = (log.getCreatedAt() != null) ? log.getCreatedAt() : LocalDateTime.now();
                    ps.setTimestamp(1, Timestamp.valueOf(created));
                    ps.setInt(2, log.getMonth());
                    ps.setBigDecimal(3, log.getTotalEmission());
                    LocalDateTime updated = (log.getUpdatedAt() != null) ? log.getUpdatedAt() : LocalDateTime.now();
                    ps.setTimestamp(4, Timestamp.valueOf(updated));
                    ps.setLong(5, log.getVehicle().getId());
                    ps.setInt(6, log.getYear());
                });
    }

    // 월별 데이터 Upsert (있으면 더하고, 없으면 생성)
    @Transactional
    public void saveAllMonthlyUpsertBatch(List<CarbonEmissionMonthlyLog> logs) {

        // MySQL 전용 문법: 이미 같은 (year, month, car_id)가 있으면 배출량을 더해라!
        String sql = "INSERT INTO carbon_emission_monthly_log " +
                "(created_at, month, total_emission, updated_at, car_id, year) " +
                "VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                "total_emission = total_emission + VALUES(total_emission), " + // 기존 값 + 새 값
                "updated_at = VALUES(updated_at)"; // 수정시간 갱신

        jdbcTemplate.batchUpdate(sql,
                logs,
                1000,
                (PreparedStatement ps, CarbonEmissionMonthlyLog log) -> {
                    // Null 체크 포함한 파라미터 매핑 (아까와 동일)
                    LocalDateTime created = (log.getCreatedAt() != null) ? log.getCreatedAt() : LocalDateTime.now();
                    ps.setTimestamp(1, Timestamp.valueOf(created));

                    ps.setInt(2, log.getMonth());
                    ps.setBigDecimal(3, log.getTotalEmission()); // 여기서 넣은 값이 위의 VALUES(total_emission)이 됨

                    LocalDateTime updated = (log.getUpdatedAt() != null) ? log.getUpdatedAt() : LocalDateTime.now();
                    ps.setTimestamp(4, Timestamp.valueOf(updated));

                    ps.setLong(5, log.getVehicle().getId());
                    ps.setInt(6, log.getYear());
                });
    }
}
