package org.example.boxlybackend.repository;

import org.example.boxlybackend.dto.MonthlyDashboardStatsResponse;
import org.example.boxlybackend.dto.MonthlyStats;
import org.example.boxlybackend.dto.MonthlyValue;
import org.example.boxlybackend.dto.projection.MonthlyConsumedDaysProjection;
import org.example.boxlybackend.dto.projection.MonthlyStatsProjection;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.LunchReservation;
import org.example.boxlybackend.entites.MenuWeekDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LunchReservationRepository extends JpaRepository<LunchReservation,Long> {

    @Query("""
        SELECT 
            YEAR(r.menuWeekDay.date) as year,
            MONTH(r.menuWeekDay.date) as month,
            SUM(CASE WHEN r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CONFIRMED THEN 1 ELSE 0 END) as totalConsumption,
            SUM(CASE WHEN r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CANCELLED THEN 1 ELSE 0 END) as canceledDays
        FROM LunchReservation r
        WHERE r.employe.email = :email
        GROUP BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
        ORDER BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
        """)
    List<MonthlyStatsProjection> getMonthlyStats(String email);
    @Query("""
SELECT new org.example.boxlybackend.dto.MonthlyValue(
    YEAR(r.menuWeekDay.date),
    MONTH(r.menuWeekDay.date),
    COUNT(r)
)
FROM LunchReservation r
WHERE r.employe.email = :email
AND r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CONFIRMED
GROUP BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
ORDER BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
""")
    List<MonthlyValue> getMonthlyConsumedDays(String email);

    @Query("""
SELECT new org.example.boxlybackend.dto.MonthlyStats(
    YEAR(m.date),
    MONTH(m.date),
    COUNT(r),
    COUNT(DISTINCT m.date)
)
FROM MenuWeekDay m
LEFT JOIN LunchReservation r 
    ON r.menuWeekDay = m 
    AND r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CONFIRMED
GROUP BY YEAR(m.date), MONTH(m.date)
ORDER BY YEAR(m.date), MONTH(m.date)
""")
    List<MonthlyStats> getMonthlyStats();

    boolean existsByEmployeAndMenuWeekDayDate(Employe employe, LocalDate date);
    @Query("SELECT COUNT(r) FROM LunchReservation r " +
            "WHERE r.menuWeekDay.date BETWEEN :start AND :end " +
            "AND r.status = 'CONFIRMED'")
    long countConfirmedReservations(@Param("start") LocalDate start,
                                    @Param("end") LocalDate end);

    @Query("SELECT COUNT(r) FROM LunchReservation r " +
            "WHERE r.menuWeekDay.date BETWEEN :start AND :end " +
            "AND r.status = 'CANCELLED'")
    long countCancelledReservations(@Param("start") LocalDate start,
                                    @Param("end") LocalDate end);



    @Query("""
    SELECT r.menuWeekDay.date, COUNT(r)
    FROM LunchReservation r
    WHERE r.menuWeekDay.date BETWEEN :start AND :end
      AND r.status = 'CONFIRMED'
    GROUP BY r.menuWeekDay.date
""")
    List<Object[]> countReservationsByDate(LocalDate start, LocalDate end);

    @Query("""
SELECT new  org.example.boxlybackend.dto.MonthlyValue(
    YEAR(r.menuWeekDay.date),
    MONTH(r.menuWeekDay.date),
    COUNT(r)
)
FROM LunchReservation r
WHERE r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CONFIRMED
GROUP BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
ORDER BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
""")
    List<MonthlyValue> getMonthlyReservations();
    @Query("""
SELECT new org.example.boxlybackend.dto.MonthlyValue(
    YEAR(r.menuWeekDay.date),
    MONTH(r.menuWeekDay.date),
    COUNT(r)
)
FROM LunchReservation r
WHERE r.status = org.example.boxlybackend.entites.Enums.ReservationStatus.CANCELLED
GROUP BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
ORDER BY YEAR(r.menuWeekDay.date), MONTH(r.menuWeekDay.date)
""")
    List<MonthlyValue> getMonthlyCancellations();

    @Query("""
    SELECT r
    FROM LunchReservation r
    WHERE r.employe = :employe
    AND r.menuWeekDay.date BETWEEN :start AND :end
""")
    List<LunchReservation> findByEmployeAndDateBetween(
            Employe employe,
            LocalDate start,
            LocalDate end
    );

    boolean existsByEmployeAndMenuWeekDay(Employe employe, MenuWeekDay day);

    List<LunchReservation> findByEmployeInAndMenuWeekDayIn(List<Employe> employes, List<MenuWeekDay> days);

    List<LunchReservation> findByEmployeAndMenuWeekDay_DateIn(Employe employe, List<LocalDate> dates);

}
