package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.example.boxlybackend.entites.Enums.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCancellationResponse {
    private Long id;
    private String employeName;
    private Long matricule;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private RequestType type;
    private RequestStatus status;
    private LocalDateTime createdAt;
}
