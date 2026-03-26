package org.example.boxlybackend.services.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.boxlybackend.dto.WeeklySubscriptionRequest;
import org.example.boxlybackend.dto.WeeklySubscriptionResponse;
import org.example.boxlybackend.dto.WeeklySubscriptionUpdateRequest;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.example.boxlybackend.mapper.WeeklySubscriptionMapper;
import org.example.boxlybackend.repository.EmployeRepository;
import org.example.boxlybackend.repository.WeeklySubscriptionRepository;
import org.example.boxlybackend.services.WeeklySubscriptionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class WeeklySubscriptionServiceImpl implements WeeklySubscriptionService{

    private final EmployeRepository employeRepository;
    private final WeeklySubscriptionMapper weeklySubscriptionMapper;
    private final WeeklySubscriptionRepository  weeklySubscriptionRepository;
    private static List<String> DISCOUNT =  List.of("CADRE","EMPLOYE");
    private static final List<DayOfWeek> WORKING_DAYS = List.of(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
    );
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

//    public Page<WeeklySubscriptionResponse> getAllSubscriptions(Pageable pageable) {
//        Page<WeeklySubscription> subscriptions = weeklySubscriptionRepository.findAll(pageable);
//        return subscriptions.map(weeklySubscriptionMapper::toResponse);
//    }
    public List<WeeklySubscription> getAllSubscriptions() {
        return weeklySubscriptionRepository.findAll();
    }

    @Override
    public WeeklySubscriptionResponse updateSubscription(int subscriptionId, WeeklySubscriptionUpdateRequest request) {
        WeeklySubscription weeklySubscription = weeklySubscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription for the employee" + subscriptionId + " not found"));
        weeklySubscription.setActive(request.isActive());
        weeklySubscription.setSubscribedDays(request.getSubscribedDays());
        WeeklySubscription saved = weeklySubscriptionRepository.save(weeklySubscription);
        return weeklySubscriptionMapper.toResponse(saved);
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
        WeeklySubscription weeklySubscription = weeklySubscriptionRepository.findByEmploye(employe)
                .orElseThrow(() -> new RuntimeException("Subscription for the employee" + userEmail + " not found"));
        return weeklySubscriptionMapper.toResponse(weeklySubscription);
    }

    @Override
    public void importEmployees(MultipartFile file) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);

            long matricule = (long) row.getCell(0).getNumericCellValue();
            int frequency = (int) row.getCell(3).getNumericCellValue();

            // 1️⃣ Create Employee
            Employe employe = employeRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new RuntimeException("Employee Not Found"));

            boolean present = weeklySubscriptionRepository.findByEmploye(employe).isPresent();
            if (present) {
                continue;
            }
            // 2️⃣ Create Subscription
            WeeklySubscription subscription = new WeeklySubscription();
            subscription.setEmploye(employe);
            subscription.setActive(true);
            subscription.setStartDate(LocalDate.now());
            if (DISCOUNT.contains(employe.getCsp())) {
                subscription.setSubscriptionPrice(20);
            } else {
                subscription.setSubscriptionPrice(40);
            }
            subscription.setSubscribedDays(generateRandomDays(frequency));

            weeklySubscriptionRepository.save(subscription);
        }

        workbook.close();
    }


    private Set<DayOfWeek> generateRandomDays(int frequency) {

        List<DayOfWeek> shuffled = new ArrayList<>(WORKING_DAYS);
        Collections.shuffle(shuffled);

        return shuffled.stream()
                .limit(frequency)
                .collect(Collectors.toSet());
    }
}
