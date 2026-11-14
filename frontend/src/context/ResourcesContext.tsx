import {
  createContext,
  useCallback,
  useContext,
  useMemo,
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

interface ResourcesContextValue {
  resources: Resource[];
  getResource: (id: string) => Resource | undefined;
  addResource: (input: ResourceInput) => Resource;
  updateResource: (id: string, patch: Partial<ResourceInput>) => void;
  changeStatus: (id: string, status: ResourceStatus) => void;
  deleteResource: (id: string) => void;
}

const ResourcesContext = createContext<ResourcesContextValue | null>(null);

let historyCounter = 0;

function createHistoryEntry(
  action: string,
  detail: string,
  date = todayISO(),
): ResourceHistoryEntry {
  historyCounter += 1;
  return {
    id: `history-${historyCounter}`,
    date,
    action,
    detail,
    actor: 'Romario Pérez',
  };
}

export function ResourcesProvider({ children }: { children: ReactNode }) {
  const [resources, setResources] = useState<Resource[]>(seedResources);

  const getResource = useCallback(
    (id: string) => resources.find((resource) => resource.id === id),
    [resources],
  );

  const addResource = useCallback((input: ResourceInput) => {
    const resource: Resource = {
      ...input,
      registeredAt: input.registeredAt ?? todayISO(),
      history: [
        createHistoryEntry(
          input.kind === 'activo' ? 'Registro del recurso' : 'Registro del espacio',
          `Creado en el inventario con ${input.kind === 'activo' ? 'placa' : 'código'} única ${input.id}.`,
        ),
      ],
    };
    setResources((current) => [resource, ...current]);
    return resource;
  }, []);

  const updateResource = useCallback(
    (id: string, patch: Partial<ResourceInput>) => {
      setResources((current) =>
        current.map((resource) => {
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
              createHistoryEntry('Edición del recurso', detail),
            ],
          };
        }),
      );
    },
    [],
  );

  const changeStatus = useCallback((id: string, status: ResourceStatus) => {
    setResources((current) =>
      current.map((resource) => {
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
            ),
          ],
        };
      }),
    );
  }, []);

  const deleteResource = useCallback((id: string) => {
    setResources((current) => current.filter((resource) => resource.id !== id));
  }, []);

  const value = useMemo(
    () => ({
      resources,
      getResource,
      addResource,
      updateResource,
      changeStatus,
      deleteResource,
    }),
    [
      resources,
      getResource,
      addResource,
      updateResource,
      changeStatus,
      deleteResource,
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
