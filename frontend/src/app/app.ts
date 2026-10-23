
import { CurrencyPipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { switchMap } from 'rxjs';

import {
  HotelApiService,
  Room,
  Reservation
} from './hotel-api.service';

import {
  Language,
  TranslationKey,
  translate
} from './translations';

import {
  DisplayCurrency,
  convertFromUsd,
  isEstimatedCurrency
} from './currency';

import {
  DisplayTimeZone,
  timeZoneOptions,
  formatReservationTime
} from './timezone';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule, CurrencyPipe],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {

  private readonly hotelApi = inject(HotelApiService);

  readonly language = signal<Language>('en');
  readonly displayCurrency = signal<DisplayCurrency>('USD');

  readonly selectedTimeZone =
    signal<DisplayTimeZone>('America/New_York');

  readonly availableTimeZones = timeZoneOptions;

  checkInDate = '';
  checkOutDate = '';
  guestCount = 2;

  bookingFullName = '';
  bookingEmail = '';
  bookingPhone = '';

  readonly rooms = signal<Room[]>([]);
  readonly searching = signal(false);
  readonly hasSearched = signal(false);
  readonly errorMessage = signal('');

  readonly selectedRoom = signal<Room | null>(null);
  readonly booking = signal(false);
  readonly bookingError = signal('');
  readonly confirmedReservation = signal<Reservation | null>(null);

  private searchCriteria: {
    checkInDate: string;
    checkOutDate: string;
    guestCount: number;
  } | null = null;

  setLanguage(language: Language): void {
    this.language.set(language);
    this.errorMessage.set('');
    this.bookingError.set('');
  }

  t(key: TranslationKey): string {
    return translate(this.language(), key);
  }

  setCurrency(currency: DisplayCurrency): void {
    this.displayCurrency.set(currency);
  }

  convertedPrice(amountUsd: number): number {
    return convertFromUsd(
      amountUsd,
      this.displayCurrency()
    );
  }

  isEstimatedPrice(): boolean {
    return isEstimatedCurrency(
      this.displayCurrency()
    );
  }

  setTimeZone(timeZone: DisplayTimeZone): void {
    this.selectedTimeZone.set(timeZone);
  }

  formatCreatedAt(timestamp: string): string {
    return formatReservationTime(
      timestamp,
      this.selectedTimeZone(),
      this.language()
    );
  }

  searchRooms(): void {

    if (this.searching() || this.booking()) {
      return;
    }

    this.errorMessage.set('');
    this.bookingError.set('');
    this.rooms.set([]);
    this.hasSearched.set(false);
    this.selectedRoom.set(null);
    this.confirmedReservation.set(null);
    this.searchCriteria = null;

    if (!this.checkInDate || !this.checkOutDate) {
      this.errorMessage.set(this.t('errorDates'));
      return;
    }

    if (this.checkOutDate <= this.checkInDate) {
      this.errorMessage.set(this.t('errorDateOrder'));
      return;
    }

    if (this.guestCount < 1) {
      this.errorMessage.set(this.t('errorGuests'));
      return;
    }

    const criteria = {
      checkInDate: this.checkInDate,
      checkOutDate: this.checkOutDate,
      guestCount: this.guestCount
    };

    this.searching.set(true);

    this.hotelApi.getAvailableRooms(
      criteria.checkInDate,
      criteria.checkOutDate,
      criteria.guestCount
    ).subscribe({

      next: (rooms) => {
        this.rooms.set(rooms);
        this.searchCriteria = criteria;
        this.hasSearched.set(true);
        this.searching.set(false);
      },

      error: () => {
        this.errorMessage.set(this.t('errorSearch'));
        this.searching.set(false);
      }

    });
  }

  selectRoom(room: Room): void {
    if (!this.searchCriteria) {
      return;
    }

    this.selectedRoom.set(room);
    this.bookingError.set('');
    this.confirmedReservation.set(null);
  }

  cancelSelection(): void {
    this.selectedRoom.set(null);
    this.bookingError.set('');
  }

  bookRoom(): void {

    const room = this.selectedRoom();
    const criteria = this.searchCriteria;

    if (!room || !criteria || this.booking()) {
      return;
    }

    if (!this.bookingFullName.trim() || !this.bookingEmail.trim()) {
      this.bookingError.set(this.t('errorGuestDetails'));
      return;
    }

    const email = this.bookingEmail.trim();

    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      this.bookingError.set(this.t('errorEmail'));
      return;
    }

    this.bookingError.set('');
    this.booking.set(true);

    this.hotelApi.createGuest({
      fullName: this.bookingFullName.trim(),
      email,
      phone: this.bookingPhone.trim() || null
    }).pipe(

      switchMap((guest) =>
        this.hotelApi.createReservation({
          guestId: guest.id,
          roomId: room.id,
          checkInDate: criteria.checkInDate,
          checkOutDate: criteria.checkOutDate,
          guestCount: criteria.guestCount
        })
      )

    ).subscribe({

      next: (reservation) => {
        this.confirmedReservation.set(reservation);
        this.selectedRoom.set(null);

        this.rooms.update(rooms =>
          rooms.filter(item => item.id !== room.id)
        );

        this.booking.set(false);
      },

      error: (error: HttpErrorResponse) => {

        if (error.status === 409) {
          this.bookingError.set(
            this.t('errorBookingConflict')
          );
        } else {
          this.bookingError.set(
            this.t('errorBooking')
          );
        }

        this.booking.set(false);
      }

    });
  }
}
