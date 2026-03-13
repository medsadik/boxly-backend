package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeReservationStats {
    private int currentMonthConsumedDays;
    private int currentMonthCancelledDays;
    private long currentMonthAmount;
    private int totalConsumedDays;

}
