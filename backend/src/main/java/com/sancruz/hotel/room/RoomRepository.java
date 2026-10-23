
package com.sancruz.hotel.room;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomNumberIgnoreCase(String roomNumber);

    List<Room> findByActiveTrueOrderByRoomNumberAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdForUpdate(@Param("id") Long id);

    @Query("""
            SELECT r FROM Room r
            WHERE r.active = true
              AND r.capacity >= :guestCount
              AND NOT EXISTS (
                  SELECT reservation.id
                  FROM Reservation reservation
                  WHERE reservation.room = r
                    AND reservation.status = :status
                    AND reservation.checkInDate < :checkOutDate
                    AND reservation.checkOutDate > :checkInDate
              )
            ORDER BY r.roomNumber ASC
            """)
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("guestCount") int guestCount,
            @Param("status") ReservationStatus status
    );
}
