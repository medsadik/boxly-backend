package org.example.boxlybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GlobalDashboardStats {

    private long totalEmployees;
    private long totalReservationsCurrentMonth;
    private long totalCancellationsCurrentMonth;
    private long totalReclamationsCurrentMonth;
    private long gap;
}
