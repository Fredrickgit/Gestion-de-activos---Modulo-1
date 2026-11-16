import type { ReactNode } from 'react';
import type { ResourceStatus } from '@/types';
import { STATUS_META } from '@/constants/status';
import { cn } from '@/lib/cn';

interface StatusBadgeProps {
  status: ResourceStatus;
  size?: 'sm' | 'md';
  uppercase?: boolean;
  className?: string;
}

export function StatusBadge({
  status,
  size = 'sm',
  uppercase = false,
  className,
}: StatusBadgeProps) {
  const meta = STATUS_META[status];

  return (
    <span
      className={cn(
        'inline-flex w-fit items-center rounded-full font-display font-semibold leading-none whitespace-nowrap',
        size === 'sm' ? 'h-[23px] px-2.5 text-xs' : 'h-7 px-3 text-[13px]',
        uppercase && 'uppercase',
        className,
      )}
      style={{ backgroundColor: meta.badge, color: meta.text }}
    >
      {uppercase ? meta.label : meta.shortLabel}
    </span>
  );
}

interface SoftTagProps {
  children: ReactNode;
  className?: string;
}

export function SoftTag({ children, className }: SoftTagProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full border border-line bg-white/90 px-2.5 py-1.5 font-display text-xs font-semibold text-ink backdrop-blur-sm',
        className,
      )}
    >
      {children}
    </span>
  );
}
