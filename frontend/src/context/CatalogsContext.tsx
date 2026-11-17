import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import type { Catalogs } from '@/types';
import {
  ASSET_CATEGORIES,
  FACULTIES,
  LOCATION_SUGGESTIONS,
  SPACE_CATEGORIES,
} from '@/data/catalog';
import { env } from '@/config/env';
import { getCatalogs } from '@/api/resources';
import { mapCatalogs } from '@/api/mappers';

interface CatalogsContextValue {
  catalogs: Catalogs;
  loading: boolean;
  error: string | null;
  reload: () => Promise<void>;
}

const EMPTY_CATALOGS: Catalogs = {
  facultades: [],
  ubicaciones: [],
  tiposActivo: [],
  estadosDisponibilidad: [],
  estadosFisicos: [],
};

const DEMO_CATALOGS: Catalogs = {
  facultades: FACULTIES.map((name) => ({ id: name, nombre: name })),
  ubicaciones: LOCATION_SUGGESTIONS.map((name) => ({ id: name, nombre: name })),
  tiposActivo: [...ASSET_CATEGORIES, ...SPACE_CATEGORIES].map((name) => ({
    id: name,
    nombre: name,
    plazoDevolucion: 1,
  })),
  estadosDisponibilidad: [],
  estadosFisicos: [],
};

const CatalogsContext = createContext<CatalogsContextValue | null>(null);

export function CatalogsProvider({ children }: { children: ReactNode }) {
  const [catalogs, setCatalogs] = useState<Catalogs>(
    env.demoMode ? DEMO_CATALOGS : EMPTY_CATALOGS,
  );
  const [loading, setLoading] = useState(!env.demoMode);
  const [error, setError] = useState<string | null>(null);

  const reload = useCallback(async () => {
    if (env.demoMode) return;
    setLoading(true);
    setError(null);
    try {
      setCatalogs(mapCatalogs(await getCatalogs()));
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'No fue posible cargar los catálogos.',
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (!env.demoMode) void reload();
  }, [reload]);

  const value = useMemo(
    () => ({ catalogs, loading, error, reload }),
    [catalogs, loading, error, reload],
  );

  return (
    <CatalogsContext.Provider value={value}>{children}</CatalogsContext.Provider>
  );
}

export function useCatalogs(): CatalogsContextValue {
  const context = useContext(CatalogsContext);
  if (!context) {
    throw new Error('useCatalogs debe usarse dentro de CatalogsProvider');
  }
  return context;
}
