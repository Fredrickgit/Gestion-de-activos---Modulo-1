import type { ResourceStatus } from '@/types';

export interface StatusMeta {
  label: string;
  shortLabel: string;
  badge: string;
  bar: string;
  text: string;
  soft: string;
  border: string;
}

export const STATUS_META: Record<ResourceStatus, StatusMeta> = {
  disponible: {
    label: 'DISPONIBLE',
    shortLabel: 'Disponible',
    badge: '#00a50b',
    bar: '#00a50b',
    text: '#ffffff',
    soft: '#e8f8e8',
    border: '#b7e5b7',
  },
  'en-uso': {
    label: 'EN USO',
    shortLabel: 'En uso',
    badge: '#d2ae00',
    bar: '#d2ae00',
    text: '#ffffff',
    soft: '#fdf8dc',
    border: '#efe19a',
  },
  mantenimiento: {
    label: 'MANTENIMIENTO',
    shortLabel: 'En mantenimiento',
    badge: '#384152',
    bar: '#384152',
    text: '#ffffff',
    soft: '#eceef1',
    border: '#cbd2da',
  },
};

export const RESOURCE_STATUSES: ResourceStatus[] = [
  'disponible',
  'en-uso',
  'mantenimiento',
];

export const STATUS_OPTIONS = RESOURCE_STATUSES.map((value) => ({
  value,
  label: STATUS_META[value].shortLabel,
}));

export const SUMMARY_STATUSES: Array<{
  status: ResourceStatus | 'todos';
  label: string;
  count: number;
}> = [
  { status: 'disponible', label: 'Disponibles', count: 230 },
  { status: 'mantenimiento', label: 'En mantenimiento', count: 12 },
  { status: 'en-uso', label: 'En uso', count: 7 },
];

export const CONDITION_OPTIONS = [
  'Bueno',
  'Muy bueno',
  'Medianamente usado',
  'Requiere revisión',
  'Dado de baja',
];
