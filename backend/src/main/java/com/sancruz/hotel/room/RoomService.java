
package com.sancruz.hotel.room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.sancruz.hotel.reservation.Reservation.ReservationStatus;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Transactional(readOnly = true)
    public List<Room> getActiveRooms() {
        return roomRepository.findByActiveTrueOrderByRoomNumberAsc();
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Room not found"
                ));
    }

    @Transactional(readOnly = true)
    public List<Room> getAvailableRooms(
            LocalDate checkInDate,
            LocalDate checkOutDate,
            int guestCount) {

        if (checkInDate == null
                || checkOutDate == null
                || !checkOutDate.isAfter(checkInDate)
                || guestCount < 1) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid availability search details"
            );
        }

        return roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                guestCount,
                ReservationStatus.CONFIRMED
        );
    }

    @Transactional
    public Room createRoom(
            String roomNumber,
            String roomType,
            int capacity,
            BigDecimal nightlyRate) {

        if (roomNumber == null || roomNumber.isBlank()
                || roomType == null || roomType.isBlank()
                || capacity < 1
                || nightlyRate == null
                || nightlyRate.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid room details"
            );
        }

        String normalizedNumber = roomNumber.trim();

        if (roomRepository.existsByRoomNumberIgnoreCase(normalizedNumber)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Room number already exists"
            );
        }

        Room room = new Room(
                normalizedNumber,
                roomType.trim(),
                capacity,
                nightlyRate
        );

        return roomRepository.save(room);
    }
}
