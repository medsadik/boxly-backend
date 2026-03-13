package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.EmployeReservationStats;
import org.example.boxlybackend.dto.MonthlyStatsResponse;
import org.example.boxlybackend.services.LunchReservationService;
import org.springframework.web.bind.annotation.*;

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
}
