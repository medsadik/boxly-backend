package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySubscriptionResponse {

    private EmployeDTO employe;
    private Set<DayOfWeek> subscribedDays;
    private boolean active;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime cancelledAt;
    private int subscriptionPrice;
}
