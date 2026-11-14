import type { ReactNode } from 'react';
import { Building2, Package, X } from 'lucide-react';
import type { Resource } from '@/types';
import { STATUS_META } from '@/constants/status';
import { cn } from '@/lib/cn';
import { Button } from '@/components/ui/Button';
import { IconButton } from '@/components/ui/IconButton';
import { StatusBadge } from '@/components/ui/Badge';

interface ResourceDetailPanelProps {
  resource: Resource;
  onClose: () => void;
  onEdit: () => void;
  onChangeStatus: () => void;
  onDelete: () => void;
  onHistory: () => void;
  className?: string;
}

function DetailRow({ label, children }: { label: string; children: ReactNode }) {
  return (
    <div className="min-w-0">
      <dt className="font-display text-[13px] font-light italic leading-none text-muted">
        {label}
      </dt>
      <dd className="mt-1.5 min-w-0 font-display text-[15px] font-semibold leading-snug text-ink md:text-base">
        {children}
      </dd>
    </div>
  );
}

export function ResourceDetailPanel({
  resource,
  onClose,
  onEdit,
  onChangeStatus,
  onDelete,
  onHistory,
  className,
}: ResourceDetailPanelProps) {
  const meta = STATUS_META[resource.status];
  const categoryShort = resource.category.split(' ')[0];

  return (
    <section
      aria-label={`Detalle del recurso ${resource.name}`}
      className={cn(
        'flex h-full flex-col gap-4 rounded-[20px] border border-line bg-white px-5 py-5 shadow-drawer min-[1700px]:shadow-none md:px-6',
        className,
      )}
    >
      <header className="flex items-center justify-between gap-4">
        <h2 className="font-display text-xl font-semibold leading-tight text-ink">
          Detalle del recurso
        </h2>
        <IconButton
          label="Cerrar detalle del recurso"
          icon={<X className="h-4 w-4" aria-hidden />}
          onClick={onClose}
          className="h-8 w-8"
        />
      </header>

      <div className="relative h-[140px] w-full shrink-0 overflow-hidden rounded-2xl border border-line bg-slate-800">
        {resource.image ? (
          <img
            src={resource.image}
            alt={`Fotografía de ${resource.name}`}
            className="h-full w-full object-cover"
            loading="lazy"
          />
        ) : (
          <div className="flex h-full w-full items-center justify-center bg-[radial-gradient(circle_at_30%_20%,#475569,#0f172a)] text-white/70">
            {resource.kind === 'espacio' ? (
              <Building2 className="h-10 w-10" aria-hidden strokeWidth={1.4} />
            ) : (
              <Package className="h-10 w-10" aria-hidden strokeWidth={1.4} />
            )}
          </div>
        )}
        <span className="absolute left-3 top-3 inline-flex items-center rounded-full border border-line bg-white/90 px-2.5 py-1.5 font-display text-xs font-semibold text-ink backdrop-blur-sm">
          {categoryShort}
        </span>
        <span
          className="absolute inset-x-0 bottom-0 h-1"
          style={{ backgroundColor: meta.bar }}
          aria-hidden
        />
      </div>

      <dl className="flex flex-col gap-3">
        <DetailRow label="Nombre">
          <span className="text-[17px] leading-snug">{resource.name}</span>
        </DetailRow>

        <div className="grid grid-cols-2 gap-4">
          <DetailRow label="ID">
            <span className="font-mono text-[15px] font-bold">{resource.id}</span>
          </DetailRow>
          <DetailRow label="Estado">
            <StatusBadge status={resource.status} size="md" uppercase />
          </DetailRow>
        </div>

        <DetailRow label="Ubicación">{resource.location}</DetailRow>

        {resource.kind === 'activo' ? (
          <div className="grid grid-cols-2 gap-4">
            <DetailRow label="Categoría">{resource.category}</DetailRow>
            <DetailRow label="Condición">{resource.condition}</DetailRow>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-4">
            <DetailRow label="Facultad">{resource.faculty}</DetailRow>
            <DetailRow label="Aforo">
              {resource.capacity ? `${resource.capacity} personas` : '—'}
            </DetailRow>
          </div>
        )}

        <div className="grid grid-cols-2 gap-4">
          <DetailRow label="Tiempo de préstamo">{resource.maxTerm}</DetailRow>
          <DetailRow label="Responsable">{resource.responsible}</DetailRow>
        </div>
      </dl>

      <div className="mt-auto flex flex-col gap-2.5 pt-2">
        <Button onClick={onEdit} fullWidth>
          Editar recurso
        </Button>
        <Button variant="outline" onClick={onChangeStatus} fullWidth>
          Cambiar estado
        </Button>
        <Button variant="danger" onClick={onDelete} fullWidth>
          Eliminar
        </Button>
        <Button variant="outline" onClick={onHistory} fullWidth>
          Ver historial
        </Button>
      </div>
    </section>
  );
}
