package org.example.boxlybackend.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuWeekDayOptionResponse {
    private Long id;
    private MenuOptionResponse menuOption;
    private boolean defaultOption;
}
