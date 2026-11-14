export type ResourceKind = 'activo' | 'espacio';

export type ResourceStatus =
  | 'disponible'
  | 'reservado'
  | 'en-uso'
  | 'mantenimiento'
  | 'bloqueo';

export interface ResourceHistoryEntry {
  id: string;
  date: string;
  action: string;
  detail: string;
  actor: string;
}

export interface Resource {
  id: string;
  name: string;
  kind: ResourceKind;
  category: string;
  location: string;
  faculty: string;
  condition: string;
  maxTerm: string;
  status: ResourceStatus;
  capacity?: number;
  responsible: string;
  registeredAt: string;
  image?: string;
  history: ResourceHistoryEntry[];
}

export type ResourceInput = Omit<Resource, 'history' | 'registeredAt'> & {
  registeredAt?: string;
};

export interface ReportFilters {
  from: string;
  to: string;
  kind: 'todos' | ResourceKind;
  format: ReportExportFormat;
}

export type ReportExportFormat = 'pdf' | 'excel' | 'csv';

export interface ReportRow {
  id: string;
  name: string;
  type: string;
  location: string;
  status: ResourceStatus;
  capacity: string;
  date: string;
  kind: ResourceKind;
}

export type ReportSortKey =
  | 'id'
  | 'name'
  | 'type'
  | 'location'
  | 'status'
  | 'capacity'
  | 'date';

export interface ToastMessage {
  id: string;
  title: string;
  description?: string;
  variant: 'success' | 'error' | 'info';
}
