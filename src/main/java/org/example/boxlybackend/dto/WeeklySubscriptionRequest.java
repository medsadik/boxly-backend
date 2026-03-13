package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklySubscriptionRequest {
    private String email;
    Set<DayOfWeek> subscribedDays;
    private boolean active;
}
