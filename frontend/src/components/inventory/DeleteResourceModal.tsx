import { TriangleAlert } from 'lucide-react';
import type { Resource } from '@/types';
import { Button } from '@/components/ui/Button';
import { Modal } from '@/components/ui/Modal';

interface DeleteResourceModalProps {
  open: boolean;
  resource: Resource | null;
  onClose: () => void;
  onConfirm: () => void;
}

export function DeleteResourceModal({
  open,
  resource,
  onClose,
  onConfirm,
}: DeleteResourceModalProps) {
  if (!resource) return null;

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Eliminar recurso"
      size="sm"
      footer={
        <>
          <Button variant="outline" onClick={onClose}>
            Cancelar
          </Button>
          <Button
            onClick={onConfirm}
            className="border border-danger bg-danger text-white hover:bg-red-600 active:bg-red-700"
          >
            Sí, eliminar
          </Button>
        </>
      }
    >
      <div className="flex items-start gap-4">
        <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-red-50 text-danger">
          <TriangleAlert className="h-5 w-5" aria-hidden />
        </span>
        <div className="min-w-0 text-sm leading-relaxed text-muted">
          <p>
            ¿Seguro que deseas eliminar{' '}
            <strong className="font-semibold text-ink">{resource.name}</strong> del inventario?
          </p>
          <p className="mt-2">
            Esta acción quitará el recurso de las tarjetas, los filtros y los reportes. No se
            puede deshacer.
          </p>
          <p className="mt-4 inline-flex rounded-md bg-soft px-2.5 py-1 font-mono text-xs font-bold text-ink">
            {resource.id}
          </p>
        </div>
      </div>
    </Modal>
  );
}
