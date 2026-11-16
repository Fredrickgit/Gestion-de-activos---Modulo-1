import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import type { Resource, ResourceInput, ResourceStatus } from '@/types';
import { INVENTORY_TABS } from '@/constants/navigation';
import { useResources } from '@/context/ResourcesContext';
import { useToast } from '@/context/ToastContext';
import { useMediaQuery } from '@/lib/media';
import { PageHeader } from '@/components/layout/PageHeader';
import { ContentSurface } from '@/components/layout/ContentSurface';
import { Button } from '@/components/ui/Button';
import { EmptyState } from '@/components/ui/EmptyState';
import { InventoryFilters, type InventoryTabKey } from '@/components/inventory/InventoryFilters';
import { StatusSummary } from '@/components/inventory/StatusSummary';
import { ResourceCard } from '@/components/inventory/ResourceCard';
import { ResourceDetailPanel } from '@/components/inventory/ResourceDetailPanel';
import { EditResourceModal } from '@/components/inventory/EditResourceModal';
import { ChangeStatusModal } from '@/components/inventory/ChangeStatusModal';
import { DeleteResourceModal } from '@/components/inventory/DeleteResourceModal';
import { HistoryModal } from '@/components/inventory/HistoryModal';

type InventoryModal = 'edit' | 'status' | 'delete' | 'history' | null;

interface InventoryPageProps {
  tab: InventoryTabKey;
}

export function InventoryPage({ tab }: InventoryPageProps) {
  const { resources, getResource, updateResource, changeStatus, deleteResource } =
    useResources();
  const { pushToast } = useToast();
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const query = searchParams.get('q') ?? '';

  const [typeFilter, setTypeFilter] = useState('todos');
  const [statusFilter, setStatusFilter] = useState<ResourceStatus | 'todos'>('todos');
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [panelOpen, setPanelOpen] = useState(false);
  const [modal, setModal] = useState<InventoryModal>(null);

  const isDesktop = useMediaQuery('(min-width: 1024px)');
  const isWide = useMediaQuery('(min-width: 1700px)');

  const tabResources = useMemo(
    () => resources.filter((resource) => tab === 'todos' || resource.kind === tab),
    [resources, tab],
  );

  const typeOptions = useMemo(
    () => [...new Set(tabResources.map((resource) => resource.category))].sort((a, b) => a.localeCompare(b)),
    [tabResources],
  );

  const visibleResources = useMemo(() => {
    const normalizedQuery = query.trim().toLowerCase();
    return tabResources.filter((resource) => {
      const searchable = [
        resource.id,
        resource.name,
        resource.location,
        resource.category,
        resource.faculty,
      ]
        .join(' ')
        .toLowerCase();
      const matchesQuery = !normalizedQuery || searchable.includes(normalizedQuery);
      const matchesType = typeFilter === 'todos' || resource.category === typeFilter;
      const matchesStatus = statusFilter === 'todos' || resource.status === statusFilter;
      return matchesQuery && matchesType && matchesStatus;
    });
  }, [tabResources, query, typeFilter, statusFilter]);

  const hasFilters = Boolean(query.trim()) || typeFilter !== 'todos' || statusFilter !== 'todos';
  const selectedResource = selectedId ? getResource(selectedId) : undefined;

  useEffect(() => {
    setTypeFilter('todos');
    setStatusFilter('todos');
    const first = tabResources[0]?.id ?? null;
    if (isDesktop) {
      setSelectedId((current) =>
        current && tabResources.some((resource) => resource.id === current) ? current : first,
      );
      setPanelOpen(true);
    } else {
      setPanelOpen(false);
      setSelectedId(null);
    }
    // Solo debe reaccionar al cambio de pestaña; el resto se gestiona con los filtros.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [tab]);

  useEffect(() => {
    if (!panelOpen) return;
    if (selectedId && visibleResources.some((resource) => resource.id === selectedId)) return;
    setSelectedId(visibleResources[0]?.id ?? null);
  }, [visibleResources, panelOpen, selectedId]);

  useEffect(() => {
    if (panelOpen && !selectedResource) {
      setPanelOpen(false);
    }
  }, [panelOpen, selectedResource]);

  useEffect(() => {
    if (!panelOpen || isWide) return;

    const previous = document.body.style.overflow;
    document.body.style.overflow = 'hidden';

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setPanelOpen(false);
    };
    document.addEventListener('keydown', handleKeyDown);

    return () => {
      document.body.style.overflow = previous;
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [panelOpen, isWide]);

  const updateQuery = (value: string) => {
    const next = new URLSearchParams(searchParams);
    if (value) next.set('q', value);
    else next.delete('q');
    setSearchParams(next, { replace: true });
  };

  const clearFilters = () => {
    setSearchParams({}, { replace: true });
    setTypeFilter('todos');
    setStatusFilter('todos');
  };

  const changeTab = (nextTab: InventoryTabKey) => {
    const target = INVENTORY_TABS.find((item) => item.key === nextTab);
    if (!target) return;
    navigate({
      pathname: target.to,
      search: query ? `?q=${encodeURIComponent(query)}` : '',
    });
  };

  const openResource = (resource: Resource) => {
    setSelectedId(resource.id);
    setPanelOpen(true);
  };

  const handleEditSave = (patch: Partial<ResourceInput>) => {
    if (!selectedResource) return;
    updateResource(selectedResource.id, patch);
    setModal(null);
    pushToast({
      variant: 'success',
      title: 'Recurso actualizado',
      description: `Los cambios de ${selectedResource.id} se guardaron correctamente.`,
    });
  };

  const handleStatusChange = (status: ResourceStatus) => {
    if (!selectedResource) return;
    changeStatus(selectedResource.id, status);
    setModal(null);
    pushToast({
      variant: 'success',
      title: 'Estado actualizado',
      description: `${selectedResource.name} ahora está en estado ${status.replace('-', ' ')}.`,
    });
  };

  const handleDelete = () => {
    if (!selectedResource) return;
    const deleted = selectedResource;
    deleteResource(deleted.id);
    setModal(null);
    setSelectedId(null);
    setPanelOpen(false);
    pushToast({
      variant: 'success',
      title: 'Recurso eliminado',
      description: `${deleted.name} se quitó del inventario.`,
    });
  };

  return (
    <>
      <PageHeader
        title="Inventario de recursos"
        size={tab === 'todos' ? 'lg' : 'md'}
        className="mt-4 md:mt-[21px]"
      />

      <div className="mt-3 flex flex-col gap-4 px-3 pb-8 pt-0 sm:px-4 lg:mt-[13px] lg:px-[13px] lg:pr-5 min-[1700px]:flex-row min-[1700px]:items-start xl:min-[1700px]:pr-5">
        <ContentSurface className="min-w-0 flex-1">
          <div className="flex flex-col gap-2.5">
            <InventoryFilters
              tab={tab}
              onTabChange={changeTab}
              query={query}
              onQueryChange={updateQuery}
              typeFilter={typeFilter}
              onTypeChange={setTypeFilter}
              statusFilter={statusFilter}
              onStatusChange={setStatusFilter}
              typeOptions={typeOptions}
              hasFilters={hasFilters}
              onClear={clearFilters}
            />

            <StatusSummary value={statusFilter} onChange={setStatusFilter} />

            <div className="rounded-2xl border border-line bg-white p-3 md:p-4">
              {visibleResources.length > 0 ? (
                <div className="flex flex-col gap-3 md:gap-4">
                  {visibleResources.map((resource) => (
                    <ResourceCard
                      key={resource.id}
                      resource={resource}
                      selected={resource.id === selectedId && panelOpen}
                      onOpen={openResource}
                    />
                  ))}
                </div>
              ) : (
                <EmptyState
                  title="No encontramos recursos"
                  description="Prueba con otro término de búsqueda o limpia los filtros para ver todo el inventario."
                  action={
                    <Button variant="outline" size="sm" onClick={clearFilters}>
                      Limpiar filtros
                    </Button>
                  }
                />
              )}
            </div>
          </div>
        </ContentSurface>

        {panelOpen && selectedResource ? (
          <>
            <button
              type="button"
              aria-label="Cerrar detalle del recurso"
              className="fixed inset-0 z-[60] bg-slate-950/40 backdrop-blur-[1px] animate-fade-in min-[1700px]:hidden"
              onClick={() => setPanelOpen(false)}
            />
            <aside className="fixed inset-y-0 right-0 z-[70] w-full overflow-y-auto p-3 animate-slide-in-right sm:max-w-[460px] sm:p-4 min-[1700px]:sticky min-[1700px]:top-4 min-[1700px]:z-auto min-[1700px]:w-[460px] min-[1700px]:max-w-none min-[1700px]:shrink-0 min-[1700px]:animate-none min-[1700px]:self-start min-[1700px]:overflow-visible min-[1700px]:p-0">
              <ResourceDetailPanel
                resource={selectedResource}
                onClose={() => setPanelOpen(false)}
                onEdit={() => setModal('edit')}
                onChangeStatus={() => setModal('status')}
                onDelete={() => setModal('delete')}
                onHistory={() => setModal('history')}
                className="min-h-full min-[1700px]:h-auto min-[1700px]:max-h-[calc(100vh-5.5rem)] min-[1700px]:overflow-y-auto"
              />
            </aside>
          </>
        ) : null}
      </div>

      <EditResourceModal
        open={modal === 'edit'}
        resource={selectedResource ?? null}
        onClose={() => setModal(null)}
        onSave={handleEditSave}
      />
      <ChangeStatusModal
        open={modal === 'status'}
        resource={selectedResource ?? null}
        onClose={() => setModal(null)}
        onConfirm={handleStatusChange}
      />
      <DeleteResourceModal
        open={modal === 'delete'}
        resource={selectedResource ?? null}
        onClose={() => setModal(null)}
        onConfirm={handleDelete}
      />
      <HistoryModal
        open={modal === 'history'}
        resource={selectedResource ?? null}
        onClose={() => setModal(null)}
      />
    </>
  );
}
