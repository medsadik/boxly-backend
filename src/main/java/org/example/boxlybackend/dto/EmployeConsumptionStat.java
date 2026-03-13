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
public class EmployeConsumptionStat {
    private List<MonthlyValue> monthlyValues;
    private List<MonthlyValue> totalAmountByMonth;

}
