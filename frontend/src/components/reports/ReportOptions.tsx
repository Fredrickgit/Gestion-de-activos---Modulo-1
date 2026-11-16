import { useRef } from 'react';
import { Calendar, Download } from 'lucide-react';
import type { ReportExportFormat, ReportFilters } from '@/types';
import { REPORT_TYPES } from '@/data/catalog';
import { formatDisplayDate } from '@/lib/format';
import { cn } from '@/lib/cn';

interface ReportOptionsProps {
  filters: ReportFilters;
  onChange: (patch: Partial<ReportFilters>) => void;
}

const EXPORT_FORMATS: Array<{ value: ReportExportFormat; label: string }> = [
  { value: 'pdf', label: 'PDF' },
  { value: 'excel', label: 'Excel' },
  { value: 'csv', label: 'CSV' },
];

const FIELD_SHELL =
  'flex h-10 items-center gap-2 rounded-lg border border-line bg-white px-3 transition-colors focus-within:border-brand/50 focus-within:ring-2 focus-within:ring-brand/10';

function DateRangeField({
  label,
  value,
  onChange,
}: {
  label: string;
  value: string;
  onChange: (value: string) => void;
}) {
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <div className={cn(FIELD_SHELL, 'relative w-full sm:w-[236px]')}>
      <Calendar className="h-4 w-4 shrink-0 text-muted" aria-hidden />
      <span className="shrink-0 text-[13px] font-semibold text-ink">{label}</span>
      <span className="truncate text-[13px] text-muted">{formatDisplayDate(value)}</span>
      <input
        ref={inputRef}
        type="date"
        aria-label={label === 'Desde:' ? 'Fecha inicial del reporte' : 'Fecha final del reporte'}
        value={value}
        onChange={(event) => onChange(event.target.value)}
        onClick={() => {
          try {
            inputRef.current?.showPicker?.();
          } catch {
            /* Algunos navegadores no permiten mostrar el calendario programáticamente. */
          }
        }}
        className="absolute inset-0 h-full w-full cursor-pointer rounded-lg opacity-0"
      />
    </div>
  );
}

export function ReportOptions({ filters, onChange }: ReportOptionsProps) {
  return (
    <div className="flex flex-col gap-3">
      <p className="text-[13px] font-semibold text-ink">Opciones de reporte</p>

      <div className="flex flex-wrap items-center gap-3">
        <DateRangeField
          label="Desde:"
          value={filters.from}
          onChange={(value) => onChange({ from: value })}
        />
        <DateRangeField
          label="Hasta:"
          value={filters.to}
          onChange={(value) => onChange({ to: value })}
        />

        <div className="relative w-full sm:w-[236px]">
          <label
            className="flex h-10 items-center gap-2 rounded-lg border border-line bg-white px-3 transition-colors focus-within:border-brand/50 focus-within:ring-2 focus-within:ring-brand/10"
            htmlFor="report-type"
          >
            <span className="pointer-events-none text-[13px] font-semibold text-ink">Tipo</span>
            <select
              id="report-type"
              value={filters.kind}
              onChange={(event) =>
                onChange({ kind: event.target.value as ReportFilters['kind'] })
              }
              className="h-full min-w-0 flex-1 cursor-pointer appearance-none bg-transparent text-[13px] text-muted outline-none"
            >
              {REPORT_TYPES.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            <svg
              className="pointer-events-none h-4 w-4 shrink-0 text-muted"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              strokeWidth={2}
              aria-hidden
            >
              <path d="m6 9 6 6 6-6" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </label>
        </div>

        <div className="flex h-10 w-full items-center gap-2.5 rounded-lg border border-line bg-white px-3 sm:w-auto">
          <Download className="h-4 w-4 shrink-0 text-muted" aria-hidden />
          <span className="shrink-0 text-[13px] font-semibold text-ink">Exportar</span>
          <div
            role="group"
            aria-label="Formato de exportación del reporte"
            className="flex items-center gap-1.5"
          >
            {EXPORT_FORMATS.map((format) => {
              const selected = filters.format === format.value;
              return (
                <button
                  key={format.value}
                  type="button"
                  aria-pressed={selected}
                  onClick={() => onChange({ format: format.value })}
                  className={cn(
                    'h-7 rounded-md px-2.5 text-[13px] transition-colors',
                    selected
                      ? 'bg-active-soft font-semibold text-brand-dark'
                      : 'text-ink hover:bg-soft',
                  )}
                >
                  {format.label}
                </button>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}
