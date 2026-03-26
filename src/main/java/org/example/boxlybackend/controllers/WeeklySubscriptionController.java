package org.example.boxlybackend.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.dto.WeeklySubscriptionUpdateRequest;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.example.boxlybackend.services.WeeklySubscriptionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@CrossOrigin
@Slf4j
public class WeeklySubscriptionController {

    private final WeeklySubscriptionService weeklySubscriptionService;
    @PostMapping
    public ResponseEntity<WeeklySubscriptionResponse> createSubscription(
            @Valid @RequestBody WeeklySubscriptionRequest request) {
        log.debug("POST /subscriptions - creating subscription for email={}", request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(weeklySubscriptionService.createSubscription(request));
    }

    @GetMapping
    public ResponseEntity<List<WeeklySubscription>> getAllSubscriptions(){
        return ResponseEntity.ok(weeklySubscriptionService.getAllSubscriptions());
    }
    @PutMapping("/{userId}")
    public ResponseEntity<WeeklySubscriptionResponse> updateSubscription(
            @PathVariable int userId,
            @RequestBody WeeklySubscriptionUpdateRequest request
    ) {
        WeeklySubscriptionResponse response =
                weeklySubscriptionService.updateSubscription(userId, request);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/import")
    public ResponseEntity<String> importEmployees(@RequestParam("file") MultipartFile file) throws Exception {
        weeklySubscriptionService.importEmployees(file);
        return ResponseEntity.ok("Import successful");
    }
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelSubscription(@RequestParam String email) {
        log.debug("POST /subscriptions/{}/cancel", email);
        weeklySubscriptionService.cancelSubscription(email);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/me/cancel")
    public ResponseEntity<Void> cancelMySubscription(Authentication authentication) {
        String email = authentication.getName();
        log.debug("POST /subscriptions/{}/cancel", email);
        weeklySubscriptionService.cancelSubscription(email);
        return ResponseEntity.noContent().build();
    }

}