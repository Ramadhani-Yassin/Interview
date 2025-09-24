package com.interview.WebApp.service;

import com.interview.WebApp.entity.DataRecord;
import com.interview.WebApp.entity.Upload;
import com.interview.WebApp.repository.DataRecordRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

@Service
public class ExportService {

    private final DataRecordRepository dataRecordRepository;

    public ExportService(DataRecordRepository dataRecordRepository) {
        this.dataRecordRepository = dataRecordRepository;
    }

    public void exportToExcel(Upload upload, OutputStream outputStream) throws IOException {
        try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) { // keep 100 rows in memory
            SXSSFSheet sheet = workbook.createSheet("Data");
            List<String> headers = Arrays.asList(upload.getHeaderLine().split(","));

            // header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
            }

            int rowIndex = 1;
            int pageSize = 2000;
            int page = 0;
            while (true) {
                var pageable = org.springframework.data.domain.PageRequest.of(page, pageSize);
                var pageData = dataRecordRepository.findByUpload(upload, pageable);
                if (pageData.isEmpty()) break;
                for (DataRecord dr : pageData.getContent()) {
                    Row row = sheet.createRow(rowIndex++);
                    for (int i = 0; i < headers.size(); i++) {
                        Cell cell = row.createCell(i);
                        String value = switch (i) {
                            case 0 -> dr.getCol01();
                            case 1 -> dr.getCol02();
                            case 2 -> dr.getCol03();
                            case 3 -> dr.getCol04();
                            case 4 -> dr.getCol05();
                            case 5 -> dr.getCol06();
                            case 6 -> dr.getCol07();
                            case 7 -> dr.getCol08();
                            case 8 -> dr.getCol09();
                            case 9 -> dr.getCol10();
                            case 10 -> dr.getCol11();
                            case 11 -> dr.getCol12();
                            case 12 -> dr.getCol13();
                            case 13 -> dr.getCol14();
                            case 14 -> dr.getCol15();
                            case 15 -> dr.getCol16();
                            case 16 -> dr.getCol17();
                            case 17 -> dr.getCol18();
                            case 18 -> dr.getCol19();
                            case 19 -> dr.getCol20();
                            case 20 -> dr.getCol21();
                            case 21 -> dr.getCol22();
                            case 22 -> dr.getCol23();
                            case 23 -> dr.getCol24();
                            case 24 -> dr.getCol25();
                            case 25 -> dr.getCol26();
                            case 26 -> dr.getCol27();
                            case 27 -> dr.getCol28();
                            case 28 -> dr.getCol29();
                            case 29 -> dr.getCol30();
                            case 30 -> dr.getCol31();
                            case 31 -> dr.getCol32();
                            case 32 -> dr.getCol33();
                            case 33 -> dr.getCol34();
                            case 34 -> dr.getCol35();
                            case 35 -> dr.getCol36();
                            case 36 -> dr.getCol37();
                            case 37 -> dr.getCol38();
                            case 38 -> dr.getCol39();
                            case 39 -> dr.getCol40();
                            case 40 -> dr.getCol41();
                            case 41 -> dr.getCol42();
                            case 42 -> dr.getCol43();
                            case 43 -> dr.getCol44();
                            case 44 -> dr.getCol45();
                            case 45 -> dr.getCol46();
                            case 46 -> dr.getCol47();
                            case 47 -> dr.getCol48();
                            case 48 -> dr.getCol49();
                            case 49 -> dr.getCol50();
                            case 50 -> dr.getCol51();
                            case 51 -> dr.getCol52();
                            case 52 -> dr.getCol53();
                            case 53 -> dr.getCol54();
                            case 54 -> dr.getCol55();
                            case 55 -> dr.getCol56();
                            case 56 -> dr.getCol57();
                            case 57 -> dr.getCol58();
                            case 58 -> dr.getCol59();
                            case 59 -> dr.getCol60();
                            case 60 -> dr.getCol61();
                            case 61 -> dr.getCol62();
                            case 62 -> dr.getCol63();
                            case 63 -> dr.getCol64();
                            default -> null;
                        };
                        if (value != null) cell.setCellValue(value);
                    }
                }
                if (pageData.getNumberOfElements() < pageSize) break;
                page++;
            }

            workbook.write(outputStream);
            outputStream.flush();
        }
    }
} 