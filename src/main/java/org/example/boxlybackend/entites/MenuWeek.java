package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.boxlybackend.entites.Enums.WeekStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@Table(name = "menu_weeks",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"startDate"}))

public class MenuWeek  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private WeekStatus status; // EDITABLE / ACTIVE / ARCHIVED

    @Version
    private Long version;

//    @OneToMany(mappedBy = "menuWeek", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<MenuWeekDay> days = new HashSet<>();

    // getters/setters
}