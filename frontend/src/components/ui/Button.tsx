import type { ButtonHTMLAttributes, ReactNode } from 'react';
import { cn } from '@/lib/cn';

type ButtonVariant = 'primary' | 'navy' | 'outline' | 'ghost' | 'danger' | 'soft';
type ButtonSize = 'sm' | 'md' | 'lg';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  size?: ButtonSize;
  fullWidth?: boolean;
  icon?: ReactNode;
  iconRight?: ReactNode;
}

const VARIANTS: Record<ButtonVariant, string> = {
  primary:
    'bg-brand text-white shadow-[0_1px_2px_rgb(15_23_42_/_0.12)] hover:bg-brand/90 active:bg-brand',
  navy: 'bg-brand-dark text-white shadow-[0_1px_2px_rgb(15_23_42_/_0.12)] hover:bg-[#172e6d]',
  outline:
    'border border-line bg-white text-ink hover:border-slate-300 hover:bg-soft active:bg-slate-100',
  ghost: 'bg-transparent text-muted hover:bg-soft hover:text-ink',
  danger: 'border border-danger/70 bg-white text-danger hover:bg-red-50 active:bg-red-100',
  soft: 'bg-active-soft text-brand-dark hover:bg-blue-100 active:bg-blue-200/60',
};

const SIZES: Record<ButtonSize, string> = {
  sm: 'h-8 gap-1.5 rounded-lg px-3 text-xs',
  md: 'h-11 gap-2 rounded-[10px] px-4 text-sm',
  lg: 'h-[58px] gap-3 rounded-lg px-6 text-lg',
};

export function Button({
  variant = 'primary',
  size = 'md',
  fullWidth = false,
  icon,
  iconRight,
  className,
  type = 'button',
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      type={type}
      className={cn(
        'inline-flex select-none items-center justify-center font-display font-semibold transition-colors duration-150 disabled:pointer-events-none disabled:opacity-50',
        VARIANTS[variant],
        SIZES[size],
        fullWidth && 'w-full',
        className,
      )}
      {...props}
    >
      {icon}
      {children}
      {iconRight}
    </button>
  );
}
