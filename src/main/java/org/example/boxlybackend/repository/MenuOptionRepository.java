package org.example.boxlybackend.repository;

import org.example.boxlybackend.entites.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuOptionRepository extends JpaRepository<MenuOption, Long> {

    // Active options only (SQLRestriction applies automatically)
    List<MenuOption> findAll();


    // Bypass SQLRestriction to fetch all including soft-deleted (admin use)
    @Query(value = "SELECT * FROM menu_options", nativeQuery = true)
    List<MenuOption> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM menu_options WHERE active = false", nativeQuery = true)
    List<MenuOption> findAllDeleted();
}