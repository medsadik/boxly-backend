package org.example.boxlybackend.services;

import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;


public interface WeeklySubscriptionService {

    WeeklySubscriptionResponse createSubscription(WeeklySubscriptionRequest request);

    Page<WeeklySubscriptionResponse> getAllSubscriptions(Pageable pageable);

//    WeeklySubscriptionResponse updateSubscriptionDays(Long userId, Set<DayOfWeek> days);
//
    void cancelSubscription(String userEmail);
//
    WeeklySubscriptionResponse getUserSubscription(String userEmail);
//
//    boolean isUserSubscribedForDate(Long userId, LocalDate date);
//
//    void generateReservationsForWeek(LocalDate weekStart);

}
