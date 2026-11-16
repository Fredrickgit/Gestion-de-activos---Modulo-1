import { NavLink, useLocation } from 'react-router-dom';
import { X } from 'lucide-react';
import { NAV_ITEMS } from '@/constants/navigation';
import { cn } from '@/lib/cn';

interface SidebarProps {
  onNavigate?: () => void;
  onClose?: () => void;
}

export function Sidebar({ onNavigate, onClose }: SidebarProps) {
  const { pathname } = useLocation();

  return (
    <aside className="relative flex h-full w-[253px] shrink-0 flex-col bg-app">
      {onClose ? (
        <button
          type="button"
          onClick={onClose}
          aria-label="Cerrar menú de navegación"
          className="absolute right-3 top-4 z-10 rounded-lg p-2 text-muted transition-colors hover:bg-white/70 hover:text-ink"
        >
          <X className="h-5 w-5" aria-hidden />
        </button>
      ) : null}

      <div className="flex items-center gap-4 px-4 pt-7 sm:pl-4">
        <img
          src="/assets/logo-unimagdalena.png"
          alt="Escudo de la Universidad del Magdalena"
          className="h-[68px] w-[73px] shrink-0 object-contain"
          width={73}
          height={71}
        />
        <div className="min-w-0 pr-2">
          <p className="font-display text-[13px] font-semibold uppercase leading-[1.35] tracking-[0.01em] text-brand-dark">
            Universidad
            <br />
            del Magdalena
          </p>
          <p className="mt-1 font-display text-xs italic leading-tight text-ink/90">
            Gestión de activos
          </p>
        </div>
      </div>

      <div className="mx-8 mt-[62px] h-px bg-board/80" aria-hidden />

      <nav aria-label="Navegación principal" className="mt-[58px] px-2.5">
        <ul className="flex flex-col gap-2">
          {NAV_ITEMS.map((item) => {
            const active = pathname.startsWith(item.match);
            const Icon = item.icon;
            return (
              <li key={item.to}>
                <NavLink
                  to={item.to}
                  onClick={onNavigate}
                  aria-current={active ? 'page' : undefined}
                  className={cn(
                    'flex h-[42px] items-center gap-3 rounded-lg px-4 text-sm transition-colors duration-150',
                    active
                      ? 'bg-active-soft font-display font-semibold text-brand-dark'
                      : 'font-display font-medium text-ink hover:bg-white/55',
                  )}
                >
                  <Icon
                    className={cn(
                      'h-[18px] w-[18px] shrink-0',
                      active ? 'text-brand-dark' : 'text-muted',
                    )}
                    strokeWidth={active ? 2.1 : 1.9}
                    aria-hidden
                  />
                  <span>{item.label}</span>
                </NavLink>
              </li>
            );
          })}
        </ul>
      </nav>

      <div className="mt-auto">
        <div className="mx-8 mb-7 h-px bg-board/80" aria-hidden />
        <div className="px-8 pb-9">
          <p className="text-[11px] font-medium leading-tight text-muted">
            Módulo de Gestión v1.0
          </p>
          <p className="mt-1 text-[10px] leading-tight text-muted">
            © 2026 Universidad del Magdalena
          </p>
        </div>
      </div>
    </aside>
  );
}
