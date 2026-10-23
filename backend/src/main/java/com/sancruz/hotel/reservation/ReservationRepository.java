
package com.sancruz.hotel.reservation;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    boolean existsByRoom_IdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
            Long roomId,
            ReservationStatus status,
            LocalDate checkOutDate,
            LocalDate checkInDate
    );

    List<Reservation> findByGuest_IdOrderByCreatedAtDesc(Long guestId);

    List<Reservation> findByRoom_IdOrderByCheckInDateAsc(Long roomId);
}
