package org.example.boxlybackend.repository;

import org.example.boxlybackend.entites.MenuWeekDayOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MenuWeekDayOptionRepository extends JpaRepository<MenuWeekDayOption, Long> {

    boolean existsByMenuWeekDayDateAndMenuOptionId(LocalDate date , Long menuOptionId);

    Optional<MenuWeekDayOption> findByMenuWeekDayDateAndMenuOptionId(LocalDate date , Long menuOptionId);

    List<MenuWeekDayOption> findByMenuWeekDay_Date(LocalDate date);
    // Reset all defaults for a given day before setting a new one
    @Modifying
    @Query("UPDATE MenuWeekDayOption o SET o.defaultOption = false WHERE o.menuWeekDay.id = :dayId")
    void resetDefaultsForDay(Long dayId);
}