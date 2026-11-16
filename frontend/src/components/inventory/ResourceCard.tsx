import type { KeyboardEvent } from 'react';
import type { Resource } from '@/types';
import { STATUS_META } from '@/constants/status';
import { cn } from '@/lib/cn';

interface ResourceCardProps {
  resource: Resource;
  selected?: boolean;
  onOpen: (resource: Resource) => void;
}

interface CardField {
  label: string;
  value: string;
}

function getFields(resource: Resource): CardField[] {
  if (resource.kind === 'espacio') {
    return [
      { label: 'Nombre', value: resource.name },
      { label: 'ID', value: resource.id },
      { label: 'Aforo', value: resource.capacity ? `${resource.capacity}` : '—' },
      { label: 'Ubicación', value: resource.location },
      { label: 'Facultad', value: resource.faculty },
      { label: 'Plazo máximo', value: resource.maxTerm },
    ];
  }

  return [
    { label: 'Nombre', value: resource.name },
    { label: 'ID', value: resource.id },
    { label: 'Tipo', value: resource.category },
    { label: 'Ubicación', value: resource.location },
    { label: 'Condición física', value: resource.condition },
    { label: 'Plazo máximo', value: resource.maxTerm },
  ];
}

export function ResourceCard({ resource, selected = false, onOpen }: ResourceCardProps) {
  const meta = STATUS_META[resource.status];
  const fields = getFields(resource);

  const handleKeyDown = (event: KeyboardEvent<HTMLElement>) => {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      onOpen(resource);
    }
  };

  return (
    <article
      role="button"
      tabIndex={0}
      aria-label={`Ver detalle de ${resource.name}`}
      aria-pressed={selected}
      onClick={() => onOpen(resource)}
      onKeyDown={handleKeyDown}
      className={cn(
        'group flex cursor-pointer flex-col gap-5 rounded-[24px] border-2 border-transparent bg-card p-5 text-left transition-all duration-200 hover:-translate-y-0.5 hover:shadow-[0_16px_34px_-24px_rgba(15,23,42,0.7)] focus-visible:border-brand/50 focus-visible:outline-none md:flex-row md:items-center md:rounded-[28px] md:p-6',
        selected && 'border-brand/45',
      )}
    >
      <div className="grid min-w-0 flex-1 grid-flow-col grid-cols-2 grid-rows-3 gap-x-4 gap-y-3.5 sm:gap-x-8 sm:gap-y-4">
        {fields.map((field) => (
          <div key={field.label} className="min-w-0">
            <p className="font-display text-sm font-light italic leading-none text-black/90 lg:text-base">
              {field.label}
            </p>
            <p
              className="mt-1.5 truncate font-display text-[17px] font-semibold leading-snug text-black md:text-[19px] xl:text-[20px]"
              title={field.value}
            >
              {field.value}
            </p>
          </div>
        ))}
      </div>

      <div className="flex shrink-0 items-center justify-between gap-4 md:ml-2 md:justify-end md:gap-5">
        <p className="max-w-[180px] font-display text-[22px] font-semibold uppercase leading-[1.15] text-ink md:max-w-[200px] md:text-right md:text-[26px] xl:max-w-[248px] xl:text-[30px]">
          {meta.label}
        </p>
        <span
          className="h-1.5 w-14 shrink-0 rounded-full md:h-[150px] md:w-[28px] md:rounded-[11px] xl:h-[175px] xl:w-[33px]"
          style={{ backgroundColor: meta.bar }}
          aria-hidden
        />
      </div>
    </article>
  );
}
