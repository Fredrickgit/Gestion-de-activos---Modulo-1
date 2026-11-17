const rawBaseUrl = (import.meta.env.VITE_API_BASE_URL ?? '').trim();
const timeout = Number(import.meta.env.VITE_API_TIMEOUT_MS ?? '60000');

export const env = {
  /** URL base del backend, por ejemplo https://mi-api.onrender.com/api/v1 */
  apiBaseUrl: rawBaseUrl.replace(/\/+$/, ''),
  apiUsername: import.meta.env.VITE_API_USERNAME ?? '',
  apiPassword: import.meta.env.VITE_API_PASSWORD ?? '',
  apiTimeoutMs: Number.isFinite(timeout) && timeout > 0 ? timeout : 60_000,
  /**
   * Cuando no hay backend configurado o se activa explícitamente, la app usa
   * los datos de demostración en memoria.
   */
  demoMode:
    import.meta.env.VITE_DEMO_MODE === 'true' ||
    rawBaseUrl.length === 0,
} as const;
