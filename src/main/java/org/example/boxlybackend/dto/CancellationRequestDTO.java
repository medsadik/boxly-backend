package org.example.boxlybackend.dto;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.CancellationRequest;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.Enums.RequestStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRequestDTO {

    private Long id;

    private String employe;

    private LocalDate startDate;
    private LocalDate endDate;

    private String reason;

    private LocalDateTime createdAt;
}
