package org.example.boxlybackend.entites;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "weekly_subscriptions")
public class WeeklySubscription  extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)      // ✅ changed from @OneToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "subscription_days",
            joinColumns = @JoinColumn(name = "subscription_id"))
    @Column(name = "day_of_week")
    private Set<DayOfWeek> subscribedDays = new HashSet<>();

    private Integer subscriptionPrice;

    private boolean active = true;

    private LocalDate startDate;
    private LocalDate endDate;

    private LocalDateTime cancelledAt;
}
