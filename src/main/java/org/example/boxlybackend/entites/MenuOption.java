package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "menu_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MenuOption extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String title;

    @Column(nullable = false)
    private String starter;

    @Column(nullable = false)
    private String main;

    @Column(nullable = false)
    private String dessert;

    private boolean drink;
    private boolean water;

    private boolean bread;

    private boolean active = true;

//    @ManyToMany(mappedBy = "options")
//    private Set<MenuWeekDay> days = new HashSet<>();

    @OneToMany(mappedBy = "menuOption", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MenuWeekDayOption> menuWeekDayOptions = new HashSet<>();

    @OneToMany(mappedBy = "menuOption")
    private Set<LunchReservation> reservations = new HashSet<>();

    // getters/setters
}