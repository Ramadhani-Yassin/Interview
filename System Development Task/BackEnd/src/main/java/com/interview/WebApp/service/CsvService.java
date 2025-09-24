package com.interview.WebApp.service;

import com.interview.WebApp.entity.DataRecord;
import com.interview.WebApp.entity.Upload;
import com.interview.WebApp.repository.DataRecordRepository;
import com.interview.WebApp.repository.UploadRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CsvService {

    private static final int BATCH_SIZE = 1000;

    private final UploadRepository uploadRepository;
    private final DataRecordRepository dataRecordRepository;

    public CsvService(UploadRepository uploadRepository, DataRecordRepository dataRecordRepository) {
        this.uploadRepository = uploadRepository;
        this.dataRecordRepository = dataRecordRepository;
    }

    @Transactional
    public Upload ingestCsv(MultipartFile file, Charset charset) throws IOException {
        Objects.requireNonNull(file, "file");
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset))) {
            reader.mark(1024 * 1024);
            String headerLine = reader.readLine();
            if (headerLine == null || headerLine.isBlank()) {
                throw new IllegalArgumentException("CSV must include header row");
            }
            headerLine = stripBom(headerLine);
            char delimiter = detectDelimiter(headerLine);
            reader.reset();

            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setDelimiter(delimiter)
                    .setTrim(true)
                    .setIgnoreSurroundingSpaces(true)
                    .setSkipHeaderRecord(false)
                    .setHeader()
                    .build()
                    .withFirstRecordAsHeader();

            CSVParser parser = format.parse(reader);
            List<String> headers = parser.getHeaderNames();
            if (headers == null || headers.isEmpty()) {
                throw new IllegalArgumentException("CSV must include header row");
            }
            if (headers.size() > 64) {
                throw new IllegalArgumentException("Max supported columns is 64");
            }

            Upload upload = new Upload();
            upload.setFileName(file.getOriginalFilename());
            upload.setNumberOfColumns(headers.size());
            upload.setHeaderLine(String.join(",", headers));
            upload.setNumberOfRows(0);
            upload = uploadRepository.save(upload);

            List<DataRecord> batch = new ArrayList<>(BATCH_SIZE);
            int rowCount = 0;
            for (CSVRecord record : parser) {
                DataRecord dr = new DataRecord();
                dr.setUpload(upload);
                for (int i = 0; i < headers.size(); i++) {
                    String value = record.get(i);
                    switch (i) {
                        case 0 -> dr.setCol01(value);
                        case 1 -> dr.setCol02(value);
                        case 2 -> dr.setCol03(value);
                        case 3 -> dr.setCol04(value);
                        case 4 -> dr.setCol05(value);
                        case 5 -> dr.setCol06(value);
                        case 6 -> dr.setCol07(value);
                        case 7 -> dr.setCol08(value);
                        case 8 -> dr.setCol09(value);
                        case 9 -> dr.setCol10(value);
                        case 10 -> dr.setCol11(value);
                        case 11 -> dr.setCol12(value);
                        case 12 -> dr.setCol13(value);
                        case 13 -> dr.setCol14(value);
                        case 14 -> dr.setCol15(value);
                        case 15 -> dr.setCol16(value);
                        case 16 -> dr.setCol17(value);
                        case 17 -> dr.setCol18(value);
                        case 18 -> dr.setCol19(value);
                        case 19 -> dr.setCol20(value);
                        case 20 -> dr.setCol21(value);
                        case 21 -> dr.setCol22(value);
                        case 22 -> dr.setCol23(value);
                        case 23 -> dr.setCol24(value);
                        case 24 -> dr.setCol25(value);
                        case 25 -> dr.setCol26(value);
                        case 26 -> dr.setCol27(value);
                        case 27 -> dr.setCol28(value);
                        case 28 -> dr.setCol29(value);
                        case 29 -> dr.setCol30(value);
                        case 30 -> dr.setCol31(value);
                        case 31 -> dr.setCol32(value);
                        case 32 -> dr.setCol33(value);
                        case 33 -> dr.setCol34(value);
                        case 34 -> dr.setCol35(value);
                        case 35 -> dr.setCol36(value);
                        case 36 -> dr.setCol37(value);
                        case 37 -> dr.setCol38(value);
                        case 38 -> dr.setCol39(value);
                        case 39 -> dr.setCol40(value);
                        case 40 -> dr.setCol41(value);
                        case 41 -> dr.setCol42(value);
                        case 42 -> dr.setCol43(value);
                        case 43 -> dr.setCol44(value);
                        case 44 -> dr.setCol45(value);
                        case 45 -> dr.setCol46(value);
                        case 46 -> dr.setCol47(value);
                        case 47 -> dr.setCol48(value);
                        case 48 -> dr.setCol49(value);
                        case 49 -> dr.setCol50(value);
                        case 50 -> dr.setCol51(value);
                        case 51 -> dr.setCol52(value);
                        case 52 -> dr.setCol53(value);
                        case 53 -> dr.setCol54(value);
                        case 54 -> dr.setCol55(value);
                        case 55 -> dr.setCol56(value);
                        case 56 -> dr.setCol57(value);
                        case 57 -> dr.setCol58(value);
                        case 58 -> dr.setCol59(value);
                        case 59 -> dr.setCol60(value);
                        case 60 -> dr.setCol61(value);
                        case 61 -> dr.setCol62(value);
                        case 62 -> dr.setCol63(value);
                        case 63 -> dr.setCol64(value);
                    }
                }
                batch.add(dr);
                rowCount++;
                if (batch.size() >= BATCH_SIZE) {
                    dataRecordRepository.saveAll(batch);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                dataRecordRepository.saveAll(batch);
            }
            upload.setNumberOfRows(rowCount);
            uploadRepository.save(upload);
            return upload;
        }
    }

    private static String stripBom(String s) {
        if (s.length() > 0 && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    private static char detectDelimiter(String headerLine) {
        int commas = count(headerLine, ',');
        int semicolons = count(headerLine, ';');
        int tabs = count(headerLine, '\t');
        if (semicolons > commas && semicolons >= tabs) return ';';
        if (tabs > commas && tabs >= semicolons) return '\t';
        return ',';
    }

    private static int count(String s, char c) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) if (s.charAt(i) == c) n++;
        return n;
    }
} 