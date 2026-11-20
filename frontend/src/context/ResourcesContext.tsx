import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
  type ReactNode,
} from 'react';
import type {
  Resource,
  ResourceHistoryEntry,
  ResourceInput,
  ResourceStatus,
} from '@/types';
import { seedResources } from '@/data/resources';
import { STATUS_META } from '@/constants/status';
import { formatDisplayDate, todayISO } from '@/lib/format';
import { env } from '@/config/env';
import { useAuth } from '@/context/AuthContext';
import {
  createActivo,
  createEspacio,
  deleteResource as deleteResourceRequest,
  getHistory,
  listResources,
  updateResource as updateResourceRequest,
  updateStatus,
} from '@/api/resources';
import {
  mapHistory,
  mapResource,
  statusToBackend,
  toCreateActivoPayload,
  toCreateEspacioPayload,
  toUpdatePayload,
} from '@/api/mappers';

interface ResourcesContextValue {
  resources: Resource[];
  loading: boolean;
  error: string | null;
  isDemo: boolean;
  refresh: () => Promise<void>;
  getResource: (id: string) => Resource | undefined;
  addResource: (input: ResourceInput) => Promise<Resource>;
  updateResource: (id: string, patch: Partial<ResourceInput>) => Promise<void>;
  changeStatus: (id: string, status: ResourceStatus, motivo?: string) => Promise<void>;
  deleteResource: (id: string) => Promise<void>;
  loadHistory: (id: string) => Promise<ResourceHistoryEntry[]>;
}

const ResourcesContext = createContext<ResourcesContextValue | null>(null);

let historyCounter = 0;

function createHistoryEntry(
  action: string,
  detail: string,
  actor: string,
  date = todayISO(),
): ResourceHistoryEntry {
  historyCounter += 1;
  return {
    id: `history-${historyCounter}`,
    date,
    action,
    detail,
    actor,
  };
}

function findUuid(resource: Resource | undefined): string {
  if (!resource?.uuid) {
    throw new Error('El recurso no tiene un identificador UUID del backend.');
  }
  return resource.uuid;
}

export function ResourcesProvider({ children }: { children: ReactNode }) {
  const { user } = useAuth();
  const actor = user?.username || env.apiUsername || 'USUARIO';
  const [resources, setResources] = useState<Resource[]>(
    env.demoMode ? seedResources : [],
  );
  const [loading, setLoading] = useState(!env.demoMode && Boolean(user));
  const [error, setError] = useState<string | null>(null);
  const mounted = useRef(true);

  useEffect(() => {
    mounted.current = true;
    return () => {
      mounted.current = false;
    };
  }, []);

  const refresh = useCallback(async () => {
    if (env.demoMode) return;
    setLoading(true);
    setError(null);
    try {
      const data = await listResources();
      if (!mounted.current) return;
      setResources(data.map(mapResource));
    } catch (cause) {
      if (!mounted.current) return;
      setError(
        cause instanceof Error
          ? cause.message
          : 'No fue posible cargar el inventario.',
      );
    } finally {
      if (mounted.current) setLoading(false);
    }
  }, []);

  useEffect(() => {
    if (env.demoMode) return;
    if (!user) {
      setResources([]);
      setError(null);
      setLoading(false);
      return;
    }
    void refresh();
  }, [refresh, user]);

  const getResource = useCallback(
    (id: string) =>
      resources.find((resource) => resource.id === id || resource.uuid === id),
    [resources],
  );

  const addResource = useCallback(async (input: ResourceInput) => {
    if (env.demoMode) {
      const resource: Resource = {
        ...input,
        registeredAt: input.registeredAt ?? todayISO(),
        history: [
          createHistoryEntry(
            input.kind === 'activo' ? 'Registro del recurso' : 'Registro del espacio',
            `Creado en el inventario con ${input.kind === 'activo' ? 'placa' : 'código'} única ${input.id}.`,
            actor,
          ),
        ],
      };
      setResources((current) => [resource, ...current]);
      return resource;
    }

    const created =
      input.kind === 'activo'
        ? await createActivo(
            toCreateActivoPayload(input, {
              facultadId: input.facultadId,
              ubicacionId: input.ubicacionId,
              tipoActivoId: input.tipoActivoId,
            }),
          )
        : await createEspacio(
            toCreateEspacioPayload(input, {
              facultadId: input.facultadId,
              ubicacionId: input.ubicacionId,
              equipamiento: input.equipamiento,
            }),
          );

    const mapped = mapResource({
      ...created,
      fechaRegistro: new Date().toISOString(),
      tipoActivo: input.category,
    });
    setResources((current) => [mapped, ...current]);
    void refresh();
    return mapped;
  }, [refresh, actor]);

  const updateResource = useCallback(
    async (id: string, patch: Partial<ResourceInput>) => {
      const current = resources.find(
        (resource) => resource.id === id || resource.uuid === id,
      );

      if (env.demoMode) {
        setResources((list) =>
          list.map((resource) => {
            if (resource.id !== id) return resource;
            const changed = Object.entries(patch).filter(([key, value]) => {
              if (key === 'history' || key === 'registeredAt') return false;
              return resource[key as keyof Resource] !== value;
            });
            if (changed.length === 0) return resource;
            const detail =
              changed.length === 1
                ? `Se actualizó el campo ${changed[0][0]}.`
                : `Se actualizaron ${changed.length} campos de la ficha del recurso.`;
            return {
              ...resource,
              ...patch,
              history: [
                ...resource.history,
                createHistoryEntry('Edición del recurso', detail, actor),
              ],
            };
          }),
        );
        return;
      }

      const updated = await updateResourceRequest(
        findUuid(current),
        toUpdatePayload(patch, {
          facultadId: patch.facultadId,
          ubicacionId: patch.ubicacionId,
          tipoActivoId: patch.tipoActivoId,
          equipamiento: patch.equipamiento,
        }),
      );

      setResources((list) =>
        list.map((resource) =>
          resource.id === (current?.id ?? id)
            ? {
                ...resource,
                ...mapResource({
                  ...updated,
                  fechaRegistro:
                    updated.fechaRegistro ?? resource.registeredAt,
                  tipoActivo: updated.tipoActivo ?? resource.category,
                }),
                registeredAt: resource.registeredAt,
                history: resource.history,
              }
            : resource,
        ),
      );
      void refresh();
    },
    [resources, refresh, actor],
  );

  const changeStatus = useCallback(
    async (id: string, status: ResourceStatus, motivo?: string) => {
      const current = resources.find(
        (resource) => resource.id === id || resource.uuid === id,
      );

      if (env.demoMode) {
        setResources((list) =>
          list.map((resource) => {
            if (resource.id !== id) return resource;
            if (resource.status === status) return resource;
            return {
              ...resource,
              status,
              history: [
                ...resource.history,
                createHistoryEntry(
                  'Cambio de estado',
                  `Estado actualizado de ${STATUS_META[resource.status].shortLabel} a ${STATUS_META[status].shortLabel} el ${formatDisplayDate(todayISO())}.`,
                  actor,
                ),
              ],
            };
          }),
        );
        return;
      }

      await updateStatus(
        findUuid(current),
        statusToBackend(status),
        actor,
        motivo,
      );
      setResources((list) =>
        list.map((resource) =>
          resource.id === (current?.id ?? id) ? { ...resource, status } : resource,
        ),
      );
    },
    [resources, actor],
  );

  const deleteResource = useCallback(
    async (id: string) => {
      const current = resources.find(
        (resource) => resource.id === id || resource.uuid === id,
      );
      if (!env.demoMode) {
        await deleteResourceRequest(findUuid(current));
      }
      setResources((list) => list.filter((resource) => resource.id !== id));
    },
    [resources],
  );

  const loadHistory = useCallback(
    async (id: string) => {
      const current = resources.find(
        (resource) => resource.id === id || resource.uuid === id,
      );
      if (env.demoMode) return [...(current?.history ?? [])].reverse();
      const entries = await getHistory(findUuid(current));
      return entries.map(mapHistory);
    },
    [resources],
  );

  const value = useMemo(
    () => ({
      resources,
      loading,
      error,
      isDemo: env.demoMode,
      refresh,
      getResource,
      addResource,
      updateResource,
      changeStatus,
      deleteResource,
      loadHistory,
    }),
    [
      resources,
      loading,
      error,
      refresh,
      getResource,
      addResource,
      updateResource,
      changeStatus,
      deleteResource,
      loadHistory,
    ],
  );

  return (
    <ResourcesContext.Provider value={value}>{children}</ResourcesContext.Provider>
  );
}

export function useResources(): ResourcesContextValue {
  const context = useContext(ResourcesContext);
  if (!context) {
    throw new Error('useResources debe usarse dentro de ResourcesProvider');
  }
  return context;
}
