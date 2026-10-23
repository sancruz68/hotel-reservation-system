
package com.sancruz.hotel.room;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public List<Room> getActiveRooms() {
        return roomService.getActiveRooms();
    }

    @GetMapping("/available")
    public List<Room> getAvailableRooms(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkInDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate checkOutDate,

            @RequestParam
            int guestCount) {

        return roomService.getAvailableRooms(
                checkInDate,
                checkOutDate,
                guestCount
        );
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        Room room = roomService.createRoom(
                request.roomNumber(),
                request.roomType(),
                request.capacity(),
                request.nightlyRate()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    public record CreateRoomRequest(
            @NotBlank String roomNumber,
            @NotBlank String roomType,
            @NotNull @Min(1) Integer capacity,
            @NotNull @DecimalMin("0.01") BigDecimal nightlyRate
    ) {
    }
}
