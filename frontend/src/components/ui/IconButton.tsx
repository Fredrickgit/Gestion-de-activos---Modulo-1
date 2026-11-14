import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';

interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  label: string;
  icon: ReactNode;
  size?: 'sm' | 'md';
  tone?: 'default' | 'danger';
}

export function IconButton({
  label,
  icon,
  size = 'md',
  tone = 'default',
  className,
  type = 'button',
  ...props
}: IconButtonProps) {
  return (
    <button
      type={type}
      aria-label={label}
      title={label}
      className={cn(
        'inline-flex shrink-0 items-center justify-center rounded-lg border border-line bg-white text-muted transition-colors duration-150 hover:bg-soft hover:text-ink disabled:pointer-events-none disabled:opacity-40',
        size === 'sm' ? 'h-7 w-7' : 'h-9 w-9',
        tone === 'danger' && 'text-danger hover:bg-red-50 hover:text-danger',
        className,
      )}
      {...props}
    >
      {icon}
    </button>
  );
}
