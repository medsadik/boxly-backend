package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
//@Table(name = "menu_week_days",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"date"}))
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuWeekDay  extends Auditable {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false,unique = true)
    private LocalDate date;


    private boolean active = true;

//    @ManyToOne
//    @JoinColumn(name = "menu_week_id", nullable = false)
//    private MenuWeek menuWeek;

    @OneToMany(mappedBy = "menuWeekDay", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MenuWeekDayOption> options = new HashSet<>();

    @OneToMany(mappedBy = "menuWeekDay")
    @Builder.Default
    private Set<LunchReservation> reservations = new HashSet<>();


    public void addOption(MenuWeekDayOption option) {
            options.add(option);
            option.setMenuWeekDay(this);
        }
    public void removeOption(MenuWeekDayOption option) {
        options.remove(option);
        option.setMenuWeekDay(null);
    }
    // getters/setters
}
