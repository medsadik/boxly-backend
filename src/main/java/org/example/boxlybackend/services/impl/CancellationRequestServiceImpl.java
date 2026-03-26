package org.example.boxlybackend.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.CancellationRequestDTO;
import org.example.boxlybackend.dto.CancellationResponse;
import org.example.boxlybackend.dto.DailyCancellationRequestDTO;
import org.example.boxlybackend.entites.CancellationRequest;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.example.boxlybackend.entites.Enums.RequestType;
import org.example.boxlybackend.entites.Enums.ReservationStatus;
import org.example.boxlybackend.entites.LunchReservation;
import org.example.boxlybackend.mapper.CancellationMapper;
import org.example.boxlybackend.repository.CancellationResquestRepository;
import org.example.boxlybackend.repository.EmployeRepository;
import org.example.boxlybackend.repository.LunchReservationRepository;
import org.example.boxlybackend.repository.MenuWeekDayRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class CancellationRequestServiceImpl {

    private final CancellationResquestRepository cancellationResquestRepository;
    private final EmployeRepository employeRepository;
    private final LunchReservationRepository lunchReservationRepository;
    private final CancellationMapper cancellationMapper;
    public List<CancellationResponse> getCancelllations(Authentication authentication)
    {
        String email = authentication.getName();
        List<CancellationRequest> cancellations = cancellationResquestRepository.findByEmployeEmail(email);
        return cancellationMapper.toResponseList(cancellations);
    }
    public void createCancellationRequest(CancellationRequestDTO request, Authentication authentication) {

        String email = authentication.getName();

        Employe employe = employeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new RuntimeException("Invalid date range");
        }

        long duration = ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()
        ) + 1;

        CancellationRequest requestEntity = new CancellationRequest();
        requestEntity.setEmploye(employe);
        requestEntity.setStartDate(request.getStartDate());
        requestEntity.setEndDate(request.getEndDate());
        requestEntity.setReason(request.getReason());
        requestEntity.setCreatedAt(LocalDateTime.now());
        requestEntity.setType(RequestType.PERIOD);
        if (duration <= 3) {
            requestEntity.setStatus(RequestStatus.APPROVED);
            requestEntity.setAutoApproved(true);
            requestEntity.setValidatedAt(LocalDateTime.now());
            requestEntity.setValidatedBy(email);
            cancelReservations(employe, request.getStartDate(), request.getEndDate(),email);
        } else {
            requestEntity.setStatus(RequestStatus.PENDING);
        }

        cancellationResquestRepository.save(requestEntity);
    }

    @Transactional
    public void createDailyCancellationRequest(
            DailyCancellationRequestDTO request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Employe employe = employeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        List<LocalDate> dates = request.getDates();

        LocalDateTime now = LocalDateTime.now();

        List<CancellationRequest> requestsToSave = new ArrayList<>();

        for (LocalDate date : dates) {

            if (date.isBefore(LocalDate.now())) {
                throw new RuntimeException("Cannot cancel past date: " + date);
            }

            boolean exists = cancellationResquestRepository
                    .existsByEmployeAndStartDateAndEndDate(employe, date, date);

            if (exists) continue;

            CancellationRequest entity = new CancellationRequest();
            entity.setEmploye(employe);
            entity.setStartDate(date);
            entity.setEndDate(date);
            entity.setReason(request.getReason());
            entity.setStatus(RequestStatus.APPROVED);
            entity.setAutoApproved(true);
            entity.setCreatedAt(now);
            entity.setValidatedAt(now);
            entity.setValidatedBy(email);
            entity.setType(RequestType.DAY);
            requestsToSave.add(entity);
        }

        cancellationResquestRepository.saveAll(requestsToSave);

        cancelReservationsBulk(employe, dates);
    }

    public void approveRequest(Long requestId, Authentication authentication) {
        String adminEmail = authentication.getName();

        CancellationRequest request = cancellationResquestRepository.findById(requestId)
                .orElseThrow();

        if (request.getStatus() == RequestStatus.APPROVED) {
            throw new RuntimeException("Already approved");
        }

        request.setStatus(RequestStatus.APPROVED);
        request.setValidatedAt(LocalDateTime.now());
        request.setValidatedBy(adminEmail);

        cancelReservations(
                request.getEmploye(),
                request.getStartDate(),
                request.getEndDate(),
                adminEmail
        );
    }


    private void cancelReservationsBulk(Employe employe, List<LocalDate> dates) {

        List<LunchReservation> reservations =
                lunchReservationRepository.findByEmployeAndMenuWeekDay_DateIn(
                        employe,
                        dates
                );

        reservations.forEach(r -> r.setStatus(ReservationStatus.CANCELLED));

        lunchReservationRepository.saveAll(reservations);
    }
    private void cancelReservations(Employe employe, LocalDate start, LocalDate end,String cancelledBy) {

        List<LunchReservation> reservations =
                lunchReservationRepository.findByEmployeAndDateBetween(
                        employe, start, end
                );

        reservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .forEach(r -> {
                    r.setStatus(ReservationStatus.CANCELLED);
                    r.setCancelledAt(LocalDateTime.now());
                    r.setCancelledBy(cancelledBy);
                });

        lunchReservationRepository.saveAll(reservations);
    }
}