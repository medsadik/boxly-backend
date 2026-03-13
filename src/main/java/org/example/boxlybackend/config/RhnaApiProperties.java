package org.example.boxlybackend.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "rhna.api")
@Validated
public record RhnaApiProperties(
        @NotBlank String baseUrl,
        @NotBlank String employeesPath,
        @NotBlank String postesPath
) {
    public String employeesUrl() {
        return baseUrl + employeesPath;
    }

    public String postesUrl() {
        return baseUrl + postesPath;
    }
}