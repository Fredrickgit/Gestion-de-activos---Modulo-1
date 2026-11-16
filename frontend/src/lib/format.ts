const MONTHS_SHORT = [
  'ene',
  'feb',
  'mar',
  'abr',
  'may',
  'jun',
  'jul',
  'ago',
  'sep',
  'oct',
  'nov',
  'dic',
];

function parseDate(value: string): Date | null {
  if (!value) return null;
  const [year, month, day] = value.slice(0, 10).split('-').map(Number);
  if (!year || !month || !day) return null;
  return new Date(year, month - 1, day);
}

export function formatDisplayDate(value: string): string {
  const date = parseDate(value);
  if (!date) return '—';
  return new Intl.DateTimeFormat('es-CO', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
  }).format(date);
}

export function formatLongDate(value: string): string {
  const date = parseDate(value);
  if (!date) return '—';
  return `${String(date.getDate()).padStart(2, '0')} ${MONTHS_SHORT[date.getMonth()]} ${date.getFullYear()}`;
}

export function formatDateTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return formatLongDate(value);
  return new Intl.DateTimeFormat('es-CO', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(date);
}

export function todayISO(): string {
  const now = new Date();
  const offset = now.getTimezoneOffset();
  return new Date(now.getTime() - offset * 60_000).toISOString().slice(0, 10);
}

export function isWithinRange(
  value: string,
  from: string,
  to: string,
): boolean {
  if (!value) return false;
  if (from && value < from) return false;
  if (to && value > to) return false;
  return true;
}

export function pluralize(count: number, singular: string, plural: string) {
  return `${count} ${count === 1 ? singular : plural}`;
}

export function initials(name: string): string {
  return name
    .trim()
    .split(/\s+/)
    .slice(0, 2)
    .map((part) => part.charAt(0).toUpperCase())
    .join('');
}

export function titleCase(value: string): string {
  return value
    .toLowerCase()
    .replace(/(^|\s|\/)([a-záéíóúñ])/g, (_, space: string, letter: string) => {
      return space + letter.toUpperCase();
    });
}
