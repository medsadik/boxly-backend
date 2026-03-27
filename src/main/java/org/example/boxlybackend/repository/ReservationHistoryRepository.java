package org.example.boxlybackend.repository;

import org.example.boxlybackend.entites.ReservationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationHistoryRepository extends JpaRepository<ReservationHistory, Long> {

    List<ReservationHistory> findByReservationIdOrderByPerformedAtAsc(Long reservationId);
}
