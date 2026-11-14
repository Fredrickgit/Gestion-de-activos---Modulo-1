import { useEffect, useState } from 'react';
import { Circle } from 'lucide-react';
import type { Resource, ResourceStatus } from '@/types';
import { RESOURCE_STATUSES, STATUS_META } from '@/constants/status';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';
import { cn } from '@/lib/cn';

const DESCRIPTIONS: Record<ResourceStatus, string> = {
  disponible: 'Listo para préstamo o asignación.',
  reservado: 'Apartado para una actividad programada.',
  'en-uso': 'Actualmente prestado o en operación.',
  mantenimiento: 'Fuera de circulación por revisión técnica.',
  bloqueo: 'No disponible por decisión académica.',
};

interface ChangeStatusModalProps {
  open: boolean;
  resource: Resource | null;
  onClose: () => void;
  onConfirm: (status: ResourceStatus) => void;
}

export function ChangeStatusModal({
  open,
  resource,
  onClose,
  onConfirm,
}: ChangeStatusModalProps) {
  const [selected, setSelected] = useState<ResourceStatus>('disponible');

  useEffect(() => {
    if (resource) setSelected(resource.status);
  }, [resource, open]);

  if (!resource) return null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Cambiar estado"
      description={`Selecciona el nuevo estado para ${resource.name}.`}
      footer={
        <>
          <Button variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button
            onClick={() => onConfirm(selected)}
            disabled={selected === resource.status}
          >
            Actualizar estado
          </Button>
        </>
      }
    >
      <div role="radiogroup" aria-label="Estado del recurso" className="flex flex-col gap-2.5">
        {RESOURCE_STATUSES.map((status) => {
          const meta = STATUS_META[status];
          const active = selected === status;
          return (
            <label
              key={status}
              className={cn(
                'flex cursor-pointer items-start gap-3 rounded-xl border p-3.5 transition-colors',
                active
                  ? 'border-brand/40 bg-active-soft'
                  : 'border-line bg-white hover:border-slate-300 hover:bg-soft',
              )}
            >
              <input
                type="radio"
                name="resource-status"
                value={status}
                checked={active}
                onChange={() => setSelected(status)}
                className="sr-only"
              />
              <span
                className="mt-0.5 flex h-5 w-5 shrink-0 items-center justify-center rounded-full border-2"
                style={{
                  borderColor: active ? meta.badge : '#cbd5e1',
                  backgroundColor: active ? meta.badge : 'transparent',
                }}
                aria-hidden
              >
                {active ? <Circle className="h-2 w-2 fill-white text-white" /> : null}
              </span>
              <span className="min-w-0">
                <span className="block font-display text-sm font-bold text-ink">
                  {meta.shortLabel}
                </span>
                <span className="mt-0.5 block text-xs leading-relaxed text-muted">
                  {DESCRIPTIONS[status]}
                </span>
              </span>
            </label>
          );
        })}
      </div>
    </Modal>
  );
}
