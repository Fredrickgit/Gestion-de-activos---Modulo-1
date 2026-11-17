import { SUMMARY_STATUSES } from '@/constants/status';
import type { Resource, ResourceStatus } from '@/types';
import { cn } from '@/lib/cn';

interface StatusSummaryProps {
  value: ResourceStatus | 'todos';
  onChange: (value: ResourceStatus | 'todos') => void;
  resources: Resource[];
}

export function StatusSummary({ value, onChange, resources }: StatusSummaryProps) {
  const counts = resources.reduce<Record<string, number>>((acc, resource) => {
    acc[resource.status] = (acc[resource.status] ?? 0) + 1;
    return acc;
  }, {});

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
        className="no-scrollbar mt-3 flex max-w-full flex-wrap items-center gap-1 rounded-lg bg-soft p-1"
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
                'shrink-0 rounded-md px-3 py-1.5 font-display text-[12px] font-semibold transition-colors duration-150 sm:px-4 sm:text-[13px]',
                active
                  ? 'bg-brand/80 text-white shadow-sm'
                  : 'text-muted hover:bg-white/70 hover:text-ink',
              )}
            >
              {item.label}: {counts[item.status] ?? 0}
            </button>
          );
        })}
      </div>
    </div>
  );
}
