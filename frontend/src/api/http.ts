import { env } from '@/config/env';
import { ApiError, toApiError } from '@/api/errors';

type Method = 'GET' | 'POST' | 'PATCH' | 'PUT' | 'DELETE';

interface RequestOptions {
  method?: Method;
  body?: unknown;
  query?: Record<string, string | number | boolean | undefined | null>;
  timeoutMs?: number;
}

function buildUrl(path: string, query?: RequestOptions['query']): string {
  const url = new URL(
    `${env.apiBaseUrl}/${path.replace(/^\/+/, '')}`,
    window.location.origin,
  );

  if (query) {
    for (const [key, value] of Object.entries(query)) {
      if (value === undefined || value === null || value === '') continue;
      url.searchParams.set(key, String(value));
    }
  }

  return url.toString();
}

function authHeader(): Record<string, string> {
  if (!env.apiUsername && !env.apiPassword) return {};
  const token = btoa(`${env.apiUsername}:${env.apiPassword}`);
  return { Authorization: `Basic ${token}` };
}

async function parseBody(response: Response): Promise<unknown> {
  if (response.status === 204) return undefined;
  const text = await response.text();
  if (!text) return undefined;
  try {
    return JSON.parse(text);
  } catch {
    return text;
  }
}

export async function apiRequest<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const controller = new AbortController();
  const timeout = window.setTimeout(
    () => controller.abort(),
    options.timeoutMs ?? env.apiTimeoutMs,
  );

  try {
    const response = await fetch(buildUrl(path, options.query), {
      method: options.method ?? 'GET',
      headers: {
        Accept: 'application/json',
        ...(options.body !== undefined ? { 'Content-Type': 'application/json' } : {}),
        ...authHeader(),
      },
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
      signal: controller.signal,
    });

    const body = await parseBody(response);

    if (!response.ok) {
      throw toApiError(response.status, body, path);
    }

    return body as T;
  } catch (error) {
    if (error instanceof ApiError) throw error;
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new ApiError(
        'La API tardó demasiado en responder. Si el servicio estaba dormido, intenta de nuevo.',
        408,
        path,
      );
    }
    throw new ApiError(
      'No fue posible conectar con la API. Verifica la URL y la conexión.',
      0,
      path,
    );
  } finally {
    window.clearTimeout(timeout);
  }
}
