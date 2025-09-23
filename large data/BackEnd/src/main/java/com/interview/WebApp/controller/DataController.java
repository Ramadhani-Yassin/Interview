package com.interview.WebApp.controller;

import com.interview.WebApp.entity.Upload;
import com.interview.WebApp.repository.UploadRepository;
import com.interview.WebApp.repository.DataRecordRepository;
import com.interview.WebApp.service.CsvService;
import com.interview.WebApp.service.ExportService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@CrossOrigin(origins = "*")
public class DataController {

    private final CsvService csvService;
    private final ExportService exportService;
    private final UploadRepository uploadRepository;
    private final DataRecordRepository dataRecordRepository;

    public DataController(CsvService csvService, ExportService exportService, UploadRepository uploadRepository, DataRecordRepository dataRecordRepository) {
        this.csvService = csvService;
        this.exportService = exportService;
        this.uploadRepository = uploadRepository;
        this.dataRecordRepository = dataRecordRepository;
    }

    @PostMapping(path = "/uploads", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Upload uploadCsv(@RequestPart("file") MultipartFile file,
                            @RequestParam(value = "charset", required = false) String charsetName) throws IOException {
        Charset charset = StandardCharsets.UTF_8;
        if (charsetName != null && !charsetName.isBlank()) {
            try {
                charset = Charset.forName(charsetName);
            } catch (IllegalArgumentException ex) {
                // Fallback to UTF-8 on invalid/unsupported charset
                charset = StandardCharsets.UTF_8;
            }
        }
        return csvService.ingestCsv(file, charset);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex) { return ex.getMessage(); }

    // Map IO/parsing errors to 400 instead of 500 for better UX when CSV/encoding is bad
    @ExceptionHandler({IOException.class})
    @ResponseStatus(org.springframework.http.HttpStatus.BAD_REQUEST)
    public String handleIo(Exception ex) { return ex.getMessage(); }

    @GetMapping("/uploads")
    public List<Upload> listUploads() {
        return uploadRepository.findAll(Sort.by(Sort.Direction.DESC, "uploadedAt"));
    }

    @GetMapping("/uploads/{uploadId}/records")
    public Page<?> getRecords(
            @PathVariable Long uploadId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "ASC") Sort.Direction direction
    ) {
        Upload upload = uploadRepository.findById(uploadId).orElseThrow();
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, mapSortField(sortBy)));
        if (search != null && !search.isBlank()) {
            return dataRecordRepository.search(upload, search, pageable);
        }
        return dataRecordRepository.findByUpload(upload, pageable);
    }

    @GetMapping("/uploads/{uploadId}/export")
    public ResponseEntity<byte[]> export(@PathVariable Long uploadId) throws IOException {
        Upload upload = uploadRepository.findById(uploadId).orElseThrow();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exportService.exportToExcel(upload, baos);
        byte[] bytes = baos.toByteArray();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=export-" + uploadId + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .contentLength(bytes.length)
                .body(bytes);
    }

    private String mapSortField(String sortBy) {
        // Accept id or col01..col64 to prevent arbitrary injection
        if ("id".equalsIgnoreCase(sortBy)) return "id";
        String normalized = sortBy.toLowerCase();
        if (normalized.matches("col(0[1-9]|[1-5][0-9]|6[0-4])")) {
            return normalized;
        }
        return "id";
    }
} 