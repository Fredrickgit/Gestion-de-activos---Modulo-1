import { createPortal } from 'react-dom';
import { CircleAlert, CircleCheck, Info, X } from 'lucide-react';
import { useToast } from '@/context/ToastContext';
import { cn } from '@/lib/cn';

const ICONS = {
  success: CircleCheck,
  error: CircleAlert,
  info: Info,
};

const TONES = {
  success: 'text-ok',
  error: 'text-danger',
  info: 'text-brand',
};

export function ToastViewport() {
  const { toasts, dismissToast } = useToast();

  if (toasts.length === 0) return null;

  return createPortal(
    <div
      className="pointer-events-none fixed inset-x-4 bottom-4 z-[200] flex flex-col items-end gap-2 sm:left-auto sm:right-6 sm:bottom-6 sm:w-[380px]"
      aria-live="polite"
      aria-atomic="false"
    >
      {toasts.map((toast) => {
        const Icon = ICONS[toast.variant];
        return (
          <div
            key={toast.id}
            role="status"
            className="pointer-events-auto flex w-full items-start gap-3 rounded-xl border border-line bg-white p-3.5 shadow-modal animate-slide-up"
          >
            <Icon
              className={cn('mt-0.5 h-5 w-5 shrink-0', TONES[toast.variant])}
              aria-hidden
            />
            <div className="min-w-0 flex-1">
              <p className="font-display text-sm font-bold leading-tight text-ink">
                {toast.title}
              </p>
              {toast.description ? (
                <p className="mt-1 text-xs leading-relaxed text-muted">{toast.description}</p>
              ) : null}
            </div>
            <button
              type="button"
              onClick={() => dismissToast(toast.id)}
              aria-label="Cerrar notificación"
              className="rounded p-0.5 text-muted transition-colors hover:bg-soft hover:text-ink"
            >
              <X className="h-4 w-4" aria-hidden />
            </button>
          </div>
        );
      })}
    </div>,
    document.body,
  );
}
