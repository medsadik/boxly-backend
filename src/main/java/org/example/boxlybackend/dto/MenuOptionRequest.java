package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuOptionRequest {

    private String title;

    private String starter;

    private String main;

    private String dessert;

    private boolean drink;

    private boolean water;

    private boolean bread;
}