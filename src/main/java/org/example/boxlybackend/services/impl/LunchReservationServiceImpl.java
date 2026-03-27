package org.example.boxlybackend.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.boxlybackend.dto.*;
import org.example.boxlybackend.dto.projection.MonthlyStatsProjection;
import org.example.boxlybackend.entites.*;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.example.boxlybackend.entites.Enums.ReservationAction;
import org.example.boxlybackend.entites.Enums.ReservationStatus;
import org.example.boxlybackend.repository.*;
import org.example.boxlybackend.services.LunchReservationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LunchReservationServiceImpl implements LunchReservationService {
    private final EmployeRepository employeRepository;
    private final WeeklySubscriptionRepository subscriptionRepository;
    private final LunchReservationRepository reservationRepository;
    private final MenuWeekDayRepository menuWeekDayRepository;
    private final CancellationResquestRepository cancelResquestRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final ReservationHistoryRepository reservationHistoryRepository;
    private static final int RESERVATION_BASELINE = 50;
    @Override
    public void generateReservationsForWeek(LocalDate weekStart) {
        List<WeeklySubscription> subscriptions =
                subscriptionRepository.findAllByActiveTrue();

        List<MenuWeekDay> weekDays =
                menuWeekDayRepository.findByDateBetweenOrderByDateAsc(
                        weekStart,
                        weekStart.plusDays(6)
                );

        for (WeeklySubscription subscription : subscriptions) {

            Employe employe = subscription.getEmploye();
            Set<DayOfWeek> subscribedDays = subscription.getSubscribedDays();

            List<CancellationRequest> cancellations =
                    cancelResquestRepository.findByEmployeAndStatus(
                            employe,
                            RequestStatus.APPROVED
                    );

            List<LunchReservation> toSave = new ArrayList<>();

            for (MenuWeekDay day : weekDays) {

                LocalDate date = day.getDate();

                // 1️⃣ Skip non-subscribed days
                if (!subscribedDays.contains(date.getDayOfWeek())) {
                    continue;
                }

                // 2️⃣ Avoid duplicates
                boolean exists = reservationRepository
                        .existsByEmployeAndMenuWeekDay(employe, day);

                if (exists) continue;

                // 3️⃣ Check cancellation
                boolean isCancelled = cancellations.stream()
                        .anyMatch(c ->
                                !date.isBefore(c.getStartDate()) &&
                                        !date.isAfter(c.getEndDate())
                        );

                ReservationStatus status = isCancelled
                        ? ReservationStatus.CANCELLED
                        : ReservationStatus.CONFIRMED;

                // 4️⃣ Create reservation
                LunchReservation reservation = new LunchReservation();
                reservation.setEmploye(employe);
                reservation.setMenuWeekDay(day);
                reservation.setStatus(status);

                toSave.add(reservation);
            }

            reservationRepository.saveAll(toSave);
        }
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
        Employe employee = getEmployeeByEmail(email);

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


    public void getReservationDiff() {

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


    @Override
    public void generateReservations(LocalDate startDate, LocalDate endDate) {

        List<WeeklySubscription> subscriptions =
                subscriptionRepository.findAllByActiveTrue();

        List<MenuWeekDay> days =
                menuWeekDayRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);

        List<Employe> employes = subscriptions.stream()
                .map(WeeklySubscription::getEmploye)
                .distinct()
                .toList();

        List<CancellationRequest> allCancellations =
                cancelResquestRepository.findByEmployeInAndStatus(
                        employes,
                        RequestStatus.APPROVED
                );

        Map<Employe, Set<LocalDate>> cancelledDatesByEmploye =
                mapCancelledDates(allCancellations);

        // 3️⃣ Fetch all existing reservations in one query
        List<LunchReservation> existingReservations =
                reservationRepository.findByEmployeInAndMenuWeekDayIn(employes, days);

        Map<Employe, Set<MenuWeekDay>> existingByEmploye =
                mapExistingReservations(existingReservations);

        // 4️⃣ Generate reservations
        List<LunchReservation> toSave = new ArrayList<>();

        for (WeeklySubscription subscription : subscriptions) {

            Employe employe = subscription.getEmploye();
            Set<DayOfWeek> subscribedDays = subscription.getSubscribedDays();

            Set<LocalDate> cancelledDates =
                    cancelledDatesByEmploye.getOrDefault(employe, Set.of());

            Set<MenuWeekDay> existingDays =
                    existingByEmploye.getOrDefault(employe, Set.of());

            for (MenuWeekDay day : days) {

                LocalDate date = day.getDate();

                // Skip non-subscribed days
                if (!subscribedDays.contains(date.getDayOfWeek())) continue;

                // Skip existing reservations
                if (existingDays.contains(day)) continue;

                ReservationStatus status = cancelledDates.contains(date)
                        ? ReservationStatus.CANCELLED
                        : ReservationStatus.CONFIRMED;

                toSave.add(buildReservation(employe, day, status));
            }
        }

        // 5️⃣ Batch save
        if (!toSave.isEmpty()) {
            reservationRepository.saveAll(toSave);
        }
    }

    @Override
    public void restoreReservation(Long reservationId, Authentication authentication) {
        LunchReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.CANCELLED) {
            throw new RuntimeException("Reservation is not cancelled");
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setCancelledAt(null);
        reservation.setCancelledBy(null);
        reservationRepository.save(reservation);

        ReservationHistory history = ReservationHistory.builder()
                .reservation(reservation)
                .action(ReservationAction.RESTORED)
                .performedBy(authentication.getName())
                .performedAt(LocalDateTime.now())
                .build();
        reservationHistoryRepository.save(history);
    }

    @Override
    public List<ReservationHistoryResponse> getReservationHistory(Long reservationId) {
        if (!reservationRepository.existsById(reservationId)) {
            throw new RuntimeException("Reservation not found");
        }
        return reservationHistoryRepository
                .findByReservationIdOrderByPerformedAtAsc(reservationId)
                .stream()
                .map(h -> new ReservationHistoryResponse(
                        h.getId(),
                        h.getAction(),
                        h.getPerformedBy(),
                        h.getPerformedAt(),
                        h.getNote()
                ))
                .toList();
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
    }

    public List<DailyDiffDTO> getReservationDiffs(
            LocalDate startDate,
            LocalDate endDate
    ) {

        List<Object[]> data =
                reservationRepository.countReservationsByDate(startDate, endDate);

        Map<LocalDate, Long> counts = new HashMap<>();

        for (Object[] row : data) {
            counts.put((LocalDate) row[0], (Long) row[1]);
        }

        List<DailyDiffDTO> result = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (isWeekend(current)) {
                current = current.plusDays(1);
                continue;
            }
            long count = counts.getOrDefault(current, 0L);
            long diff = count - RESERVATION_BASELINE;

            result.add(new DailyDiffDTO(current, count, diff));

            current = current.plusDays(1);
        }

        return result;
    }
    public List<DailyDiffDTO> getCurrentMonthDiffs() {
        LocalDate now = LocalDate.now();
        LocalDate start = now.withDayOfMonth(1);
        LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

        return getReservationDiffs(start, end);
    }

    public List<MonthlyStats> getMonthlyDiffs() {
        List<MonthlyStats> stats = reservationRepository.getMonthlyStats();

        stats.forEach(s -> {
            long baseline = s.getTotalDays() * RESERVATION_BASELINE;
            s.setBaseline(baseline);
            s.setDiff(s.getTotalReservations() - baseline);
        });

        return stats;
    }

    private LunchReservation buildReservation(Employe employe, MenuWeekDay day, ReservationStatus status) {
        LunchReservation r = new LunchReservation();
        r.setEmploye(employe);
        r.setMenuWeekDay(day);
        r.setStatus(status);
        return r;
    }
    private Map<Employe, Set<LocalDate>> mapCancelledDates(List<CancellationRequest> cancellations) {

        Map<Employe, Set<LocalDate>> map = new HashMap<>();

        for (CancellationRequest c : cancellations) {

            Set<LocalDate> dates = c.getStartDate()
                    .datesUntil(c.getEndDate().plusDays(1))
                    .collect(Collectors.toSet());

            map.computeIfAbsent(c.getEmploye(), e -> new HashSet<>())
                    .addAll(dates);
        }

        return map;
    }

    private Map<Employe, Set<MenuWeekDay>> mapExistingReservations(List<LunchReservation> reservations) {

        Map<Employe, Set<MenuWeekDay>> map = new HashMap<>();

        for (LunchReservation r : reservations) {
            map.computeIfAbsent(r.getEmploye(), e -> new HashSet<>())
                    .add(r.getMenuWeekDay());
        }

        return map;
    }
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

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}
