
package com.sancruz.hotel.guest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
