package com.hdmbe.excelUpBaseInfo.service;

import com.hdmbe.excelUpBaseInfo.dto.ExcelDownBaseInfoDto;
import com.hdmbe.excelUpBaseInfo.repository.ExcelBaseInfoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelDownBaseInfoServiceImpl implements ExcelDownBaseInfoService {
    private final ExcelBaseInfoRepository excelBaseInfoRepository;

    @Override
    public byte[] downloadBaseInfoExcel() {
        try {
            // 1️⃣ DB 조회
            List<ExcelDownBaseInfoDto> data = excelBaseInfoRepository.findBaseInfoForExcel();

            // 2️⃣ 엑셀 생성 (데이터가 없어도 빈 엑셀은 내려주는 게 좋음)
            return createExcelFile(data);

        } catch (IOException e) {
            // 로그 찍어주는 게 좋음 (log.error("...", e))
            throw new RuntimeException("엑셀 파일 생성 실패", e);
        }
    }

    private byte[] createExcelFile(List<ExcelDownBaseInfoDto> data) throws IOException {

        // try-with-resources 쓰면 close() 자동으로 해줘서 더 안전함
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("기준정보");

            // 헤더 생성
            Row header = sheet.createRow(0);
            String[] columns = {
                    "순번",             // 0 (New!)
                    "차량 번호",          // 1
                    "소유주",               // 2
                    "사원 번호",          // 3
                    "협력사",               // 4
                    "공급유형",            // 5
                    "공급고객",            // 6
                    "Scope",              // 7
                    "운행 목적",           // 8
                    "주소",               // 9
                    "편도거리 (km)",       // 10
                    "차종",               // 11
                    "차종구분 (대분류)",   // 12
                    "차종구분 (소분류)",   // 13
                    "연료 종류",           // 14
                    "연비 (ℓ/km)",        // 15
                    "탄소 배출 계수"       // 16
            };

            // 🔥 [핵심 2] 줄바꿈(\r\n)이 엑셀에서 보이려면 스타일 설정이 필요함
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setWrapText(true); // 줄바꿈 허용
            headerStyle.setAlignment(HorizontalAlignment.CENTER); // 가운데 정렬
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 수직 가운데 정렬

            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            // 데이터 채우기
            int rowIdx = 1;
            for (ExcelDownBaseInfoDto dto : data) {
                Row row = sheet.createRow(rowIdx++);
                //[0] 순번 (rowIdx가 1부터 시작하므로 그대로 넣으면 1, 2, 3...)
                row.createCell(0).setCellValue(rowIdx);
                row.createCell(1).setCellValue(dto.getCarNumber());
                row.createCell(2).setCellValue(dto.getEmployeeName());
                row.createCell(3).setCellValue(dto.getDriverMemberId());
                row.createCell(4).setCellValue(dto.getCompanyName());
                row.createCell(5).setCellValue(dto.getSupplyTypeName());
                row.createCell(6).setCellValue(dto.getSupplyCustomerName());
                // (1) Scope (숫자니까 null 체크)
                if (dto.getDefaultScope() != null) {
                    row.createCell(7).setCellValue(dto.getDefaultScope());
                } else {
                    row.createCell(7).setCellValue("");
                }
                // 문자열은 null이면 "" 빈값 처리 (선택사항, 안 해도 죽지는 않음)
                row.createCell(8).setCellValue(dto.getPurposeName());
                row.createCell(9).setCellValue(dto.getAddress());
                // 숫자형 Null 체크
                setNumericCell(row, 10, dto.getDistanceInput());
                row.createCell(11).setCellValue(dto.getCarModelName());
                row.createCell(12).setCellValue(dto.getBigCategory());
                row.createCell(13).setCellValue(dto.getSmallCategory());
                // (2) FuelType (Enum이니까 .name() 사용)
                if (dto.getFuelType() != null) {
                    row.createCell(14).setCellValue(dto.getFuelType().name()); // "가솔린" 출력
                } else {
                    row.createCell(14).setCellValue("");
                }
                // 숫자형 Null 체크
                setNumericCell(row, 15, dto.getEfficiency());
                // 숫자형 Null 체크 (이거 안 하면 에러남)
                setNumericCell(row, 16, dto.getEmissionFactor());
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }

    // 💡 Null 방지용 헬퍼 메서드 (숫자용)
    private void setNumericCell(Row row, int cellIndex, BigDecimal value) {
        if (value != null) {
            row.createCell(cellIndex).setCellValue(value.doubleValue());
        } else {
            row.createCell(cellIndex).setCellValue(0); // null이면 0 또는 빈칸
        }
    }
}
