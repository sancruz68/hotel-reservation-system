
package com.sancruz.hotel.reservation;

import java.time.LocalDate;
import java.util.List;

import com.sancruz.hotel.reservation.ReservationService.ReservationResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{id}")
    public ReservationResponse getReservationById(
            @PathVariable Long id) {

        return reservationService.getReservationById(id);
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {

        ReservationResponse reservation =
                reservationService.createReservation(
                        request.guestId(),
                        request.roomId(),
                        request.checkInDate(),
                        request.checkOutDate(),
                        request.guestCount()
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reservation);
    }

    @PostMapping("/{id}/cancel")
    public ReservationResponse cancelReservation(
            @PathVariable Long id) {

        return reservationService.cancelReservation(id);
    }

    public record CreateReservationRequest(
            @NotNull Long guestId,
            @NotNull Long roomId,
            @NotNull LocalDate checkInDate,
            @NotNull LocalDate checkOutDate,
            @NotNull @Min(1) Integer guestCount
    ) {
    }
}
