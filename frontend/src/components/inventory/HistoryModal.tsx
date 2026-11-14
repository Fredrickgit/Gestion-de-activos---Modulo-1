import { History } from 'lucide-react';
import type { Resource } from '@/types';
import { formatLongDate } from '@/lib/format';
import { Modal } from '@/components/ui/Modal';
import { EmptyState } from '@/components/ui/EmptyState';

interface HistoryModalProps {
  open: boolean;
  resource: Resource | null;
  onClose: () => void;
}

export function HistoryModal({ open, resource, onClose }: HistoryModalProps) {
  if (!resource) return null;

  const entries = [...resource.history].reverse();

  return (
    <Modal
      open={open}
      onClose={onClose}
      title="Historial del recurso"
      description={`${resource.name} · ${resource.id}`}
      size="lg"
    >
      {entries.length === 0 ? (
        <EmptyState
          icon={<History className="h-6 w-6" aria-hidden />}
          title="Sin movimientos registrados"
          description="Este recurso todavía no tiene cambios ni préstamos en su historial."
        />
      ) : (
        <ol className="relative ml-1 flex flex-col">
          {entries.map((entry, index) => (
            <li key={entry.id} className="relative flex gap-4 pb-6 last:pb-0">
              {index !== entries.length - 1 ? (
                <span
                  className="absolute left-[5px] top-3 h-full w-px bg-line"
                  aria-hidden
                />
              ) : null}
              <span
                className="relative z-10 mt-1.5 h-[11px] w-[11px] shrink-0 rounded-full border-2 border-white"
                style={{ backgroundColor: index === 0 ? '#1e40af' : '#94a3b8' }}
                aria-hidden
              />
              <div className="min-w-0 flex-1 rounded-lg border border-line bg-soft/60 px-3.5 py-3">
                <div className="flex flex-wrap items-center justify-between gap-2">
                  <h3 className="font-display text-sm font-bold text-ink">{entry.action}</h3>
                  <time className="font-mono text-[11px] font-bold text-muted">
                    {formatLongDate(entry.date)}
                  </time>
                </div>
                <p className="mt-1 text-[13px] leading-relaxed text-muted">{entry.detail}</p>
                <p className="mt-2 text-[11px] font-medium text-muted/90">
                  Registrado por {entry.actor}
                </p>
              </div>
            </li>
          ))}
        </ol>
      )}
    </Modal>
  );
}
