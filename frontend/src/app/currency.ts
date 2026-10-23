
export type DisplayCurrency = 'USD' | 'EUR' | 'GBP';

// Example conversion rates for demonstration only.
// These are not live exchange rates.
// All actual reservation prices remain in USD.
const demoRates: Record<DisplayCurrency, number> = {
  USD: 1,
  EUR: 0.90,
  GBP: 0.78
};

export function convertFromUsd(
  amountUsd: number,
  currency: DisplayCurrency
): number {
  return amountUsd * demoRates[currency];
}

export function isEstimatedCurrency(
  currency: DisplayCurrency
): boolean {
  return currency !== 'USD';
}
