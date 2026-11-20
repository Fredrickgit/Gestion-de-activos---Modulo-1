import { useState, type FormEvent } from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { CircleAlert, Eye, EyeOff, LoaderCircle, ShieldCheck } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { TextField } from '@/components/ui/Fields';
import { useAuth } from '@/context/AuthContext';
import { env } from '@/config/env';

interface LoginLocationState {
  from?: string;
}

export function LoginPage() {
  const { user, signIn } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const destination =
    (location.state as LoginLocationState | null)?.from ?? '/inventario';

  if (user) {
    return <Navigate to={destination} replace />;
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await signIn(username, password);
      navigate(destination, { replace: true });
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'No fue posible iniciar sesión.',
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-app px-4 py-10">
      <div className="w-full max-w-[900px] overflow-hidden rounded-[28px] bg-white shadow-modal md:grid md:grid-cols-[1.05fr_1fr]">
        <aside className="relative hidden flex-col justify-between overflow-hidden bg-brand-dark p-10 text-white md:flex">
          <div
            className="pointer-events-none absolute -right-20 -top-24 h-72 w-72 rounded-full bg-white/10"
            aria-hidden
          />
          <div
            className="pointer-events-none absolute -bottom-28 -left-20 h-80 w-80 rounded-full bg-white/5"
            aria-hidden
          />

          <div className="relative">
            <div className="inline-flex rounded-2xl bg-white p-3 shadow-lg">
              <img
                src="/assets/logo-unimagdalena.png"
                alt="Escudo de la Universidad del Magdalena"
                className="h-16 w-[68px] object-contain"
                width={68}
                height={64}
              />
            </div>
            <p className="mt-8 font-display text-2xl font-bold leading-tight">
              Universidad
              <br />
              del Magdalena
            </p>
            <p className="mt-2 font-display text-sm italic text-white/70">
              Gestión de activos
            </p>
          </div>

          <div className="relative mt-10">
            <div className="flex items-center gap-2 text-white/80">
              <ShieldCheck className="h-5 w-5" aria-hidden />
              <span className="text-xs font-semibold uppercase tracking-[0.14em]">
                Acceso seguro
              </span>
            </div>
            <p className="mt-3 max-w-[28ch] text-sm leading-relaxed text-white/60">
              Ingresa con tus credenciales institucionales para consultar y
              gestionar el inventario de recursos.
            </p>
          </div>
        </aside>

        <section className="p-7 sm:p-10">
          <div className="mb-7 flex items-center gap-3 md:hidden">
            <img
              src="/assets/logo-unimagdalena.png"
              alt="Escudo de la Universidad del Magdalena"
              className="h-12 w-[52px] object-contain"
              width={52}
              height={48}
            />
            <div>
              <p className="font-display text-sm font-semibold uppercase leading-tight text-brand-dark">
                Universidad del Magdalena
              </p>
              <p className="font-display text-xs italic text-muted">
                Gestión de activos
              </p>
            </div>
          </div>

          <div>
            <p className="text-[11px] font-semibold uppercase tracking-[0.16em] text-brand">
              Iniciar sesión
            </p>
            <h1 className="mt-2 font-display text-[26px] font-bold leading-tight text-ink">
              Bienvenido de nuevo
            </h1>
            <p className="mt-2 text-sm leading-relaxed text-muted">
              Usa tus credenciales para entrar al módulo de gestión de activos.
            </p>
          </div>

          <form className="mt-7 flex flex-col gap-5" onSubmit={handleSubmit} noValidate>
            <TextField
              label="Usuario"
              value={username}
              onChange={setUsername}
              placeholder="Ingresa tu usuario"
              autoComplete="username"
              autoFocus
              required
              disabled={submitting}
            />

            <TextField
              label="Contraseña"
              value={password}
              onChange={setPassword}
              placeholder="Ingresa tu contraseña"
              type={showPassword ? 'text' : 'password'}
              autoComplete="current-password"
              required
              disabled={submitting}
              rightAdornment={
                <button
                  type="button"
                  onClick={() => setShowPassword((current) => !current)}
                  aria-label={
                    showPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'
                  }
                  className="rounded-md p-2 text-muted transition-colors hover:bg-soft hover:text-ink"
                >
                  {showPassword ? (
                    <EyeOff className="h-[18px] w-[18px]" aria-hidden />
                  ) : (
                    <Eye className="h-[18px] w-[18px]" aria-hidden />
                  )}
                </button>
              }
            />

            {error ? (
              <div
                role="alert"
                className="flex items-start gap-2 rounded-lg border border-danger/25 bg-red-50 px-3 py-2.5 text-xs font-medium leading-snug text-danger"
              >
                <CircleAlert className="mt-px h-4 w-4 shrink-0" aria-hidden />
                <span>{error}</span>
              </div>
            ) : null}

            <Button
              type="submit"
              size="lg"
              fullWidth
              disabled={submitting}
              icon={
                submitting ? (
                  <LoaderCircle className="h-5 w-5 animate-spin" aria-hidden />
                ) : undefined
              }
            >
              {submitting ? 'Ingresando…' : 'Ingresar'}
            </Button>
          </form>

          {env.demoMode ? (
            <p className="mt-5 rounded-lg bg-soft px-3 py-2.5 text-[11px] leading-relaxed text-muted">
              Modo demostración: usa <strong className="text-ink">admin / admin</strong>{' '}
              (Dirección) o <strong className="text-ink">monitor / monitor</strong>{' '}
              (Monitor).
            </p>
          ) : null}

          <p className="mt-8 text-center text-[11px] leading-tight text-muted">
            © 2026 Universidad del Magdalena · Módulo de Gestión v1.0
          </p>
        </section>
      </div>
    </div>
  );
}
