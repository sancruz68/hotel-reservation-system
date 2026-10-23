
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Room {
  id: number;
  roomNumber: string;
  roomType: string;
  capacity: number;
  nightlyRate: number;
  active: boolean;
}

export interface Guest {
  id: number;
  fullName: string;
  email: string;
  phone: string | null;
}

export interface CreateGuestRequest {
  fullName: string;
  email: string;
  phone: string | null;
}

export interface CreateReservationRequest {
  guestId: number;
  roomId: number;
  checkInDate: string;
  checkOutDate: string;
  guestCount: number;
}

export interface Reservation {
  id: number;
  guestId: number;
  roomId: number;
  checkInDate: string;
  checkOutDate: string;
  guestCount: number;
  totalPrice: number;
  status: 'CONFIRMED' | 'CANCELLED';
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class HotelApiService {

  private readonly http = inject(HttpClient);

  private readonly roomsUrl = '/api/rooms';
  private readonly guestsUrl = '/api/guests';
  private readonly reservationsUrl = '/api/reservations';

  getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(this.roomsUrl);
  }

  getAvailableRooms(
    checkInDate: string,
    checkOutDate: string,
    guestCount: number
  ): Observable<Room[]> {

    const params = new HttpParams()
      .set('checkInDate', checkInDate)
      .set('checkOutDate', checkOutDate)
      .set('guestCount', guestCount.toString());

    return this.http.get<Room[]>(
      `${this.roomsUrl}/available`,
      { params }
    );
  }

  createGuest(
    request: CreateGuestRequest
  ): Observable<Guest> {

    return this.http.post<Guest>(
      this.guestsUrl,
      request
    );
  }

  createReservation(
    request: CreateReservationRequest
  ): Observable<Reservation> {

    return this.http.post<Reservation>(
      this.reservationsUrl,
      request
    );
  }

  getReservation(
    id: number
  ): Observable<Reservation> {

    return this.http.get<Reservation>(
      `${this.reservationsUrl}/${id}`
    );
  }
}
