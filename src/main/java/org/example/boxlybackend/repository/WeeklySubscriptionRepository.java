package org.example.boxlybackend.repository;

import org.example.boxlybackend.dto.MonthlyValue;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.WeeklySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface WeeklySubscriptionRepository extends JpaRepository<WeeklySubscription, Integer> {
    boolean existsByEmployeAndActiveTrue(Employe employe);

    Optional<WeeklySubscription> findByEmployeAndActiveTrue(Employe employe);
    Optional<WeeklySubscription> findByEmploye(Employe employe);
    Optional<WeeklySubscription> findByEmployeEmail(String email);
    List<WeeklySubscription> findAllByActiveTrue();
    long countByActiveTrue();
    @Query("""
SELECT new  org.example.boxlybackend.dto.MonthlyValue(
    YEAR(s.startDate),
    MONTH(s.startDate),
    COUNT(s)
)
FROM WeeklySubscription s
GROUP BY YEAR(s.startDate), MONTH(s.startDate)
ORDER BY YEAR(s.startDate), MONTH(s.startDate)
""")
    List<MonthlyValue> getMonthlySubscriptions();


}
