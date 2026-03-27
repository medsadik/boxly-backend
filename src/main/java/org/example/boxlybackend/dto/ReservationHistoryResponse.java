package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.ReservationAction;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHistoryResponse {
    private Long id;
    private ReservationAction action;
    private String performedBy;
    private LocalDateTime performedAt;
    private String note;
}
