import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';

interface PageHeaderProps {
  title: string;
  actions?: ReactNode;
  size?: 'md' | 'lg';
  className?: string;
}

export function PageHeader({ title, actions, size = 'lg', className }: PageHeaderProps) {
  return (
    <div
      className={cn(
        'flex min-h-[56px] flex-wrap items-center justify-between gap-4 px-5 md:min-h-[73px] md:px-10',
        className,
      )}
    >
      <h1
        className={cn(
          'font-display font-semibold leading-[1.12] tracking-[-0.015em] text-black',
          size === 'lg' ? 'text-[28px] sm:text-[32px] lg:text-[40px]' : 'text-[26px] sm:text-[30px] lg:text-[32px]',
        )}
      >
        {title}
      </h1>
      {actions ? <div className="flex items-center gap-3">{actions}</div> : null}
    </div>
  );
}
