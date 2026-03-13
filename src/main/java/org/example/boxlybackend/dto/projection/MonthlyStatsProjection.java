package org.example.boxlybackend.dto.projection;

public interface MonthlyStatsProjection {

    int getYear();
    int getMonth();
    int getTotalConsumption();
    int getCanceledDays();

}