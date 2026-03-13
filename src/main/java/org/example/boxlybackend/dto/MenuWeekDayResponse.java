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
public class MenuWeekDayResponse {

    private Long id;
    private LocalDate date;
    private boolean active;
    private List<MenuWeekDayOptionResponse> options;
}