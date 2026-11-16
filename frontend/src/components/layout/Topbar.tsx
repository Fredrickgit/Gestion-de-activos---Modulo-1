import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu } from 'lucide-react';
import { SearchInput } from '@/components/ui/SearchInput';
import { IconButton } from '@/components/ui/IconButton';

interface TopbarProps {
  onOpenMenu: () => void;
}

export function Topbar({ onOpenMenu }: TopbarProps) {
  const [query, setQuery] = useState('');
  const navigate = useNavigate();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const search = query.trim();
    navigate({
      pathname: '/inventario',
      search: search ? `?q=${encodeURIComponent(search)}` : '',
    });
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
          Rol de ejemplo
        </span>
        <span className="hidden h-6 w-px bg-line md:block" aria-hidden />
        <div className="flex items-center gap-2.5">
          <div className="hidden flex-col items-end gap-0.5 sm:flex">
            <span className="text-[13px] font-semibold leading-tight text-ink">
              Romario Perez
            </span>
            <span className="text-[11px] leading-tight text-muted">Rol de ejemplo</span>
          </div>
          <img
            src="/assets/avatar-user.png"
            alt="Foto de perfil de Romario Perez"
            className="h-9 w-9 rounded-full object-cover ring-2 ring-white"
            width={36}
            height={36}
          />
        </div>
      </div>
    </header>
  );
}
