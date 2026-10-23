
package com.sancruz.hotel.reservation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import com.sancruz.hotel.guest.Guest;
import com.sancruz.hotel.guest.GuestRepository;
import com.sancruz.hotel.room.Room;
import com.sancruz.hotel.room.RoomRepository;
import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private RoomRepository roomRepository;
    private GuestRepository guestRepository;
    private ReservationService reservationService;

    private Guest guest;
    private Room room;

    private final LocalDate checkIn =
            LocalDate.of(2026, 11, 20);

    private final LocalDate checkOut =
            LocalDate.of(2026, 11, 22);

    @BeforeEach
    void setUp() {

        reservationRepository = mock(ReservationRepository.class);
        roomRepository = mock(RoomRepository.class);
        guestRepository = mock(GuestRepository.class);

        reservationService = new ReservationService(
                reservationRepository,
                roomRepository,
                guestRepository
        );

        guest = new Guest(
                "Alex Rivera",
                "alex@example.com",
                "717-555-0101"
        );

        room = new Room(
                "101",
                "Standard Queen",
                2,
                new BigDecimal("129.99")
        );
    }

    private void mockExistingGuestAndRoom() {

        when(guestRepository.findById(1L))
                .thenReturn(Optional.of(guest));

        when(roomRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(room));
    }

    @Test
    void shouldCreateReservationWithCorrectTotalPrice() {

        mockExistingGuestAndRoom();

        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var result = reservationService.createReservation(
                1L,
                1L,
                checkIn,
                checkOut,
                2
        );

        assertEquals(
                new BigDecimal("259.98"),
                result.totalPrice()
        );

        assertEquals(
                ReservationStatus.CONFIRMED,
                result.status()
        );

        assertEquals(2, result.guestCount());

        verify(reservationRepository).save(
                any(Reservation.class)
        );

        verify(roomRepository).findByIdForUpdate(1L);
    }

    @Test
    void shouldRejectOverlappingReservation() {

        mockExistingGuestAndRoom();

        when(reservationRepository
                .existsByRoom_IdAndStatusAndCheckInDateLessThanAndCheckOutDateGreaterThan(
                        1L,
                        ReservationStatus.CONFIRMED,
                        checkOut,
                        checkIn
                ))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkIn,
                        checkOut,
                        2
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void shouldRejectGuestCountAboveRoomCapacity() {

        mockExistingGuestAndRoom();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkIn,
                        checkOut,
                        3
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void shouldRejectSameDayCheckInAndCheckOut() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkIn,
                        checkIn,
                        2
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldRejectCheckoutBeforeCheckin() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkOut,
                        checkIn,
                        2
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldRejectMissingGuest() {

        when(guestRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        999L,
                        1L,
                        checkIn,
                        checkOut,
                        2
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void shouldRejectMissingRoom() {

        when(guestRepository.findById(1L))
                .thenReturn(Optional.of(guest));

        when(roomRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        999L,
                        checkIn,
                        checkOut,
                        2
                )
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void shouldRejectInactiveRoom() {

        room.setActive(false);

        mockExistingGuestAndRoom();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkIn,
                        checkOut,
                        2
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void shouldRejectZeroGuests() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.createReservation(
                        1L,
                        1L,
                        checkIn,
                        checkOut,
                        0
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(reservationRepository);
    }

    @Test
    void shouldCancelConfirmedReservation() {

        Reservation reservation = new Reservation(
                guest,
                room,
                checkIn,
                checkOut,
                2,
                new BigDecimal("259.98")
        );

        when(reservationRepository.findById(3L))
                .thenReturn(Optional.of(reservation));

        var result = reservationService.cancelReservation(3L);

        assertEquals(
                ReservationStatus.CANCELLED,
                result.status()
        );

        assertEquals(
                ReservationStatus.CANCELLED,
                reservation.getStatus()
        );
    }

    @Test
    void shouldReturnNotFoundForMissingReservation() {

        when(reservationRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> reservationService.getReservationById(999L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }
}
