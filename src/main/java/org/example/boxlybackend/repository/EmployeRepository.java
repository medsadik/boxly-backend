package org.example.boxlybackend.repository;

import org.example.boxlybackend.dto.projection.MonthlyStatsProjection;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.MenuOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeRepository extends JpaRepository<Employe, Long> {

    @Query("SELECT e.email FROM Employe e WHERE e.email IS NOT NULL AND e.email != '' ORDER BY e.email asc")
    List<String> findAllEmails();

    Optional<Employe> findByEmail(String email);

    Optional<Employe> findByMatricule(Long matricule);





}
