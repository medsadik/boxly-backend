package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.ReservationStatus;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(name = "lunch_reservations",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "menu_week_day_id"}))
public class LunchReservation extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "menu_week_day_id", nullable = false)
    private MenuWeekDay menuWeekDay;

    @ManyToOne
    @JoinColumn(name = "menu_option_id")
    private MenuOption menuOption; // can be null until selected

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // CONFIRMED / CANCELLED

    private LocalDateTime cancelledAt;

    private String cancelledBy; // USER / ADMIN

    // getters/setters
}