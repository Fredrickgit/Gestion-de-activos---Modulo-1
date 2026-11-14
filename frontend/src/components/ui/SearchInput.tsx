import { Search, X } from 'lucide-react';
import { cn } from '@/lib/cn';

interface SearchInputProps {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  ariaLabel?: string;
  className?: string;
  inputClassName?: string;
  size?: 'sm' | 'md';
}

export function SearchInput({
  value,
  onChange,
  placeholder = 'Buscar...',
  ariaLabel = 'Buscar',
  className,
  inputClassName,
  size = 'md',
}: SearchInputProps) {
  return (
    <div
      className={cn(
        'flex items-center gap-2 rounded-lg border border-line bg-white px-3 transition-colors focus-within:border-brand/40 focus-within:ring-2 focus-within:ring-brand/10',
        size === 'sm' ? 'h-8' : 'h-9',
        className,
      )}
    >
      <Search className="h-4 w-4 shrink-0 text-muted" strokeWidth={2} aria-hidden />
      <input
        value={value}
        onChange={(event) => onChange(event.target.value)}
        placeholder={placeholder}
        aria-label={ariaLabel}
        className={cn(
          'min-w-0 flex-1 bg-transparent text-[13px] text-ink outline-none placeholder:font-display placeholder:italic placeholder:text-muted/90',
          size === 'md' && 'placeholder:text-sm',
          inputClassName,
        )}
      />
      {value ? (
        <button
          type="button"
          onClick={() => onChange('')}
          aria-label="Limpiar búsqueda"
          className="shrink-0 rounded p-0.5 text-muted/80 transition-colors hover:bg-soft hover:text-ink"
        >
          <X className="h-3.5 w-3.5" aria-hidden />
        </button>
      ) : null}
    </div>
  );
}
