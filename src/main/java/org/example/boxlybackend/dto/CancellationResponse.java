package org.example.boxlybackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.example.boxlybackend.entites.Enums.RequestType;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CancellationResponse{
    private LocalDate startDate;
    private LocalDate endDate;
    private RequestStatus status;
    private RequestType type;
}
