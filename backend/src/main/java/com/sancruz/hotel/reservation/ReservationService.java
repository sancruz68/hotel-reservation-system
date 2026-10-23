
package com.sancruz.hotel.reservation;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import com.sancruz.hotel.guest.Guest;
import com.sancruz.hotel.guest.GuestRepository;
import com.sancruz.hotel.room.Room;
import com.sancruz.hotel.room.RoomRepository;
import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final GuestRepository guestRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            RoomRepository roomRepository,
            GuestRepository guestRepository) {

        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.guestRepository = guestRepository;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"
                ));

        return toResponse(reservation);
    }

    @Transactional
    public ReservationResponse createReservation(
            Long guestId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guestCount) {

        if (guestId == null || roomId == null
                || checkInDate == null || checkOutDate == null
                || !checkOutDate.isAfter(checkInDate)
                || guestCount < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid reservation details"
            );
        }

        Guest guest = guestRepository.findById(guestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest not found"
                ));

        Room room = roomRepository.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found"
                ));

        if (!room.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is not active"
            );
        }

        if (guestCount > room.getCapacity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Guest count exceeds room capacity"
            );
        }

        boolean overlaps =
                reservationRepository
                        .existsByRoom_IdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                                roomId,
                                ReservationStatus.CONFIRMED,
                                checkOutDate,
                                checkInDate
                        );

        if (overlaps) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room is already booked for these dates"
            );
        }

        long nights = ChronoUnit.DAYS.between(
                checkInDate,
                checkOutDate
        );

        BigDecimal totalPrice = room.getNightlyRate()
                .multiply(BigDecimal.valueOf(nights));

        Reservation reservation = new Reservation(
                guest,
                room,
                checkInDate,
                checkOutDate,
                guestCount,
                totalPrice
        );

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Reservation not found"
                ));

        if (reservation.getStatus() != ReservationStatus.CANCELLED) {
            reservation.cancel();
        }

        return toResponse(reservation);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getGuest().getId(),
                reservation.getRoom().getId(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getGuestCount(),
                reservation.getTotalPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }

    public record ReservationResponse(
            Long id,
            Long guestId,
            Long roomId,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guestCount,
            BigDecimal totalPrice,
            ReservationStatus status,
            Instant createdAt
    ) {
    }
}
