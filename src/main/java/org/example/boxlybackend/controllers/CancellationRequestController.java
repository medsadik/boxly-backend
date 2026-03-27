package org.example.boxlybackend.controllers;


import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.AdminCancellationResponse;
import org.example.boxlybackend.dto.CancellationRequestDTO;
import org.example.boxlybackend.dto.CancellationResponse;
import org.example.boxlybackend.dto.DailyCancellationRequestDTO;
import org.example.boxlybackend.services.impl.CancellationRequestServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cancellations")
@RequiredArgsConstructor
@CrossOrigin
public class CancellationRequestController {

    private final CancellationRequestServiceImpl cancellationService;

    // ✅ Employee creates a cancellation request
    @PostMapping("/period")
    public ResponseEntity<String> createRequest(
            @RequestBody CancellationRequestDTO request,
            Authentication authentication
    ) {

        cancellationService.createCancellationRequest(request, authentication);

        return ResponseEntity.ok("Cancellation request submitted successfully");
    }
    @PostMapping("/week-days")
    public ResponseEntity<String> createDailyCancellation(
            @RequestBody DailyCancellationRequestDTO request,
            Authentication authentication
    ) {
        cancellationService.createDailyCancellationRequest(request, authentication);
        return ResponseEntity.ok("Daily cancellation processed successfully");
    }
    // ✅ Admin approves a request
    @PostMapping("/{id}/approve")
    public ResponseEntity<String> approveRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {

        cancellationService.approveRequest(id, authentication);

        return ResponseEntity.ok("Request approved successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<List<CancellationResponse>> getCancellations(Authentication authentication) {
        List<CancellationResponse> cancelllations = cancellationService.getCancelllations(authentication);
        return ResponseEntity.ok().body(cancelllations);
    }

    // Admin endpoints
    @GetMapping("/pending")
    public ResponseEntity<List<AdminCancellationResponse>> getPendingRequests() {
        return ResponseEntity.ok(cancellationService.getPendingRequests());
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<String> rejectRequest(
            @PathVariable Long id,
            Authentication authentication
    ) {
        cancellationService.rejectRequest(id, authentication);
        return ResponseEntity.ok("Request rejected successfully");
    }
}