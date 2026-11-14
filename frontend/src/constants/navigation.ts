import { ChartColumn, FilePlus, ListChecks, type LucideIcon } from 'lucide-react';

export interface NavItem {
  label: string;
  to: string;
  icon: LucideIcon;
  match: string;
}

export const NAV_ITEMS: NavItem[] = [
  {
    label: 'Inventario y Estados',
    to: '/inventario',
    icon: ListChecks,
    match: '/inventario',
  },
  {
    label: 'Registrar recurso',
    to: '/registrar',
    icon: FilePlus,
    match: '/registrar',
  },
  {
    label: 'Reportes',
    to: '/reportes',
    icon: ChartColumn,
    match: '/reportes',
  },
];

export interface InventoryTab {
  key: 'todos' | 'activo' | 'espacio';
  label: string;
  to: string;
}

export const INVENTORY_TABS: InventoryTab[] = [
  { key: 'todos', label: 'Todos', to: '/inventario' },
  { key: 'activo', label: 'Activos Físicos', to: '/inventario/activos' },
  { key: 'espacio', label: 'Espacios Académicos', to: '/inventario/espacios' },
];
