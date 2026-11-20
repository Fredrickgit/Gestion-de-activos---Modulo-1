import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react';
import { ApiError } from '@/api/errors';
import { apiRequest } from '@/api/http';
import { setApiCredentials, type BasicCredentials } from '@/api/authStore';
import { env } from '@/config/env';

export type UserRole = 'DIRECCION' | 'MONITOR';

export interface AuthUser {
  username: string;
  role: UserRole;
  roleLabel: string;
}

interface Session {
  user: AuthUser;
  password: string;
}

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  signIn: (username: string, password: string) => Promise<void>;
  signOut: () => void;
}

interface AuthMeResponse {
  usuario: string;
  rol: string;
}

const ROLE_LABELS: Record<UserRole, string> = {
  DIRECCION: 'Dirección Universitaria',
  MONITOR: 'Monitor de recursos',
};

const SESSION_KEY = 'gestion-activos.session';

/** Credenciales locales para poder probar la app sin backend configurado. */
const DEMO_USERS: Record<string, { password: string; role: UserRole }> = {
  admin: { password: 'admin', role: 'DIRECCION' },
  monitor: { password: 'monitor', role: 'MONITOR' },
};

const AuthContext = createContext<AuthContextValue | null>(null);

function roleLabel(role: UserRole): string {
  return ROLE_LABELS[role];
}

function normalizeRole(raw: string): UserRole {
  return raw.toUpperCase().includes('DIRECCION') ? 'DIRECCION' : 'MONITOR';
}

function readStoredSession(): Session | null {
  try {
    const raw = sessionStorage.getItem(SESSION_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as Partial<Session>;
    const username = parsed.user?.username;
    const password = parsed.password;
    const role = parsed.user?.role;
    if (
      typeof username !== 'string' ||
      typeof password !== 'string' ||
      (role !== 'DIRECCION' && role !== 'MONITOR')
    ) {
      return null;
    }
    return {
      user: { username, role, roleLabel: roleLabel(role) },
      password,
    };
  } catch {
    return null;
  }
}

function persistSession(session: Session | null): void {
  try {
    if (session) {
      sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
    } else {
      sessionStorage.removeItem(SESSION_KEY);
    }
  } catch {
    // sessionStorage puede estar bloqueado; la sesión sigue en memoria.
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => {
    const stored = readStoredSession();
    if (stored) {
      setApiCredentials({
        username: stored.user.username,
        password: stored.password,
      });
    }
    return stored;
  });

  const signIn = useCallback(async (username: string, password: string) => {
    const cleanUsername = username.trim();
    if (!cleanUsername || !password) {
      throw new Error('Ingresa tu usuario y contraseña.');
    }

    let role: UserRole;

    if (env.demoMode) {
      const demo = DEMO_USERS[cleanUsername.toLowerCase()];
      if (!demo || demo.password !== password) {
        throw new Error('Usuario o contraseña incorrectos.');
      }
      role = demo.role;
    } else {
      const credentials: BasicCredentials = {
        username: cleanUsername,
        password,
      };
      try {
        const response = await apiRequest<AuthMeResponse>('auth/me', {
          auth: credentials,
        });
        role = normalizeRole(response.rol);
      } catch (cause) {
        if (cause instanceof ApiError && cause.status === 401) {
          throw new Error('Usuario o contraseña incorrectos.');
        }
        throw cause instanceof Error
          ? cause
          : new Error('No fue posible iniciar sesión.');
      }
    }

    const next: Session = {
      user: {
        username: cleanUsername,
        role,
        roleLabel: roleLabel(role),
      },
      password,
    };
    setApiCredentials({ username: cleanUsername, password });
    persistSession(next);
    setSession(next);
  }, []);

  const signOut = useCallback(() => {
    setApiCredentials(null);
    persistSession(null);
    setSession(null);
  }, []);

  const value = useMemo<AuthContextValue>(
    () => ({
      user: session?.user ?? null,
      isAuthenticated: session !== null,
      signIn,
      signOut,
    }),
    [session, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de AuthProvider');
  }
  return context;
}
