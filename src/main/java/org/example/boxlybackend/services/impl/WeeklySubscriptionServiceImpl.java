package org.example.boxlybackend.services.impl;


import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.example.boxlybackend.mapper.WeeklySubscriptionMapper;
import org.example.boxlybackend.repository.EmployeRepository;
import org.example.boxlybackend.repository.WeeklySubscriptionRepository;
import org.example.boxlybackend.services.WeeklySubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class WeeklySubscriptionServiceImpl implements WeeklySubscriptionService{

    private final EmployeRepository employeRepository;
    private final WeeklySubscriptionMapper weeklySubscriptionMapper;
    private final WeeklySubscriptionRepository  weeklySubscriptionRepository;
    private static List<String> DISCOUNT =  List.of("CADRE","EMPLOYE");

    public WeeklySubscriptionResponse createSubscription(WeeklySubscriptionRequest request){
        List<WeeklySubscription> all = weeklySubscriptionRepository.findAll();
        weeklySubscriptionRepository.saveAll(all);
        Employe employe = employeRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Employee with email " + request.getEmail() + " not found"));
        if (request.getSubscribedDays() == null || request.getSubscribedDays().size() < 3) {
            throw new IllegalArgumentException("Subscription must contain at least 3 days");
        }

        if (weeklySubscriptionRepository.existsByEmployeAndActiveTrue(employe)) {
            throw new IllegalStateException("Employee already has an active subscription");
        }

        WeeklySubscription subscription = weeklySubscriptionMapper.toEntity(request);
        if (DISCOUNT.contains(employe.getCsp())) {
            subscription.setSubscriptionPrice(20);
        } else {
            subscription.setSubscriptionPrice(40);
        }
        subscription.setEmploye(employe);
        subscription.setStartDate(LocalDate.now());
        WeeklySubscription saved = weeklySubscriptionRepository.save(subscription);
        return weeklySubscriptionMapper.toResponse(saved);
    }

    public Page<WeeklySubscriptionResponse> getAllSubscriptions(Pageable pageable) {
        Page<WeeklySubscription> subscriptions = weeklySubscriptionRepository.findAll(pageable);
        return subscriptions.map(weeklySubscriptionMapper::toResponse);
    }

    @Override
    public void cancelSubscription(String userEmail) {
        Employe employe = employeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Employee with email " + userEmail + " not found"));
        WeeklySubscription weeklySubscription = weeklySubscriptionRepository.findByEmployeAndActiveTrue(employe)
                .orElseThrow(() -> new RuntimeException("Subscription for the employee" + userEmail + " not found"));
        weeklySubscription.setActive(false);
        weeklySubscription.setEndDate(LocalDate.now());
        weeklySubscription.setCancelledAt(LocalDateTime.now());
        weeklySubscriptionRepository.save(weeklySubscription);
    }

//    @Override
//    public WeeklySubscriptionResponse getUserSubscription(Authentication authentication) {
//        return null;
//    }

    @Override
    public WeeklySubscriptionResponse getUserSubscription(String userEmail) {
        Employe employe = employeRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Employee with email " + userEmail + " not found"));
        WeeklySubscription weeklySubscription = weeklySubscriptionRepository.findByEmployeAndActiveTrue(employe)
                .orElseThrow(() -> new RuntimeException("Subscription for the employee" + userEmail + " not found"));
        return weeklySubscriptionMapper.toResponse(weeklySubscription);
    }

}
