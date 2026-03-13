package org.example.boxlybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyDashboardStatsResponse{

    private List<MonthlyValue> activeSubscriptions;
    private List<MonthlyValue> cancellations;
    private List<MonthlyValue> reservations;
}

