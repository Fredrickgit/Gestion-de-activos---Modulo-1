import { apiRequest } from '@/api/http';
import type {
  BackendAvailabilityResponse,
  BackendCatalogs,
  BackendHistoryEntry,
  BackendPage,
  BackendResource,
  BackendStatusUpdateResponse,
  CreateActivoPayload,
  CreateEspacioPayload,
  UpdateResourcePayload,
} from '@/api/dto';
import { env } from '@/config/env';

const PAGE_SIZE = 20;

export async function listResources(): Promise<BackendResource[]> {
  const first = await apiRequest<BackendPage<BackendResource>>('recursos', {
    query: { page: 0, size: PAGE_SIZE },
  });

  const pages = Math.ceil(first.totalElements / PAGE_SIZE);
  if (pages <= 1) return first.content;

  const rest = await Promise.all(
    Array.from({ length: pages - 1 }, (_, index) =>
      apiRequest<BackendPage<BackendResource>>('recursos', {
        query: { page: index + 1, size: PAGE_SIZE },
      }),
    ),
  );

  return [first, ...rest].flatMap((page) => page.content);
}

export async function getResource(id: string): Promise<BackendResource> {
  return apiRequest<BackendResource>(`recursos/${id}`);
}

export async function getAvailability(id: string): Promise<BackendAvailabilityResponse> {
  return apiRequest<BackendAvailabilityResponse>(`recursos/${id}/disponibilidad`);
}

export async function getHistory(id: string): Promise<BackendHistoryEntry[]> {
  return apiRequest<BackendHistoryEntry[]>(`recursos/${id}/historial`);
}

export async function getCatalogs(): Promise<BackendCatalogs> {
  return apiRequest<BackendCatalogs>('catalogos');
}

export async function createActivo(payload: CreateActivoPayload): Promise<BackendResource> {
  return apiRequest<BackendResource>('activos', { method: 'POST', body: payload });
}

export async function createEspacio(payload: CreateEspacioPayload): Promise<BackendResource> {
  return apiRequest<BackendResource>('espacios', { method: 'POST', body: payload });
}

export async function updateResource(
  id: string,
  payload: UpdateResourcePayload,
): Promise<BackendResource> {
  return apiRequest<BackendResource>(`recursos/${id}`, {
    method: 'PATCH',
    body: payload,
  });
}

export async function updateStatus(
  id: string,
  status: string,
  usuarioResponsable: string,
  motivo?: string,
): Promise<BackendStatusUpdateResponse> {
  return apiRequest<BackendStatusUpdateResponse>(`recursos/${id}/estado`, {
    method: 'PATCH',
    body: { nuevoEstado: status, usuarioResponsable, motivo },
  });
}

export async function deleteResource(id: string): Promise<void> {
  await apiRequest<void>(`recursos/${id}`, { method: 'DELETE' });
}

export const apiConfig = env;
