
export const translations = {
  en: {
    language: 'Language',
    rooms: 'Rooms',
    about: 'About',

    welcome: 'WELCOME TO NORTHSTAR HOTEL',
    heroTitle: 'Your next stay starts here.',
    heroDescription:
      'Discover comfortable rooms, explore availability, and plan your stay with confidence.',

    planVisit: 'PLAN YOUR VISIT',
    findRoom: 'Find Your Room',
    selectDates: 'Select your dates and number of guests.',
    checkIn: 'Check-in',
    checkOut: 'Check-out',
    guests: 'Guests',
    guest: 'Guest',
    searching: 'Searching...',
    searchRooms: 'Search Rooms',
    searchingRooms: 'Searching available rooms...',

    accommodations: 'ACCOMMODATIONS',
    availableRooms: 'Available Rooms',
    browseRooms:
      'Browse rooms that match your selected dates and guests.',
    room: 'ROOM',
    upTo: 'Up to',
    perNight: 'per night',
    bookRoom: 'Book Room',

    perfectRoom: 'Your perfect room awaits.',
    selectDatesPrompt:
      'Select your dates and search to see available rooms.',
    noRooms: 'No rooms available.',
    tryDifferent: 'Try different dates or fewer guests.',

    completeReservation: 'COMPLETE YOUR RESERVATION',
    fullName: 'Full Name',
    emailAddress: 'Email Address',
    phoneOptional: 'Phone Number (Optional)',
    confirmBooking: 'Confirm Booking',
    processing: 'Processing...',
    cancel: 'Cancel',

    reservationConfirmed: 'RESERVATION CONFIRMED',
    bookingConfirmed: 'Your booking is confirmed!',
    confirmationNumber: 'Confirmation Number',
    total: 'Total',
    status: 'Status',

    aboutTitle: 'Stay comfortably. Travel confidently.',
    aboutDescription:
      'Northstar Hotel makes it simple to find and reserve a room that fits your travel plans.',
    footer: 'Northstar Hotel | Reservation System',

    errorDates: 'Please select your check-in and check-out dates.',
    errorDateOrder: 'Check-out must be after check-in.',
    errorGuests: 'Please select at least one guest.',
    errorSearch: 'Unable to search rooms. Please try again.',
    errorGuestDetails: 'Please enter your name and email address.',
    errorEmail: 'Please enter a valid email address.',
    errorBookingConflict:
      'Booking could not be completed. The email may already be registered, or the room may have been booked. Please check and try again.',
    errorBooking:
      'Unable to complete your booking. Please try again.'
  },

  es: {
    language: 'Idioma',
    rooms: 'Habitaciones',
    about: 'Acerca de',

    welcome: 'BIENVENIDO A NORTHSTAR HOTEL',
    heroTitle: 'Tu próxima estadía comienza aquí.',
    heroDescription:
      'Descubre habitaciones cómodas, consulta la disponibilidad y planifica tu estadía con confianza.',

    planVisit: 'PLANIFICA TU VISITA',
    findRoom: 'Encuentra tu habitación',
    selectDates: 'Selecciona las fechas y el número de huéspedes.',
    checkIn: 'Entrada',
    checkOut: 'Salida',
    guests: 'Huéspedes',
    guest: 'Huésped',
    searching: 'Buscando...',
    searchRooms: 'Buscar habitaciones',
    searchingRooms: 'Buscando habitaciones disponibles...',

    accommodations: 'ALOJAMIENTO',
    availableRooms: 'Habitaciones disponibles',
    browseRooms:
      'Explora las habitaciones disponibles para tus fechas y el número de huéspedes.',
    room: 'HABITACIÓN',
    upTo: 'Hasta',
    perNight: 'por noche',
    bookRoom: 'Reservar habitación',

    perfectRoom: 'Tu habitación ideal te espera.',
    selectDatesPrompt:
      'Selecciona tus fechas para ver las habitaciones disponibles.',
    noRooms: 'No hay habitaciones disponibles.',
    tryDifferent: 'Prueba otras fechas o menos huéspedes.',

    completeReservation: 'COMPLETA TU RESERVA',
    fullName: 'Nombre completo',
    emailAddress: 'Correo electrónico',
    phoneOptional: 'Teléfono (opcional)',
    confirmBooking: 'Confirmar reserva',
    processing: 'Procesando...',
    cancel: 'Cancelar',

    reservationConfirmed: 'RESERVA CONFIRMADA',
    bookingConfirmed: '¡Tu reserva está confirmada!',
    confirmationNumber: 'Número de confirmación',
    total: 'Total',
    status: 'Estado',

    aboutTitle: 'Descansa cómodamente. Viaja con confianza.',
    aboutDescription:
      'Northstar Hotel facilita la búsqueda y reserva de una habitación que se adapte a tus planes de viaje.',
    footer: 'Northstar Hotel | Sistema de Reservas',

    errorDates: 'Selecciona las fechas de entrada y salida.',
    errorDateOrder: 'La fecha de salida debe ser posterior a la entrada.',
    errorGuests: 'Selecciona al menos un huésped.',
    errorSearch: 'No se pudieron buscar las habitaciones. Inténtalo de nuevo.',
    errorGuestDetails: 'Ingresa tu nombre y correo electrónico.',
    errorEmail: 'Ingresa un correo electrónico válido.',
    errorBookingConflict:
      'No se pudo completar la reserva. Es posible que el correo ya esté registrado o que la habitación esté reservada. Verifica e inténtalo de nuevo.',
    errorBooking:
      'No se pudo completar tu reserva. Inténtalo de nuevo.'
  }
} as const;

export type Language = keyof typeof translations;
export type TranslationKey = keyof typeof translations.en;

export function translate(
  language: Language,
  key: TranslationKey
): string {
  return translations[language][key];
}
