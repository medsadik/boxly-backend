package org.example.boxlybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStatsResponse {
    private int year;
    private int month;
    private int consumedDays;
    private int cancelledDays;
    private long scheduledDays;
    private BigDecimal consumptionRate;
    private long amount;

}
