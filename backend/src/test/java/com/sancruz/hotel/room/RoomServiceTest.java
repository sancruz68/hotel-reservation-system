
package com.sancruz.hotel.room;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class RoomServiceTest {

    private RoomRepository roomRepository;
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        roomRepository = mock(RoomRepository.class);
        roomService = new RoomService(roomRepository);
    }

    @Test
    void shouldCreateValidRoom() {

        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Room room = roomService.createRoom(
                "101",
                "Standard Queen",
                2,
                new BigDecimal("129.99")
        );

        assertEquals("101", room.getRoomNumber());
        assertEquals("Standard Queen", room.getRoomType());
        assertEquals(2, room.getCapacity());
        assertEquals(
                new BigDecimal("129.99"),
                room.getNightlyRate()
        );

        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void shouldRejectDuplicateRoomNumber() {

        when(roomRepository.existsByRoomNumberIgnoreCase("101"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roomService.createRoom(
                        "101",
                        "Standard Queen",
                        2,
                        new BigDecimal("129.99")
                )
        );

        assertEquals(
                HttpStatus.CONFLICT,
                exception.getStatusCode()
        );

        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void shouldRejectInvalidRoomCapacity() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roomService.createRoom(
                        "102",
                        "King Suite",
                        0,
                        new BigDecimal("199.99")
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void shouldRejectInvalidNightlyRate() {

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roomService.createRoom(
                        "103",
                        "Double Queen",
                        4,
                        BigDecimal.ZERO
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void shouldRejectInvalidSearchDates() {

        LocalDate checkIn = LocalDate.of(2026, 11, 20);
        LocalDate checkOut = LocalDate.of(2026, 11, 18);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roomService.getAvailableRooms(
                        checkIn,
                        checkOut,
                        2
                )
        );

        assertEquals(
                HttpStatus.BAD_REQUEST,
                exception.getStatusCode()
        );

        verifyNoInteractions(roomRepository);
    }

    @Test
    void shouldSearchAvailableRooms() {

        LocalDate checkIn = LocalDate.of(2026, 11, 18);
        LocalDate checkOut = LocalDate.of(2026, 11, 20);

        Room room = new Room(
                "101",
                "Standard Queen",
                2,
                new BigDecimal("129.99")
        );

        when(roomRepository.findAvailableRooms(
                checkIn,
                checkOut,
                2,
                ReservationStatus.CONFIRMED
        )).thenReturn(List.of(room));

        List<Room> results = roomService.getAvailableRooms(
                checkIn,
                checkOut,
                2
        );

        assertEquals(1, results.size());
        assertEquals("101", results.get(0).getRoomNumber());

        verify(roomRepository).findAvailableRooms(
                checkIn,
                checkOut,
                2,
                ReservationStatus.CONFIRMED
        );
    }

    @Test
    void shouldReturnNotFoundForMissingRoom() {

        when(roomRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> roomService.getRoomById(999L)
        );

        assertEquals(
                HttpStatus.NOT_FOUND,
                exception.getStatusCode()
        );
    }
}
