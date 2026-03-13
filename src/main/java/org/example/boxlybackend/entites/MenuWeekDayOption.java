package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.*;

// Instead of @ManyToMany, model the join table explicitly
@Entity
@Table(name = "menu_week_day_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class MenuWeekDayOption  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "menu_week_day_id", nullable = false)
    private MenuWeekDay menuWeekDay;

    @ManyToOne
    @JoinColumn(name = "menu_option_id", nullable = false)
    private MenuOption menuOption;

    @Builder.Default
    private boolean defaultOption = false; // 👈 lives here, scoped to this day
}