package org.example.boxlybackend.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxlybackend.dto.EmployeReservationStats;
import org.example.boxlybackend.dto.LunchReservationRequest;
import org.example.boxlybackend.dto.LunchReservationResponse;
import org.example.boxlybackend.dto.MonthlyStatsResponse;
import org.example.boxlybackend.dto.projection.MonthlyStatsProjection;
import org.example.boxlybackend.entites.*;
import org.example.boxlybackend.entites.Enums.ReservationStatus;
import org.example.boxlybackend.repository.*;
import org.example.boxlybackend.services.LunchReservationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class LunchReservationServiceImpl implements LunchReservationService {
    private final EmployeRepository employeRepository;
    private final WeeklySubscriptionRepository subscriptionRepository;
    private final LunchReservationRepository reservationRepository;
    private final MenuWeekDayRepository menuWeekDayRepository;

    private final MenuOptionRepository menuOptionRepository;
    @Override
    public void generateReservationsForWeek(LocalDate weekStart) {

    }

    @Override
    public LunchReservationResponse reserveLunchFor(LunchReservationRequest lunchReservationRequest) {
        return null;
    }

    @Override
    public void cancelLunchReservationFor(Long reservationId) {

    }

    @Override
    public void cancelLunchReservationFor(String email, LocalDate date) {
    }

    @Override
    public LunchReservationResponse changeMenuOption(Long reservationId, Long menuOptionId) {
        return null;
    }

    @Override
    public List<LunchReservationResponse> getReservationsForEmployee(String email) {
        return List.of();
    }

    @Override
    public List<LunchReservationResponse> getReservationsBetween(String email, LocalDate start, LocalDate end) {
        return List.of();
    }

    @Override
    public List<LunchReservationResponse> getReservationsForDay(LocalDate date) {
        return List.of();
    }

    @Override
    public boolean reservationExists(String email, LocalDate date) {
        return false;
    }

    @Override
    public void generateReservationsForEmployee(String email) {

        Employe employe = getEmployeeByEmail(email);

        WeeklySubscription subscription = getWeeklySubscriptionByEmployee(employe);

        LocalDate startDate = subscription.getStartDate();
        Set<DayOfWeek> reservedDays = subscription.getSubscribedDays();

        LocalDate today = LocalDate.now();

        LocalDate current = startDate;

        while (!current.isAfter(today)) {
            log.info("Current date: " + current);
            if (reservedDays.contains(current.getDayOfWeek())) {
                boolean exists = reservationRepository
                        .existsByEmployeAndMenuWeekDayDate(employe, current);

                if (!exists) {
                    MenuWeekDay menuDay = menuWeekDayRepository
                            .findByDate(current)
                            .orElse(null);

                    if (menuDay != null) {
                        LunchReservation reservation = LunchReservation.builder()
                                .employe(employe)
                                .menuWeekDay(menuDay)
                                .menuOption(menuOptionRepository.findAll().get(0))
                                .status(ReservationStatus.CONFIRMED)
                                .build();
                        reservationRepository.save(reservation);
                    }
                }
            }

            current = current.plusDays(1);
        }
    }

    private Employe getEmployeeByEmail(String email) {
        return employeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private WeeklySubscription getWeeklySubscriptionByEmployee(Employe employe) {
        WeeklySubscription subscription =
                subscriptionRepository.findByEmploye(employe)
                        .orElseThrow(() -> new RuntimeException("Subscription not found"));
        return subscription;
    }
    private MonthlyStatsResponse toMonthlyStatsResponse(
            MonthlyStatsProjection projection,
            Set<DayOfWeek> subscribedDays,
            Employe employee) {

        long subscribedDaysCount = calculateSubscribedDays(
                projection.getYear(), projection.getMonth(), subscribedDays);
//
        BigDecimal rate = BigDecimal.valueOf(projection.getTotalConsumption())
                .divide(BigDecimal.valueOf(subscribedDaysCount), 2, RoundingMode.HALF_UP);

        long totalAmount = getTotalAmount(employee, subscribedDaysCount);

        return new MonthlyStatsResponse(
                projection.getYear(),
                projection.getMonth(),
                projection.getTotalConsumption(),
                projection.getCanceledDays(),
                subscribedDaysCount,
                rate,
                totalAmount
        );
    }

    @Override
    public List<MonthlyStatsResponse> getMonthlyStats(String email) {
        log.debug("Fetching monthly stats for email={}", email);

        Employe employee = getEmployeeByEmail(email);
        WeeklySubscription subscription = getWeeklySubscriptionByEmployee(employee);
        Set<DayOfWeek> subscribedDays = subscription.getSubscribedDays();

        List<MonthlyStatsProjection> projections = reservationRepository.getMonthlyStats(email);
        log.debug("Found {} monthly stat projections for email={}", projections.size(), email);

        return projections.stream()
                .map(p -> toMonthlyStatsResponse(p, subscribedDays, employee))
                .toList();
    }

    @Override
    public EmployeReservationStats getCurrentMonthStat(String email) {

        try {

            List<MonthlyStatsProjection> projections = reservationRepository.getMonthlyStats(email);

            Employe employee = getEmployeeByEmail(email);
            WeeklySubscription subscription = getWeeklySubscriptionByEmployee(employee);
            Set<DayOfWeek> subscribedDays = subscription.getSubscribedDays();

            LocalDate now = LocalDate.now();

            MonthlyStatsProjection currentMonthStats = projections.stream()
                    .filter(e -> e.getMonth() == now.getMonthValue()
                            && e.getYear() == now.getYear())
                    .findFirst()
                    .orElse(null);

            MonthlyStatsResponse monthlyStatsResponse =
                    toMonthlyStatsResponse(currentMonthStats, subscribedDays, employee);

            int currentMonthConsumedDays = monthlyStatsResponse.getConsumedDays();
            int currentMonthCancelledDays = monthlyStatsResponse.getCancelledDays();
            long currentMonthAmount = monthlyStatsResponse.getAmount();

            int totalConsumedDays = projections.stream()
                    .mapToInt(MonthlyStatsProjection::getTotalConsumption)
                    .sum();

            return new EmployeReservationStats(
                    currentMonthConsumedDays,
                    currentMonthCancelledDays,
                    currentMonthAmount,
                    totalConsumedDays
            );

        } catch (RuntimeException e) {
            // Employee exists but has no subscription
            return new EmployeReservationStats(0, 0, 0, 0);
        }
    }//    @Override
//    public List<MonthlyStatsResponse> getMonthlyStats(String email) {
//        Employe employee = getEmployeeByEmail(email);
//        WeeklySubscription subscription = getWeeklySubscriptionByEmployee(employee);
//        String email1 = subscription.getEmploye().getEmail();
//        Set<DayOfWeek> subscribedDays = subscription.getSubscribedDays();
//        List<MonthlyStatsProjection> projections = reservationRepository.getMonthlyStats(email1);
//        projections.forEach(p ->
//                System.out.println(
//                        p.getYear() + " " +
//                                p.getMonth() + " " +
//                                p.getTotalConsumption() + " " +
//                                p.getCanceledDays()
//                )
//        );
//        return projections.stream()
//                .map(p -> {
//                    long subscribedDaysCount = calculateSubscribedDays(p.getYear(), p.getMonth(), subscribedDays);
//                    long totalAmount = getTotalAmount(employee, subscribedDaysCount);
//                    double rate = Math.round(((double) p.getTotalConsumption() / subscribedDaysCount) * 100.0) / 100.0;
//                    return new MonthlyStatsResponse(
//                            p.getYear(),
//                            p.getMonth(),
//                            p.getTotalConsumption(),
//                            p.getCanceledDays(),
//                            subscribedDaysCount,
//                            rate,
//                            totalAmount
//                    );
//                }).toList();
//                   }

    private long getTotalAmount(Employe employee, long subscribedDaysCount) {
        List<String> discount = List.of("CADRE","EMPLOYE");
        long totalAmount = discount.contains(employee.getCsp()) ? subscribedDaysCount *20 : subscribedDaysCount *40;
        return totalAmount;
    }

    private long calculateSubscribedDays(int year, int month, Set<DayOfWeek> subscribedDays) {

        List<MenuWeekDay> days = menuWeekDayRepository.findByYearAndMonth(year, month);

        return days.stream()
                .filter(d -> subscribedDays.contains(d.getDate().getDayOfWeek()))
                .count();
    }
}
