package org.example.boxlybackend.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.config.RhnaApiProperties;
import org.example.boxlybackend.dto.*;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.repository.EmployeRepository;
import org.example.boxlybackend.services.LunchReservationService;
import org.example.boxlybackend.services.WeeklySubscriptionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;
    private final RestTemplate restTemplate;
    private final RhnaApiProperties rhnaApiProperties;  // ← inject it
    private final LunchReservationService lunchReservationService;
    private final WeeklySubscriptionService weeklySubscriptionService;

    public List<Employe> getAllEmployes() {
//        String apiUrlEmployees = "https://rhna.cmscompany.net/APIQualiso/employes";
//        String apiUrlPostes = "https://rhna.cmscompany.net/APIQualiso/postes";
    String apiUrlEmployees = rhnaApiProperties.employeesUrl();
        String apiUrlPostes = rhnaApiProperties.postesUrl();

        String responseEmployees = restTemplate.getForObject(apiUrlEmployees, String.class);
        ObjectMapper objectMapper = new ObjectMapper();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String responsePostes = restTemplate.getForObject(apiUrlPostes, String.class);
        Map<String, String> postIdToNameMap = new HashMap<>();

        List<Employe> collaborateurList = new ArrayList<>();
        try {
            JsonNode postsRoot = objectMapper.readTree(responsePostes);
            JsonNode postsPayload = postsRoot.path("payload");
            for (JsonNode postNode : postsPayload) {
                String postId = postNode.path("Fonction").path("id").asText();
                String postName = postNode.path("Fonction").path("intitule_du_poste").asText();
                postIdToNameMap.put(postId, postName);
            }
            JsonNode employeesRoot = objectMapper.readTree(responseEmployees);
            JsonNode payload = employeesRoot.path("payload");
            for (JsonNode node : payload) {
                JsonNode employeeNode = node.path("Employe");
                JsonNode managerNode = node.path("ResponsableDirect");
                JsonNode roleNode = node.path("CSP");
                if(employeeNode.path("matricule").asLong() == 0000) continue;
                String postId = node.path("Poste").path("poste_id").asText();
                String postName = postIdToNameMap.getOrDefault(postId, "Unknown Post");
                Employe collaborateur = Employe.builder()
                        .matricule(employeeNode.path("matricule").asLong())
                        .name(employeeNode.path("prenom").asText().concat(" ").concat(employeeNode.path("nom").asText()))
                        .cin(employeeNode.path("cin").asText())
                        .csp(roleNode.path("csp").asText())
                        .email(employeeNode.path("poste_email").asText())
                        .civilite(employeeNode.path("civilite").asText())
                        .build();
                ;
                collaborateurList.add(collaborateur);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return collaborateurList;
    }

    public List<String> syncEmployes() {
        List<Employe> rhnaList = getAllEmployes();
        List<Employe> dbList = employeRepository.findAll();
        List<String> differenceListNames = rhnaList.stream()
                .filter(e -> !dbList.contains(e))
                .map(e->e.getMatricule()+"-"+e.getName())
                .toList();
        if(!differenceListNames.isEmpty()){
            rhnaList.forEach(employe -> {dbList.stream()
                    .filter(c -> Objects.equals(employe.getMatricule(), c.getMatricule()))
                    .findAny()
                    .ifPresent(employeResponse -> {
                        employe.setId(employeResponse.getId());
                    });
            });
            employeRepository.saveAll(rhnaList);
        }
        return differenceListNames;

    }

    public List<String> getAllEmployeesEmails(){
       return employeRepository.findAllEmails();
    }
    public EmployeStats getEmployeeStats(String email){
        WeeklySubscriptionResponse userSubscription = weeklySubscriptionService.getUserSubscription(email);
        List<MonthlyStatsResponse> monthlyStats = lunchReservationService.getMonthlyStats(email);
        MonthlyStatsResponse currentMonthStats = monthlyStats.stream().filter(
                e -> e.getMonth() == LocalDate.now().getMonthValue()
                        && e.getYear() == LocalDate.now().getYear()).findAny().orElse(null);

        int totalCancletions = monthlyStats.stream().mapToInt(MonthlyStatsResponse::getCancelledDays).sum();
        int totalConsumption = monthlyStats.stream().mapToInt(MonthlyStatsResponse::getConsumedDays).sum();
        StatsTotals totals = StatsTotals.builder()
                .consumedDays(totalConsumption)
                .cancelledDays(totalCancletions)
                .build();
        return EmployeStats.builder()
                .startDate(userSubscription.getStartDate())
                .endDate(userSubscription.getEndDate())
                .cancellationDate(userSubscription.getCancelledAt() != null ? userSubscription.getCancelledAt().toLocalDate() : null)
                .currentMonth(currentMonthStats)
                .monthlyStats(monthlyStats)
                .totals(totals)
                .build();

    }

    public UserSubscriptionResponse getUserSubscriptionDetails(Authentication authentication){
        String email = authentication.getName();
        WeeklySubscriptionResponse userSubscription = weeklySubscriptionService.getUserSubscription(email);
        EmployeReservationStats monthlyStats = lunchReservationService.getCurrentMonthStat(email);
        Set<DayOfWeek> subscribedDays = userSubscription.getSubscribedDays();
        int scheduledDays = countSubscribedDaysInCurrentMonth(subscribedDays);
       return UserSubscriptionResponse.builder()
                .startDate(userSubscription.getStartDate())
                .active(userSubscription.isActive())
                .consumedDays(monthlyStats.getCurrentMonthConsumedDays())
                .subscribedDays(subscribedDays)
               .subscriptionPrice(userSubscription.getSubscriptionPrice())
                .scheduledDays(scheduledDays)
               .build();


    }
    public int countSubscribedDaysInCurrentMonth(Set<DayOfWeek> subscribedDays) {
        LocalDate now = LocalDate.now();
        YearMonth yearMonth = YearMonth.from(now);

        LocalDate start = yearMonth.atDay(1);
        LocalDate end = yearMonth.atEndOfMonth();

        int count = 0;

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (subscribedDays.contains(date.getDayOfWeek())) {
                count++;
            }
        }

        return count;
    }
}