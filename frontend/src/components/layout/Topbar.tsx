import { useEffect, useRef, useState, type FormEvent } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { LogOut, Menu } from 'lucide-react';
import { SearchInput } from '@/components/ui/SearchInput';
import { IconButton } from '@/components/ui/IconButton';
import { useAuth } from '@/context/AuthContext';

interface TopbarProps {
  onOpenMenu: () => void;
}

export function Topbar({ onOpenMenu }: TopbarProps) {
  const [query, setQuery] = useState('');
  const [sessionMenuOpen, setSessionMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const { user, signOut } = useAuth();

  const username = user?.username ?? 'USUARIO';
  const roleLabel = user?.roleLabel ?? 'Sin rol';

  useEffect(() => {
    setSessionMenuOpen(false);
  }, [location.pathname]);

  useEffect(() => {
    if (!sessionMenuOpen) return;

    const onPointerDown = (event: MouseEvent) => {
      if (!menuRef.current?.contains(event.target as Node)) {
        setSessionMenuOpen(false);
      }
    };
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setSessionMenuOpen(false);
    };

    document.addEventListener('mousedown', onPointerDown);
    document.addEventListener('keydown', onKeyDown);
    return () => {
      document.removeEventListener('mousedown', onPointerDown);
      document.removeEventListener('keydown', onKeyDown);
    };
  }, [sessionMenuOpen]);

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const search = query.trim();
    navigate({
      pathname: '/inventario',
      search: search ? `?q=${encodeURIComponent(search)}` : '',
    });
  };

  const handleSignOut = () => {
    setSessionMenuOpen(false);
    signOut();
    navigate('/login', { replace: true });
  };

  return (
    <header className="flex items-center gap-3 px-5 pt-5 md:px-10 md:pt-[35px]">
      <IconButton
        label="Abrir menú de navegación"
        icon={<Menu className="h-5 w-5" aria-hidden />}
        onClick={onOpenMenu}
        className="lg:hidden"
      />

      <form onSubmit={handleSubmit} className="min-w-0 max-w-[838px] flex-1" role="search">
        <SearchInput
          value={query}
          onChange={setQuery}
          placeholder="Búsqueda rápida"
          ariaLabel="Búsqueda rápida de recursos"
          className="h-10 w-full md:h-[34px]"
          inputClassName="text-sm"
        />
      </form>

      <div className="ml-auto flex shrink-0 items-center gap-4">
        <span className="hidden h-[23px] items-center rounded-full bg-board px-2.5 font-display text-xs font-semibold text-white md:inline-flex">
          {roleLabel}
        </span>
        <span className="hidden h-6 w-px bg-line md:block" aria-hidden />
        <div className="relative" ref={menuRef}>
          <button
            type="button"
            onClick={() => setSessionMenuOpen((current) => !current)}
            aria-haspopup="menu"
            aria-expanded={sessionMenuOpen}
            aria-label="Abrir menú de usuario"
            className="flex items-center gap-2.5 rounded-xl px-1.5 py-1 transition-colors hover:bg-soft"
          >
            <div className="hidden flex-col items-end gap-0.5 sm:flex">
              <span className="max-w-[180px] truncate text-[13px] font-semibold leading-tight text-ink">
                {username}
              </span>
              <span className="text-[11px] leading-tight text-muted">{roleLabel}</span>
            </div>
            <img
              src="/assets/avatar-usuario.svg"
              alt={`Foto de perfil de ${username}`}
              className="h-9 w-9 rounded-full object-cover ring-2 ring-white"
              width={36}
              height={36}
            />
          </button>

          {sessionMenuOpen ? (
            <div
              role="menu"
              className="absolute right-0 top-full z-50 mt-2 w-60 rounded-xl border border-line bg-white p-2 shadow-modal animate-scale-in"
            >
              <div className="px-3 py-2.5">
                <p className="truncate text-sm font-semibold text-ink">{username}</p>
                <p className="mt-0.5 text-xs text-muted">{roleLabel}</p>
              </div>
              <div className="my-1 h-px bg-line" aria-hidden />
              <button
                type="button"
                role="menuitem"
                onClick={handleSignOut}
                className="flex w-full items-center gap-2.5 rounded-lg px-3 py-2.5 text-left text-sm font-medium text-danger transition-colors hover:bg-red-50"
              >
                <LogOut className="h-4 w-4" aria-hidden />
                Cerrar sesión
              </button>
            </div>
          ) : null}
        </div>
      </div>
    </header>
  );
}
