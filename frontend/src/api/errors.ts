export class ApiError extends Error {
  readonly status: number;
  readonly path?: string;

  constructor(message: string, status: number, path?: string) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.path = path;
  }
}

interface BackendErrorBody {
  message?: string;
  error?: string;
  status?: number;
  path?: string;
}

export function toApiError(status: number, body: unknown, path?: string): ApiError {
  if (body && typeof body === 'object') {
    const parsed = body as BackendErrorBody;
    const message = parsed.message || parsed.error;
    if (message) return new ApiError(message, status, parsed.path ?? path);
  }
  return new ApiError(`La API respondió con estado ${status}.`, status, path);
}
