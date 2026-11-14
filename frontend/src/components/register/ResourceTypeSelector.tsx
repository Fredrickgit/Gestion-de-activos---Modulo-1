import type { ResourceKind } from '@/types';
import { cn } from '@/lib/cn';

interface ResourceTypeSelectorProps {
  value: ResourceKind;
  onChange: (value: ResourceKind) => void;
}

const OPTIONS: Array<{
  value: ResourceKind;
  title: string;
  description: string;
}> = [
  {
    value: 'activo',
    title: 'Activo Físico',
    description: 'Equipos, cómputo, mobiliario de laboratorios, multimedia y oficinas.',
  },
  {
    value: 'espacio',
    title: 'Espacio Académico',
    description:
      'Aulas teóricas, auditorios de eventos, laboratorios completos o salas especiales.',
  },
];

export function ResourceTypeSelector({ value, onChange }: ResourceTypeSelectorProps) {
  return (
    <div
      role="radiogroup"
      aria-label="Tipo de recurso a registrar"
      className="grid gap-3 sm:grid-cols-2"
    >
      {OPTIONS.map((option) => {
        const selected = option.value === value;
        return (
          <label
            key={option.value}
            className={cn(
              'flex cursor-pointer items-start gap-4 rounded-xl border p-4 transition-colors duration-150 sm:p-5',
              selected
                ? 'border-brand/30 bg-active-soft'
                : 'border-line bg-white hover:border-slate-300 hover:bg-soft/60',
            )}
          >
            <input
              type="radio"
              name="resource-kind"
              value={option.value}
              checked={selected}
              onChange={() => onChange(option.value)}
              className="sr-only"
            />
            <span
              className={cn(
                'mt-1 flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2 bg-white transition-colors',
                selected ? 'border-brand' : 'border-line',
              )}
              aria-hidden
            >
              {selected ? <span className="h-2.5 w-2.5 rounded-full bg-brand" /> : null}
            </span>
            <span className="min-w-0">
              <span
                className={cn(
                  'block font-sans text-base font-bold leading-tight',
                  selected ? 'text-brand' : 'text-ink',
                )}
              >
                {option.title}
              </span>
              <span className="mt-1 block text-[13px] leading-snug text-muted">
                {option.description}
              </span>
            </span>
          </label>
        );
      })}
    </div>
  );
}
