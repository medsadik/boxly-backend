package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.EmployeConsumptionStat;
import org.example.boxlybackend.dto.EmployeStats;
import org.example.boxlybackend.dto.MonthlyValue;
import org.example.boxlybackend.dto.UserSubscriptionResponse;
import org.example.boxlybackend.services.impl.DashboardService;
import org.example.boxlybackend.services.impl.EmployeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employee")
@CrossOrigin
public class EmployeController {

    private final EmployeService employeService;
    private final DashboardService dashboardService;
    @PostMapping("/sync")
    public List<String> sync() {
        return employeService.syncEmployes();
    }

    @GetMapping("/emails")
    public List<String> findAllEmails(){
        return employeService.getAllEmployeesEmails();
    }
    @GetMapping("/stats")
    public ResponseEntity<EmployeStats> getEmployeStats(String email){
        return ResponseEntity.ok(employeService.getEmployeeStats(email));
    }
    @GetMapping("/subscription-details/me")
    public ResponseEntity<UserSubscriptionResponse> getUserSubscriptionDetails(Authentication authentication) {
        UserSubscriptionResponse response =
                employeService.getUserSubscriptionDetails(authentication);

        return ResponseEntity.ok(response);
    }

//    @GetMapping("/consumption-stats")
//    public EmployeConsumptionStat getTotalAmountByMonth(
//            @RequestParam String email
//    ) {
//        return dashboardService.getComsumptionsStats(email);
//    }
}