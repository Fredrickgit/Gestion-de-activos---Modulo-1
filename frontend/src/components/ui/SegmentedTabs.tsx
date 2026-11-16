import { cn } from '@/lib/cn';

export interface SegmentedOption<T extends string> {
  value: T;
  label: string;
  count?: number;
}

interface SegmentedTabsProps<T extends string> {
  options: SegmentedOption<T>[];
  value: T;
  onChange: (value: T) => void;
  ariaLabel: string;
  className?: string;
  size?: 'sm' | 'md';
}

export function SegmentedTabs<T extends string>({
  options,
  value,
  onChange,
  ariaLabel,
  className,
  size = 'sm',
}: SegmentedTabsProps<T>) {
  return (
    <div
      role="tablist"
      aria-label={ariaLabel}
      className={cn(
        'no-scrollbar flex max-w-full items-center gap-1 overflow-x-auto rounded-lg bg-soft p-1',
        className,
      )}
    >
      {options.map((option) => {
        const selected = option.value === value;
        return (
          <button
            key={option.value}
            type="button"
            role="tab"
            aria-selected={selected}
            onClick={() => onChange(option.value)}
            className={cn(
              'shrink-0 rounded-md font-display font-semibold transition-colors duration-150',
              size === 'sm' ? 'px-3 py-1.5 text-[13px]' : 'px-4 py-2 text-sm',
              selected
                ? 'bg-brand/80 text-white shadow-sm'
                : 'text-muted hover:bg-white/70 hover:text-ink',
            )}
          >
            {option.label}
            {typeof option.count === 'number' ? (
              <span className={cn('ml-1', selected ? 'text-white/80' : 'text-muted/80')}>
                {option.count}
              </span>
            ) : null}
          </button>
        );
      })}
    </div>
  );
}
