package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.services.PdfReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin
public class PdfReportController {

    private final PdfReportService pdfReportService;

    @GetMapping("/daily-reservations")
    public ResponseEntity<byte[]> getDailyReservationsPdf(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        LocalDate targetDate = date != null ? date : LocalDate.now();
        byte[] pdf = pdfReportService.generateDailyReservationReport(targetDate);

        String filename = "reservations_" + targetDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdf);
    }
}
