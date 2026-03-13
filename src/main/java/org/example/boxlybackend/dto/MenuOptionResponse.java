package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOptionResponse {

    private Long id;
    private String title;
    private String starter;
    private String main;
    private String dessert;
    private String drink;
    private String bread;
    private boolean active;
    private LocalDateTime createdAt;
}