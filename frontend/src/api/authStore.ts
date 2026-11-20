/**
 * Credenciales HTTP Basic de la sesión activa.
 *
 * <p>Se mantienen fuera de React para que la capa `api` pueda usarlas sin
 * recibir prop drilling desde cada pantalla.</p>
 */
export interface BasicCredentials {
  username: string;
  password: string;
}

let credentials: BasicCredentials | null = null;

export function setApiCredentials(next: BasicCredentials | null): void {
  credentials = next;
}

export function getApiCredentials(): BasicCredentials | null {
  return credentials;
}
