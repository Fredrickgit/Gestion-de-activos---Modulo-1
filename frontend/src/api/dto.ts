export type BackendResourceType = 'ACTIVO' | 'ESPACIO';

export type BackendAvailability =
  | 'DISPONIBLE'
  | 'RESERVADO'
  | 'EN_USO'
  | 'EN_MANTENIMIENTO'
  | 'BLOQUEO_ACADEMICO';

export type BackendPhysicalState = 'NUEVO' | 'OPTIMO' | 'MAL_ESTADO' | 'AVERIADO';

export interface BackendResource {
  id: string;
  codigo: string;
  nombre: string;
  tipo: BackendResourceType;
  estado: BackendAvailability;
  estadoFisico: BackendPhysicalState | null;
  facultad: string | null;
  ubicacion: string | null;
  aforoMaximo: number | null;
  plazoDevolucion: number | null;
  fechaRegistro: string | null;
  tipoActivo?: string | null;
}

export interface BackendPage<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
}

export interface BackendCatalogItem {
  id: string;
  nombre: string;
  descripcion?: string | null;
}

export interface BackendCatalogs {
  facultades: BackendCatalogItem[];
  ubicaciones: BackendCatalogItem[];
  tiposActivo: Array<BackendCatalogItem & { plazoDevolucion: number }>;
  estadosDisponibilidad: BackendCatalogItem[];
  estadosFisicos: BackendCatalogItem[];
}

export interface BackendHistoryEntry {
  id: string;
  recursoId: string;
  estadoAnterior: BackendAvailability;
  estadoNuevo: BackendAvailability;
  usuarioResponsable: string;
  fechaEvento: string;
  motivo: string | null;
}

export interface BackendAvailabilityResponse {
  id: string;
  estado: BackendAvailability;
  disponible: boolean;
  horarioAsignado: { inicio: string; fin: string } | null;
  motivoNoDisponibilidad: string | null;
}

export interface BackendStatusUpdateResponse {
  id: string;
  tipo: BackendResourceType;
  estadoAnterior: BackendAvailability;
  estadoNuevo: BackendAvailability;
  auditoria: {
    usuario: string;
    timestamp: string;
  };
}

export interface CreateActivoPayload {
  nombre: string;
  codigo: string;
  estadoFisico: BackendPhysicalState;
  ubicacionId: string;
  facultadId?: string;
  tipoActivoId: string;
  plazoDevolucion?: number;
}

export interface CreateEspacioPayload {
  nombre: string;
  codigo: string;
  aforoMaximo: number;
  facultadId: string;
  ubicacionId?: string;
  edificio?: string;
  equipamiento: string[];
  plazoDevolucion?: number;
}

export interface UpdateResourcePayload {
  nombre?: string;
  estadoFisico?: BackendPhysicalState;
  plazoDevolucion?: number;
  aforoMaximo?: number;
  ubicacionId?: string;
  facultadId?: string;
  tipoActivoId?: string;
  equipamiento?: string[];
}
