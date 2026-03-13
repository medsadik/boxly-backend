package org.example.boxlybackend.repository;

import org.example.boxlybackend.entites.Enums.WeekStatus;
import org.example.boxlybackend.entites.MenuWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuWeekRepository extends JpaRepository<MenuWeek, Long> {

    List<MenuWeek> findByStatus(WeekStatus status);

    Optional<MenuWeek> findByStartDate(LocalDate startDate);

    boolean existsByStartDate(LocalDate startDate);

    // Fetch week with all its days eagerly (avoids N+1 in detail views)
//    @Query("SELECT mw FROM MenuWeek mw LEFT JOIN FETCH mw.days WHERE mw.id = :id")
//    Optional<MenuWeek> findByIdWithDays(Long id);
//
//    // Find the current active week
//    @Query("SELECT mw FROM MenuWeek mw WHERE mw.status = 'ACTIVE' AND mw.startDate <= :today AND mw.endDate >= :today")
//    Optional<MenuWeek> findCurrentActiveWeek(LocalDate today);
}