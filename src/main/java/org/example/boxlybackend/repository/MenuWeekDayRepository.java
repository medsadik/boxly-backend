package org.example.boxlybackend.repository;

import org.example.boxlybackend.dto.MenuWeekDayResponse;
import org.example.boxlybackend.entites.MenuWeekDay;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuWeekDayRepository extends JpaRepository<MenuWeekDay, Long> {

//    List<MenuWeekDay> findByMenuWeekId(Long menuWeekId);

//    Optional<MenuWeekDay> findByDate(LocalDate date);

//    boolean existsByDateAndMenuWeekId(LocalDate date, Long menuWeekId);

    // Fetch day with options and their menu options eagerly
//    @Query("""
//            SELECT mwd FROM MenuWeekDay mwd
//            LEFT JOIN FETCH mwd.options o
//            LEFT JOIN FETCH o.menuOption
//            WHERE mwd.date = :date
//            """)
    @EntityGraph(attributePaths = {"options", "options.menuOption"})
    Optional<MenuWeekDay> findByDate(LocalDate date);

    @EntityGraph(attributePaths = {"options", "options.menuOption"})
    List<MenuWeekDay> findByDateBetweenOrderByDateAsc(LocalDate startDate, LocalDate endDate);

    @Query("""
    SELECT m
    FROM MenuWeekDay m
    WHERE YEAR(m.date) = :year
    AND MONTH(m.date) = :month
""")
    List<MenuWeekDay> findByYearAndMonth(int year, int month);
}