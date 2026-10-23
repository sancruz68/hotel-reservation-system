
package com.sancruz.hotel.guest;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/guests")
public class GuestController {

    private final GuestService guestService;

    public GuestController(GuestService guestService) {
        this.guestService = guestService;
    }

    @GetMapping
    public List<Guest> getAllGuests() {
        return guestService.getAllGuests();
    }

    @GetMapping("/{id}")
    public Guest getGuestById(@PathVariable Long id) {
        return guestService.getGuestById(id);
    }

    @PostMapping
    public ResponseEntity<Guest> createGuest(
            @Valid @RequestBody CreateGuestRequest request) {

        Guest guest = guestService.createGuest(
                request.fullName(),
                request.email(),
                request.phone()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(guest);
    }

    public record CreateGuestRequest(
            @NotBlank
            @Size(max = 120)
            String fullName,

            @NotBlank
            @Email
            @Size(max = 254)
            String email,

            @Size(max = 30)
            String phone
    ) {
    }
}
