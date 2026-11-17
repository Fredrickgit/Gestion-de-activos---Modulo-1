# Gestión de activos universitarios — Módulo 1

Frontend del módulo de inventario, registro y reportes de recursos universitarios.

## Stack

- Vite + React 19 + TypeScript
- Tailwind CSS v4 (`@tailwindcss/vite`)
- React Router
- lucide-react
- Tipografías: Plus Jakarta Sans (títulos), Inter (interfaz), Cousine (IDs)

## Configuración

Copia `.env.example` a `.env` y define la conexión:

```bash
cp .env.example .env
```

| Variable | Descripción |
| --- | --- |
| `VITE_API_BASE_URL` | URL base del backend, incluyendo `/api/v1` |
| `VITE_API_USERNAME` | Usuario HTTP Basic (`SECURITY_USER` en Render) |
| `VITE_API_PASSWORD` | Contraseña HTTP Basic (`SECURITY_PASSWORD` en Render) |
| `VITE_API_TIMEOUT_MS` | Timeout por petición (por defecto 60000) |
| `VITE_DEMO_MODE` | `true` usa los datos de demostración en memoria |

Si `VITE_API_BASE_URL` está vacío, la app arranca automáticamente en modo demo.

> Las variables `VITE_*` se incorporan al bundle durante el build. Un usuario
> puede leer las credenciales Basic desde el navegador. Para producción se
> recomienda un endpoint de login con token de corta duración o un proxy BFF.

## Scripts

```bash
npm install
npm run dev        # servidor de desarrollo
npm run typecheck  # verificación de tipos
npm run build      # build de producción
npm run preview    # previsualizar el build
```

## Rutas

| Ruta | Pantalla |
| --- | --- |
| `/inventario` | Inventario — todos los recursos |
| `/inventario/activos` | Inventario — activos |
| `/inventario/espacios` | Inventario — espacios |
| `/registrar` | Registrar nuevo recurso |
| `/reportes` | Generar reporte |

## Integración con el backend

La capa de acceso está en `src/api`:

- `src/config/env.ts`: variables de entorno.
- `api/http.ts`: `fetch` con HTTP Basic, timeout y errores tipados.
- `api/dto.ts`: contrato del backend.
- `api/mappers.ts`: traducción entre DTOs y el modelo de la UI.
- `api/resources.ts`: listado paginado, detalle, catálogos, historial, creación,
  edición, cambio de estado y baja lógica.
- `context/ResourcesContext.tsx`: estado asíncrono del inventario.
- `context/CatalogsContext.tsx`: facultades, ubicaciones, tipos de activo y estados.

Endpoints usados:

| Método | Ruta |
| --- | --- |
| `GET` | `/api/v1/recursos` |
| `GET` | `/api/v1/recursos/{id}` |
| `GET` | `/api/v1/recursos/{id}/disponibilidad` |
| `GET` | `/api/v1/recursos/{id}/historial` |
| `PATCH` | `/api/v1/recursos/{id}` |
| `DELETE` | `/api/v1/recursos/{id}` |
| `PATCH` | `/api/v1/recursos/{id}/estado` |
| `POST` | `/api/v1/activos` |
| `POST` | `/api/v1/espacios` |
| `GET` | `/api/v1/catalogos` |
| `GET` | `/api/v1/reportes` |

## Render

El `render.yaml` de la raíz crea el frontend como sitio estático:

- Build: `cd frontend && npm ci && npm run build`
- Publicación: `frontend/dist`
- Rewrite `/*` → `/index.html` para React Router
- Variables `VITE_*` configuradas en el Blueprint

El backend debe permitir el origen del frontend en `CORS_ALLOWED_ORIGINS`.
