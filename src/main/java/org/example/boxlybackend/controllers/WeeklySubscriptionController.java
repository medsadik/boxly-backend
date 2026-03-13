package org.example.boxlybackend.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.services.WeeklySubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
    public ResponseEntity<Page<WeeklySubscriptionResponse>> getAllSubscriptions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.debug("GET /subscriptions - page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(weeklySubscriptionService.getAllSubscriptions(pageable));
    }

    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelSubscription(@RequestParam String email) {
        log.debug("POST /subscriptions/{}/cancel", email);
        weeklySubscriptionService.cancelSubscription(email);
        return ResponseEntity.noContent().build();
    }

}