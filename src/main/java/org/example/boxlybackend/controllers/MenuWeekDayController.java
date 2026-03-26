package org.example.boxlybackend.controllers;


import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.MenuWeekDayResponse;
import org.example.boxlybackend.services.MenuWeekDayService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin
public class MenuWeekDayController {
    private final MenuWeekDayService menuWeekDayService;


    @GetMapping("/by-date")
    public ResponseEntity<MenuWeekDayResponse> getMenuByDate(
            @RequestParam LocalDate date) {
        MenuWeekDayResponse response = menuWeekDayService.getMenuByDate(date);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/week")
    public ResponseEntity<List<MenuWeekDayResponse>> getWeekMenus(
            @RequestParam  LocalDate startDate) {
        List<MenuWeekDayResponse> responses = menuWeekDayService.getWeekMenu(startDate);
        return ResponseEntity.ok(responses);
    }
    @PostMapping("/assign")
    public ResponseEntity<MenuWeekDayResponse> assignMenuOptionToAday(@RequestParam Long menuOptionId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(menuWeekDayService.assignOption(menuOptionId,date));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeMenuOptionToAday(@RequestParam Long menuOptionId, @RequestParam LocalDate date) {
        menuWeekDayService.removeOption(menuOptionId,date);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-from-start")
    public ResponseEntity<String> generateFromStart(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
            ) {
        menuWeekDayService.generateMenuWeekDaysFromStartToToday(startDate,endDate);
        return ResponseEntity.ok("MenuWeekDays generated from " + startDate + " to today with default options.");
    }
}
