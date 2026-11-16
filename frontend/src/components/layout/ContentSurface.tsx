import type { ReactNode } from 'react';
import { cn } from '@/lib/cn';

interface ContentSurfaceProps {
  children: ReactNode;
  className?: string;
}

export function ContentSurface({ children, className }: ContentSurfaceProps) {
  return (
    <section
      className={cn(
        'rounded-[24px] bg-board p-4 ring-1 ring-white/10 md:rounded-[32px] md:p-6',
        className,
      )}
    >
      {children}
    </section>
  );
}
