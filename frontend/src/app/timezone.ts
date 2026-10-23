
export type DisplayTimeZone =
  | 'America/New_York'
  | 'Europe/London'
  | 'Europe/Madrid'
  | 'Asia/Tokyo';

export const timeZoneOptions: {
  value: DisplayTimeZone;
  label: string;
}[] = [
  {
    value: 'America/New_York',
    label: 'New York (ET)'
  },
  {
    value: 'Europe/London',
    label: 'London (UK)'
  },
  {
    value: 'Europe/Madrid',
    label: 'Madrid (ES)'
  },
  {
    value: 'Asia/Tokyo',
    label: 'Tokyo (JP)'
  }
];

export function formatReservationTime(
  timestamp: string,
  timeZone: DisplayTimeZone,
  language: 'en' | 'es'
): string {

  const locale = language === 'es'
    ? 'es-PR'
    : 'en-US';

  return new Intl.DateTimeFormat(locale, {
    timeZone,
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: 'numeric',
    minute: '2-digit',
    timeZoneName: 'short'
  }).format(new Date(timestamp));
}
