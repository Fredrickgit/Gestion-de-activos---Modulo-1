import { SUMMARY_STATUSES } from '@/constants/status';
import type { ResourceStatus } from '@/types';
import { cn } from '@/lib/cn';

interface StatusSummaryProps {
  value: ResourceStatus | 'todos';
  onChange: (value: ResourceStatus | 'todos') => void;
}

export function StatusSummary({ value, onChange }: StatusSummaryProps) {
  return (
    <div className="rounded-[10px] border border-line bg-white p-4">
      <div className="flex items-center justify-between gap-3">
        <span className="font-display text-xs font-semibold uppercase tracking-[0.08em] text-muted">
          Resumen por estado
        </span>
        {value !== 'todos' ? (
          <button
            type="button"
            onClick={() => onChange('todos')}
            className="font-display text-[11px] font-semibold text-brand hover:underline"
          >
            Ver todos
          </button>
        ) : null}
      </div>

      <div
        role="group"
        aria-label="Filtrar por estado del recurso"
        className="no-scrollbar mt-3 flex max-w-full items-center gap-1 overflow-x-auto rounded-lg bg-soft p-1"
      >
        {SUMMARY_STATUSES.map((item) => {
          const active = value === item.status;
          return (
            <button
              key={item.status}
              type="button"
              aria-pressed={active}
              onClick={() => onChange(active ? 'todos' : item.status)}
              className={cn(
                'shrink-0 rounded-md px-4 py-1.5 font-display text-[13px] font-semibold transition-colors duration-150',
                active
                  ? 'bg-brand/80 text-white shadow-sm'
                  : 'text-muted hover:bg-white/70 hover:text-ink',
              )}
            >
              {item.label}: {item.count}
            </button>
          );
        })}
      </div>
    </div>
  );
}
