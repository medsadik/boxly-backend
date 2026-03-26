package org.example.boxlybackend.services;

import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.dto.WeeklySubscriptionUpdateRequest;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


public interface WeeklySubscriptionService {

    WeeklySubscriptionResponse createSubscription(WeeklySubscriptionRequest request);

    List<WeeklySubscription> getAllSubscriptions();

    WeeklySubscriptionResponse updateSubscription(int userId, WeeklySubscriptionUpdateRequest request);
//
    void cancelSubscription(String userEmail);
//
    void importEmployees(MultipartFile file) throws Exception;
    WeeklySubscriptionResponse getUserSubscription(String userEmail);
//
//    boolean isUserSubscribedForDate(Long userId, LocalDate date);
//
//    void generateReservationsForWeek(LocalDate weekStart);

}
