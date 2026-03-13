package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeStats {
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate cancellationDate;
    private List<MonthlyStatsResponse> monthlyStats;
    private MonthlyStatsResponse currentMonth;
    private StatsTotals totals;

}
