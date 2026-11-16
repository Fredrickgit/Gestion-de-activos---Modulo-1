import { REPORT_TYPES } from '@/data/catalog';
import type { ReportFilters } from '@/types';
import { formatDisplayDate } from '@/lib/format';
import { Button } from '@/components/ui/Button';

interface ReportSummaryProps {
  filters: ReportFilters;
  count: number;
  canClear: boolean;
  onClear: () => void;
}

export function ReportSummary({ filters, count, canClear, onClear }: ReportSummaryProps) {
  const kindLabel =
    REPORT_TYPES.find((option) => option.value === filters.kind)?.label ?? 'Todos los recursos';

  return (
    <div className="flex flex-col gap-3 rounded-[10px] border border-line bg-soft px-3 py-2.5 sm:flex-row sm:items-center sm:justify-between">
      <p className="min-w-0 text-[13px] leading-relaxed text-ink">
        <span className="font-semibold">Filtro activo:</span> {kindLabel}{' '}
        <span className="mx-1 text-muted/70" aria-hidden>
          |
        </span>{' '}
        <span className="font-semibold">Período:</span> {formatDisplayDate(filters.from)} -{' '}
        {formatDisplayDate(filters.to)}{' '}
        <span className="mx-1 text-muted/70" aria-hidden>
          |
        </span>{' '}
        <span className="font-semibold">
          {count} resultado{count === 1 ? '' : 's'} encontrado{count === 1 ? '' : 's'}
        </span>
      </p>
      <Button
        variant="outline"
        size="sm"
        onClick={onClear}
        disabled={!canClear}
        className="shrink-0 self-start sm:self-auto"
      >
        Limpiar filtros
      </Button>
    </div>
  );
}
