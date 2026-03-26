package org.example.boxlybackend.entites;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.example.boxlybackend.entites.Enums.RequestType;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employe employe;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(length = 500)
    private String reason;
    @Enumerated(EnumType.STRING)
    private RequestType type;
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean autoApproved;
    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;

    private String validatedBy;
}


