package org.example.boxlybackend.services;

import org.example.boxlybackend.dto.EmployeReservationStats;
import org.example.boxlybackend.dto.LunchReservationRequest;
import org.example.boxlybackend.dto.LunchReservationResponse;
import org.example.boxlybackend.dto.MonthlyStatsResponse;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.List;

public interface LunchReservationService {
    void generateReservationsForWeek(LocalDate weekStart);

    LunchReservationResponse reserveLunchFor(LunchReservationRequest lunchReservationRequest);

    void cancelLunchReservationFor(Long reservationId);
    void cancelLunchReservationFor(String email, LocalDate date);

    LunchReservationResponse changeMenuOption(Long reservationId, Long menuOptionId);
    List<LunchReservationResponse> getReservationsForEmployee(String email);
    List<LunchReservationResponse> getReservationsBetween(
            String email,
            LocalDate start,
            LocalDate end
    );

    List<LunchReservationResponse> getReservationsForDay(LocalDate date);

//    List<MenuOptionCountDTO> getMenuSummaryForDay(LocalDate date);
public EmployeReservationStats getCurrentMonthStat(String email);
    boolean reservationExists(String email, LocalDate date);
    public void generateReservationsForEmployee(String email);
    List<MonthlyStatsResponse> getMonthlyStats(String email);


}
