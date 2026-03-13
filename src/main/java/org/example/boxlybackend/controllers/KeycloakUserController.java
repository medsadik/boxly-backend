package org.example.boxlybackend.controllers;

import lombok.RequiredArgsConstructor;
import org.example.boxlybackend.services.impl.KeycloakService;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/keycloak/users")
@RequiredArgsConstructor
public class KeycloakUserController {

    private final KeycloakService keycloakUserService;

    @GetMapping
    public ResponseEntity<List<String>> getAllUsers() {
        return ResponseEntity.ok(keycloakUserService.getAllUserEmails());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserRepresentation> getUserById(@PathVariable String id) {
        UserRepresentation user = keycloakUserService.getUserById(id);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<UserRepresentation> getUserByUsername(@RequestParam String username) {
        UserRepresentation user = keycloakUserService.getUserByUsername(username);
        if (user == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(user);
    }
}