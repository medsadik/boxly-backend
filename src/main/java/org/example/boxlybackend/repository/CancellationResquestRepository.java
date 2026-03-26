package org.example.boxlybackend.repository;

import org.example.boxlybackend.entites.CancellationRequest;
import org.example.boxlybackend.entites.Employe;
import org.example.boxlybackend.entites.Enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CancellationResquestRepository extends JpaRepository<CancellationRequest,Long> {


    List<CancellationRequest> findByEmployeAndStatus(Employe employe, RequestStatus requestStatus);

    List<CancellationRequest> findByEmployeInAndStatus(List<Employe> employes, RequestStatus requestStatus);

    boolean existsByEmployeAndStartDateAndEndDate(Employe employe, LocalDate date, LocalDate date1);

    List<CancellationRequest> findByEmployeEmail(String email);
}
