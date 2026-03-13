package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSubscriptionResponse{
    private Set<DayOfWeek> subscribedDays;
    private boolean active;
    private LocalDate startDate;
    private int consumedDays;
    private int subscriptionPrice;
    private int scheduledDays;
}
