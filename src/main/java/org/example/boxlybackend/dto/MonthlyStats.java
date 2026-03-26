package org.example.boxlybackend.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyStats {
    private int year;
    private int month;
    private long totalReservations;
    private long totalDays;

    // computed fields
    private long baseline;
    private long diff;

    public MonthlyStats(int year, int month, long totalReservations, long totalDays) {
        this.year = year;
        this.month = month;
        this.totalReservations = totalReservations;
        this.totalDays = totalDays;

        this.baseline = totalDays * 50;
        this.diff = totalReservations - baseline;
    }
}
