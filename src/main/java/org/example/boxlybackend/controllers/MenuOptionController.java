package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.dto.MenuOptionRequest;
import org.example.boxlybackend.dto.MenuOptionResponse;
import org.example.boxlybackend.dto.MenuWeekDayResponse;
import org.example.boxlybackend.services.MenuOptionService;
import org.example.boxlybackend.services.MenuWeekDayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/menu-options")
@RequiredArgsConstructor
@CrossOrigin
public class MenuOptionController {

    private final MenuOptionService menuOptionService;
    private final MenuWeekDayService menuWeekDayService;

    @PostMapping
    public ResponseEntity<MenuOptionResponse> create(@RequestBody MenuOptionRequest request) {
        return ResponseEntity.ok(menuOptionService.createMenuOption(request));
    }
    @GetMapping("/{id}")
    public ResponseEntity<MenuOptionResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(menuOptionService.getMenuOptionById(id));
    }

    @GetMapping
    public ResponseEntity<List<MenuOptionResponse>> getAll() {
        return ResponseEntity.ok(menuOptionService.getAllMenuOptions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuOptionResponse> update(@PathVariable Long id,
                                                    @RequestBody MenuOptionRequest request) {
        return ResponseEntity.ok(menuOptionService.updateMenuOptions(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuOptionService.deleteMenuOptions(id);
        return ResponseEntity.noContent().build();
    }
}