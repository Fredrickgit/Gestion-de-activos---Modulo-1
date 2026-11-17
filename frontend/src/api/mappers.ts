import type {
  BackendAvailability,
  BackendCatalogs,
  BackendHistoryEntry,
  BackendPhysicalState,
  BackendResource,
  BackendResourceType,
  CreateActivoPayload,
  CreateEspacioPayload,
  UpdateResourcePayload,
} from '@/api/dto';
import type {
  Catalogs,
  Resource,
  ResourceHistoryEntry,
  ResourceInput,
  ResourceStatus,
} from '@/types';
import { STATUS_META } from '@/constants/status';

const STATUS_FROM_BACKEND: Record<BackendAvailability, ResourceStatus> = {
  DISPONIBLE: 'disponible',
  RESERVADO: 'reservado',
  EN_USO: 'en-uso',
  EN_MANTENIMIENTO: 'mantenimiento',
  BLOQUEO_ACADEMICO: 'bloqueo',
};

const STATUS_TO_BACKEND: Record<ResourceStatus, BackendAvailability> = {
  disponible: 'DISPONIBLE',
  reservado: 'RESERVADO',
  'en-uso': 'EN_USO',
  mantenimiento: 'EN_MANTENIMIENTO',
  bloqueo: 'BLOQUEO_ACADEMICO',
};

const CONDITION_FROM_BACKEND: Record<BackendPhysicalState, string> = {
  NUEVO: 'Bueno',
  OPTIMO: 'Muy bueno',
  MAL_ESTADO: 'Requiere revisión',
  AVERIADO: 'Dado de baja',
};

export function statusFromBackend(value: string | null | undefined): ResourceStatus {
  return STATUS_FROM_BACKEND[value as BackendAvailability] ?? 'disponible';
}

export function statusToBackend(value: ResourceStatus): BackendAvailability {
  return STATUS_TO_BACKEND[value];
}

export function physicalStateToBackend(value: string): BackendPhysicalState {
  switch (value) {
    case 'Nuevo':
      return 'NUEVO';
    case 'Muy bueno':
    case 'Bueno':
      return 'OPTIMO';
    case 'Dado de baja':
      return 'AVERIADO';
    default:
      return 'MAL_ESTADO';
  }
}

export function plazoToLabel(days: number | null | undefined): string {
  const value = days ?? 0;
  return `${value} ${value === 1 ? 'día' : 'días'}`;
}

export function labelToPlazo(label: string): number {
  const match = label.match(/\d+/);
  return match ? Number(match[0]) : 0;
}

export function mapResource(dto: BackendResource): Resource {
  const kind = dto.tipo === 'ESPACIO' ? 'espacio' : 'activo';
  return {
    id: dto.codigo,
    uuid: dto.id,
    name: dto.nombre,
    kind,
    category:
      dto.tipoActivo ??
      (dto.tipo === ('ACTIVO' as BackendResourceType)
        ? 'Activo físico'
        : 'Espacio académico'),
    location: dto.ubicacion ?? 'Sin ubicación',
    faculty: dto.facultad ?? 'General',
    condition: dto.estadoFisico
      ? CONDITION_FROM_BACKEND[dto.estadoFisico]
      : 'Bueno',
    maxTerm: plazoToLabel(dto.plazoDevolucion),
    status: statusFromBackend(dto.estado),
    capacity: dto.aforoMaximo ?? undefined,
    responsible: 'Sin asignar',
    registeredAt: dto.fechaRegistro ? dto.fechaRegistro.slice(0, 10) : '',
    history: [],
  };
}

export function mapHistory(entry: BackendHistoryEntry): ResourceHistoryEntry {
  const previous = STATUS_META[statusFromBackend(entry.estadoAnterior)].shortLabel;
  const next = STATUS_META[statusFromBackend(entry.estadoNuevo)].shortLabel;
  return {
    id: entry.id,
    date: entry.fechaEvento,
    action: `Cambio de estado a ${next}`,
    detail:
      entry.motivo ??
      `Estado anterior: ${previous} · Nuevo: ${next}.`,
    actor: entry.usuarioResponsable,
  };
}

export function mapCatalogs(dto: BackendCatalogs): Catalogs {
  return {
    facultades: dto.facultades.map((item) => ({
      id: item.id,
      nombre: item.nombre,
      descripcion: item.descripcion ?? undefined,
    })),
    ubicaciones: dto.ubicaciones.map((item) => ({
      id: item.id,
      nombre: item.nombre,
      descripcion: item.descripcion ?? undefined,
    })),
    tiposActivo: dto.tiposActivo.map((item) => ({
      id: item.id,
      nombre: item.nombre,
      descripcion: item.descripcion ?? undefined,
      plazoDevolucion: item.plazoDevolucion,
    })),
    estadosDisponibilidad: dto.estadosDisponibilidad.map((item) => ({
      id: item.id,
      nombre: item.nombre,
      descripcion: item.descripcion ?? undefined,
    })),
    estadosFisicos: dto.estadosFisicos.map((item) => ({
      id: item.id,
      nombre: item.nombre,
      descripcion: item.descripcion ?? undefined,
    })),
  };
}

export function toCreateActivoPayload(
  input: ResourceInput,
  referencia: { facultadId?: string; ubicacionId?: string; tipoActivoId?: string },
): CreateActivoPayload {
  if (!referencia.ubicacionId) {
    throw new Error('Selecciona una ubicación válida.');
  }
  if (!referencia.tipoActivoId) {
    throw new Error('Selecciona un tipo de activo válido.');
  }

  return {
    nombre: input.name,
    codigo: input.id,
    estadoFisico: physicalStateToBackend(input.condition),
    ubicacionId: referencia.ubicacionId,
    facultadId: referencia.facultadId,
    tipoActivoId: referencia.tipoActivoId,
    plazoDevolucion: input.plazoDevolucion ?? labelToPlazo(input.maxTerm),
  };
}

export function toCreateEspacioPayload(
  input: ResourceInput,
  referencia: {
    facultadId?: string;
    ubicacionId?: string;
    equipamiento?: string[];
  },
): CreateEspacioPayload {
  if (!referencia.facultadId) {
    throw new Error('Selecciona una facultad válida.');
  }
  if (!referencia.ubicacionId) {
    throw new Error('Selecciona una ubicación válida.');
  }
  return {
    nombre: input.name,
    codigo: input.id,
    aforoMaximo: input.capacity ?? 0,
    facultadId: referencia.facultadId,
    ubicacionId: referencia.ubicacionId,
    equipamiento: referencia.equipamiento ?? [],
    plazoDevolucion: input.plazoDevolucion ?? labelToPlazo(input.maxTerm),
  };
}

export function toUpdatePayload(
  patch: Partial<ResourceInput>,
  referencia: {
    facultadId?: string;
    ubicacionId?: string;
    tipoActivoId?: string;
    equipamiento?: string[];
  },
): UpdateResourcePayload {
  const payload: UpdateResourcePayload = {};
  if (patch.name !== undefined) payload.nombre = patch.name;
  if (patch.condition !== undefined) {
    payload.estadoFisico = physicalStateToBackend(patch.condition);
  }
  if (patch.maxTerm !== undefined) payload.plazoDevolucion = labelToPlazo(patch.maxTerm);
  if (patch.capacity !== undefined) payload.aforoMaximo = patch.capacity;
  if (referencia.ubicacionId) payload.ubicacionId = referencia.ubicacionId;
  if (referencia.facultadId) payload.facultadId = referencia.facultadId;
  if (referencia.tipoActivoId) payload.tipoActivoId = referencia.tipoActivoId;
  if (referencia.equipamiento && referencia.equipamiento.length > 0) {
    payload.equipamiento = referencia.equipamiento;
  }
  return payload;
}
