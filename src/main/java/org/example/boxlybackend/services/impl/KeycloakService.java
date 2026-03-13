package org.example.boxlybackend.services.impl;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KeycloakService {

    private final Keycloak keycloakAdmin;
    private final String realm;

    public KeycloakService(Keycloak keycloakAdmin, @Value("${keycloak.realm}") String realm) {
        this.keycloakAdmin = keycloakAdmin;
        this.realm = realm;
    }

    public List<String> getAllUserEmails() {
        List<UserRepresentation> users = keycloakAdmin.realm(realm)
                .users()
                .list();

        return users.stream()
                .map(UserRepresentation::getEmail)
                .filter(email -> email != null)
                .collect(Collectors.toList());
    }

    /**
     * Get user by username
     */
    public UserRepresentation getUserByUsername(String username) {
        List<UserRepresentation> users = keycloakAdmin.realm(realm)
                .users()
                .search(username, true);
        return users.isEmpty() ? null : users.get(0);
    }

    /**
     * Get user by ID
     */
    public UserRepresentation getUserById(String userId) {
        return keycloakAdmin.realm(realm)
                .users()
                .get(userId)
                .toRepresentation();
    }
}