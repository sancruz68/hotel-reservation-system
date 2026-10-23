
package com.sancruz.hotel.guest;

import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Transactional(readOnly = true)
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Guest getGuestById(Long id) {
        return guestRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Guest not found"
                ));
    }

    @Transactional
    public Guest createGuest(
            String fullName,
            String email,
            String phone) {

        if (fullName == null || fullName.isBlank()
                || fullName.trim().length() > 120
                || email == null || email.isBlank()
                || email.trim().length() > 254
                || (phone != null && phone.length() > 30)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid guest details"
            );
        }

        String normalizedName = fullName.trim();
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        String normalizedPhone = phone == null || phone.isBlank()
                ? null
                : phone.trim();

        if (guestRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Guest email already exists"
            );
        }

        Guest guest = new Guest(
                normalizedName,
                normalizedEmail,
                normalizedPhone
        );

        return guestRepository.save(guest);
    }
}
