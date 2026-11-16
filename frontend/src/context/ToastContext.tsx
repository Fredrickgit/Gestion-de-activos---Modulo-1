import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import type { ToastMessage } from '@/types';

type ToastInput = Omit<ToastMessage, 'id'>;

interface ToastContextValue {
  toasts: ToastMessage[];
  pushToast: (toast: ToastInput) => void;
  dismissToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextValue | null>(null);

let toastCounter = 0;

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastMessage[]>([]);

  const dismissToast = useCallback((id: string) => {
    setToasts((current) => current.filter((toast) => toast.id !== id));
  }, []);

  const pushToast = useCallback(
    (toast: ToastInput) => {
      toastCounter += 1;
      const id = `toast-${toastCounter}`;
      setToasts((current) => [...current.slice(-2), { ...toast, id }]);
      window.setTimeout(() => dismissToast(id), 4500);
    },
    [dismissToast],
  );

  const value = useMemo(
    () => ({ toasts, pushToast, dismissToast }),
    [toasts, pushToast, dismissToast],
  );

  return <ToastContext.Provider value={value}>{children}</ToastContext.Provider>;
}

export function useToast(): ToastContextValue {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error('useToast debe usarse dentro de ToastProvider');
  }
  return context;
}
