package org.example.boxlybackend.services.impl;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.*;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.example.boxlybackend.repository.LunchReservationRepository;
import org.example.boxlybackend.repository.WeeklySubscriptionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LunchReservationRepository reservationRepository;
    private final WeeklySubscriptionRepository weeklySubscriptionRepository;

    public GlobalDashboardStats getDashboardStats() {
        long totalEmployees = weeklySubscriptionRepository.countByActiveTrue();

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        long totalReservations = reservationRepository.countConfirmedReservations(monthStart, monthEnd);
        long totalCancellations = reservationRepository.countCancelledReservations(monthStart, monthEnd);
        long totalReclamations = 0;
        return new GlobalDashboardStats(totalEmployees, totalReservations, totalCancellations, totalReclamations);
    }

    public MonthlyDashboardStatsResponse getMonthlyDashboardStats() {
        return new MonthlyDashboardStatsResponse(
                weeklySubscriptionRepository.getMonthlySubscriptions(),
                reservationRepository.getMonthlyReservations(),
                reservationRepository.getMonthlyCancellations()
        );
    }

    public DashboardResponse getDashboardResponse() {
        GlobalDashboardStats dashboardStats = getDashboardStats();
        MonthlyDashboardStatsResponse monthlyDashboardStats = getMonthlyDashboardStats();

        return DashboardResponse.builder()
                .globalStats(dashboardStats)
                .monthlyStats(monthlyDashboardStats)
                .build();
    }

    public List<MonthlyValue> countConsumedDaysByMonth(String email) {
        List<MonthlyValue> monthlyStats = reservationRepository.getMonthlyConsumedDays(email);
        return monthlyStats;
    }

//    public List<MonthlyValue> getTotalAmountByMonth(String email) {
//        WeeklySubscription weeklySubscription =
//                weeklySubscriptionRepository.findByEmployeEmail(email).orElse(null);
//        List<MonthlyValue> monthlyStats = reservationRepository.getMonthlyConsumedDays(email);
//
//        monthlyStats.forEach(r ->
//                r.setValue(r.getValue() * weeklySubscription.getSubscriptionPrice())
//        );
//
//        return monthlyStats;
//    }
    public EmployeConsumptionStat getComsumptionsStats(Authentication authentication) {
        String email = authentication.getName();
        List<MonthlyValue> monthlyValues = countConsumedDaysByMonth(email);
        List<MonthlyValue> totalAmountByMonth = getTotalAmountByMonth(email);
        return EmployeConsumptionStat.builder()
                .monthlyValues(monthlyValues)
                .totalAmountByMonth(totalAmountByMonth)
                .build();
    }
public List<MonthlyValue> getTotalAmountByMonth(String email) {

    WeeklySubscription weeklySubscription =
            weeklySubscriptionRepository
                    .findByEmployeEmail(email)
                    .orElseThrow(() -> new RuntimeException("Subscription not found"));

    Integer price = weeklySubscription.getSubscriptionPrice();

    if (price == null) {
        price = 0;
    }

    List<MonthlyValue> monthlyStats = reservationRepository.getMonthlyConsumedDays(email);

    int finalPrice = price;

    monthlyStats.forEach(r ->
            r.setValue(r.getValue() * finalPrice)
    );

    return monthlyStats;
}
}