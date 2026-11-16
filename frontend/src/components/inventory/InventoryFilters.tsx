import { RotateCcw, SlidersHorizontal } from 'lucide-react';
import { INVENTORY_TABS } from '@/constants/navigation';
import { STATUS_OPTIONS } from '@/constants/status';
import type { ResourceStatus } from '@/types';
import { SegmentedTabs } from '@/components/ui/SegmentedTabs';
import { SearchInput } from '@/components/ui/SearchInput';
import { SelectField } from '@/components/ui/Fields';

export type InventoryTabKey = 'todos' | 'activo' | 'espacio';

interface InventoryFiltersProps {
  tab: InventoryTabKey;
  onTabChange: (tab: InventoryTabKey) => void;
  query: string;
  onQueryChange: (value: string) => void;
  typeFilter: string;
  onTypeChange: (value: string) => void;
  statusFilter: ResourceStatus | 'todos';
  onStatusChange: (value: ResourceStatus | 'todos') => void;
  typeOptions: string[];
  hasFilters: boolean;
  onClear: () => void;
}

export function InventoryFilters({
  tab,
  onTabChange,
  query,
  onQueryChange,
  typeFilter,
  onTypeChange,
  statusFilter,
  onStatusChange,
  typeOptions,
  hasFilters,
  onClear,
}: InventoryFiltersProps) {
  return (
    <div className="rounded-[10px] border border-line bg-white p-4">
      <div className="flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between">
        <SegmentedTabs
          options={INVENTORY_TABS.map((item) => ({
            value: item.key,
            label: item.label,
          }))}
          value={tab}
          onChange={onTabChange}
          ariaLabel="Filtrar inventario por tipo de recurso"
        />
        <SearchInput
          value={query}
          onChange={onQueryChange}
          placeholder="Buscar por ID, placa, nombre..."
          ariaLabel="Buscar recursos en el inventario"
          size="sm"
          className="w-full lg:w-[320px]"
        />
      </div>

      <div className="mt-4 flex flex-wrap items-center gap-2.5">
        <SlidersHorizontal className="h-4 w-4 shrink-0 text-muted" aria-hidden />
        <SelectField
          label="Tipo"
          hideLabel
          value={typeFilter}
          onChange={onTypeChange}
          options={[
            { value: 'todos', label: 'Tipo: Todos' },
            ...typeOptions.map((option) => ({
              value: option,
              label: `Tipo: ${option}`,
            })),
          ]}
          fieldClassName="w-[190px]"
          className="h-8 rounded-lg text-[13px]"
        />
        <SelectField
          label="Estado"
          hideLabel
          value={statusFilter}
          onChange={(value) => onStatusChange(value as ResourceStatus | 'todos')}
          options={[
            { value: 'todos', label: 'Estado: Todos' },
            ...STATUS_OPTIONS.map((option) => ({
              value: option.value,
              label: `Estado: ${option.label}`,
            })),
          ]}
          fieldClassName="w-[185px]"
          className="h-8 rounded-lg text-[13px]"
        />
        <button
          type="button"
          onClick={onClear}
          disabled={!hasFilters}
          className="inline-flex h-8 items-center gap-1.5 rounded-lg px-3 font-display text-[13px] font-semibold text-brand transition-colors hover:bg-active-soft disabled:cursor-default disabled:text-muted/50 disabled:hover:bg-transparent"
        >
          <RotateCcw className="h-3.5 w-3.5" aria-hidden />
          Limpiar filtros
        </button>
      </div>
    </div>
  );
}
