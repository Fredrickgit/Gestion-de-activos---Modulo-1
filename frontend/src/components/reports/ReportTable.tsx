import { ArrowDown, ArrowUp, ChevronsUpDown, SearchX } from 'lucide-react';
import type { ReportRow, ReportSortKey } from '@/types';
import { formatDisplayDate } from '@/lib/format';
import { cn } from '@/lib/cn';
import { StatusBadge } from '@/components/ui/Badge';

export type SortDirection = 'asc' | 'desc';

interface ReportTableProps {
  rows: ReportRow[];
  sortKey: ReportSortKey | null;
  sortDirection: SortDirection;
  onSort: (key: ReportSortKey) => void;
}

const COLUMNS: Array<{
  key: ReportSortKey;
  label: string;
  className: string;
  cellClassName?: string;
}> = [
  { key: 'id', label: 'ID Recurso', className: 'w-[130px]' },
  { key: 'name', label: 'Nombre del Recurso', className: 'min-w-[280px]' },
  { key: 'type', label: 'Tipo', className: 'w-[180px]' },
  { key: 'location', label: 'Ubicación Física', className: 'w-[250px]' },
  { key: 'status', label: 'Estado', className: 'w-[160px]' },
  { key: 'capacity', label: 'Aforo / Capacidad', className: 'w-[150px]' },
  { key: 'date', label: 'Fecha Registro', className: 'w-[140px]' },
];

export function ReportTable({ rows, sortKey, sortDirection, onSort }: ReportTableProps) {
  return (
    <div className="overflow-x-auto">
      <table className="w-full min-w-[1160px] border-collapse text-left">
        <thead>
          <tr className="border-b border-line bg-soft">
            {COLUMNS.map((column) => {
              const active = sortKey === column.key;
              const Icon = !active ? ChevronsUpDown : sortDirection === 'asc' ? ArrowUp : ArrowDown;
              return (
                <th
                  key={column.key}
                  scope="col"
                  className={cn('px-4 py-3 align-middle', column.className)}
                  aria-sort={
                    active ? (sortDirection === 'asc' ? 'ascending' : 'descending') : 'none'
                  }
                >
                  <button
                    type="button"
                    onClick={() => onSort(column.key)}
                    className={cn(
                      'group inline-flex items-center gap-1 text-[13px] font-semibold transition-colors',
                      active ? 'text-brand-dark' : 'text-ink hover:text-brand-dark',
                    )}
                  >
                    {column.label}
                    <Icon
                      className={cn(
                        'h-3.5 w-3.5 shrink-0',
                        active ? 'text-brand' : 'text-muted group-hover:text-brand-dark',
                      )}
                      aria-hidden
                    />
                  </button>
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {rows.length === 0 ? (
            <tr>
              <td colSpan={COLUMNS.length} className="px-4 py-16">
                <div className="mx-auto flex max-w-sm flex-col items-center text-center">
                  <span className="flex h-11 w-11 items-center justify-center rounded-full bg-soft text-muted">
                    <SearchX className="h-5 w-5" aria-hidden />
                  </span>
                  <p className="mt-3 font-display text-sm font-bold text-ink">
                    Sin resultados en este período
                  </p>
                  <p className="mt-1 text-[13px] leading-relaxed text-muted">
                    Ajusta las fechas o el tipo de recurso para generar el reporte.
                  </p>
                </div>
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr
                key={row.id}
                className="h-[55px] border-b border-line bg-white transition-colors even:bg-soft/60 last:border-b-0 hover:bg-active-soft/50"
              >
                <td className="px-4 py-2">
                  <span className="whitespace-nowrap font-mono text-[13px] font-bold text-ink">
                    {row.id}
                  </span>
                </td>
                <td className="px-4 py-2">
                  <span className="block truncate text-sm font-medium text-ink" title={row.name}>
                    {row.name}
                  </span>
                </td>
                <td className="px-4 py-2">
                  <span className="text-[13px] text-muted">{row.type}</span>
                </td>
                <td className="px-4 py-2">
                  <span className="block truncate text-[13px] text-muted" title={row.location}>
                    {row.location}
                  </span>
                </td>
                <td className="px-4 py-2">
                  <StatusBadge status={row.status} />
                </td>
                <td className="px-4 py-2">
                  <span className="whitespace-nowrap text-[13px] text-ink">{row.capacity}</span>
                </td>
                <td className="px-4 py-2">
                  <span className="whitespace-nowrap text-[13px] text-muted">
                    {formatDisplayDate(row.date)}
                  </span>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
