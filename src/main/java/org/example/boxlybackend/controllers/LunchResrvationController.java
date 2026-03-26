package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.DailyDiffDTO;
import org.example.boxlybackend.dto.EmployeReservationStats;
import org.example.boxlybackend.dto.MonthlyStats;
import org.example.boxlybackend.dto.MonthlyStatsResponse;
import org.example.boxlybackend.services.LunchReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
@CrossOrigin
public class LunchResrvationController {

    private final LunchReservationService lunchReservationService;

    @PostMapping("/generate")
    public void generateReservations(@RequestParam String email) {
        lunchReservationService.generateReservationsForEmployee(email);
    }

    @GetMapping("/stats")
    public List<MonthlyStatsResponse> getReservationStat(@RequestParam String email) {
        return lunchReservationService.getMonthlyStats(email);
    }

    @GetMapping("/current-month/employee")
    public EmployeReservationStats getCurrentMonthStateByUser(@RequestParam String email) {
        return lunchReservationService.getCurrentMonthStat(email);
    }
    @PostMapping("/generate-period")
    public ResponseEntity<String> generateReservations(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest()
                    .body("endDate must be after startDate");
        }

        lunchReservationService.generateReservations(startDate, endDate);

        return ResponseEntity.ok("Reservations generated successfully");
    }

    @GetMapping("/diff")
    public ResponseEntity<List<DailyDiffDTO>> getReservationDiffs(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate
    ) {

        if (endDate.isBefore(startDate)) {
            return ResponseEntity.badRequest().build();
        }


        List<DailyDiffDTO> reservationDiffs = lunchReservationService.getReservationDiffs(startDate, endDate);

        return ResponseEntity.ok(reservationDiffs);
    }

    @GetMapping("/diff/current-month")
    public ResponseEntity<List<DailyDiffDTO>> getCurrentMonthDiffs(
    ) {
        return ResponseEntity.ok(
                lunchReservationService.getCurrentMonthDiffs()
        );
    }

    @GetMapping("/diff/grouped-by-month")
    public List<MonthlyStats> getMonthlyStats() {
        return lunchReservationService.getMonthlyDiffs();
    }
}
