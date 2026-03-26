package org.example.boxlybackend.controllers;

import org.example.boxlybackend.dto.EmployeConsumptionStat;
import org.example.boxlybackend.dto.GlobalDashboardStats;
import org.example.boxlybackend.dto.MonthlyDashboardStatsResponse;
import org.example.boxlybackend.services.impl.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin
public class DashboardController {

    private final DashboardService dashboardService;

    //@TODO AJOUTER LA DIFFERENCE ENTRE LES REPAS LIVRER ET 50 PAR JOUR
    //@TODO annulation d'une plage AVEC UNE VALIDATION
    //@TODO add weekly subscription days update


    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/global-stats")
    public ResponseEntity<GlobalDashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }
    @GetMapping("/monthly-stats")
    public ResponseEntity<MonthlyDashboardStatsResponse> getMonthlyStats() {
        return ResponseEntity.ok(dashboardService.getMonthlyDashboardStats());
    }
    @GetMapping("/employee/consumption-stats")
    public EmployeConsumptionStat getTotalAmountByMonth(
            Authentication authentication
    ) {
        return dashboardService.getComsumptionsStats(authentication);
    }
}