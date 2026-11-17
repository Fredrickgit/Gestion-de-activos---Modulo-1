# Despliegue en Render.com

Documentación del despliegue actual del **Módulo 1 — Gestión de Activos Universitarios**.

---

## 1. Qué es Render.com

Render.com es una plataforma en la nube (PaaS) que permite desplegar
aplicaciones, bases de datos y sitios estáticos sin administrar servidores.

En este proyecto se usa para alojar:

- Una **API REST** desarrollada con Spring Boot.
- Un **frontend** desarrollado con React + Vite.
- Una **base de datos PostgreSQL**.

Render ofrece, entre otras cosas:

- Despliegue automático desde un repositorio de GitHub.
- HTTPS y certificados TLS administrados.
- Servicios en Docker.
- PostgreSQL administrado.
- Sitios estáticos servidos por CDN.
- Un plan gratuito para pruebas y demostraciones.

### Cómo funciona el despliegue

Cada vez que se hace `push` a la rama configurada, Render reconstruye y publica
el servicio correspondiente.

```
GitHub (rama develop)
        │
        ├── push ──> Backend  (Docker)     ──> PostgreSQL
        │
        └── push ──> Frontend (Static Site)
```

El backend y el frontend son **dos servicios independientes**. No es que el
backend sirva el frontend: cada uno tiene su propia URL, su propio build y su
propio despliegue.

---

## 2. Qué tenemos creado en Render

Actualmente existen estos recursos:

| Recurso | Tipo en Render | Nombre del servicio | URL pública |
|---|---|---|---|
| Backend / API REST | Web Service con Docker | `gestion-de-activos-modulo-1` | `https://gestion-de-activos-modulo-1.onrender.com` |
| Frontend | Static Site | `gestion-de-activos-modulo-1-0yu6` | `https://gestion-de-activos-modulo-1-0yu6.onrender.com` |
| Base de datos | Render Postgres | Ver Dashboard | Solo conexión interna |

### 2.1 Backend

- Aplicación Spring Boot 4 con Java 21.
- Se ejecuta dentro de un contenedor Docker.
- Usa PostgreSQL como base de datos.
- Aplica las migraciones con Flyway al arrancar.
- Expone la API bajo `/api/v1`.
- Está protegido con autenticación HTTP Basic.
- En el plan gratuito se duerme tras 15 minutos sin tráfico.

Ejemplos de endpoints:

```
GET  /api/v1/recursos
GET  /api/v1/recursos/{id}
GET  /api/v1/recursos/{id}/historial
GET  /api/v1/catalogos
GET  /api/v1/reportes
POST /api/v1/activos
POST /api/v1/espacios
PATCH /api/v1/recursos/{id}
PATCH /api/v1/recursos/{id}/estado
DELETE /api/v1/recursos/{id}
```

### 2.2 Frontend

- Aplicación React 19 + Vite + TypeScript.
- Se compila a archivos estáticos en `frontend/dist`.
- Render la publica como **Static Site** sobre CDN.
- No se duerme.
- Usa React Router, por lo que necesita una regla de rewrite para las rutas.
- Consume la API del backend por HTTPS.

### 2.3 Base de datos

- PostgreSQL administrado por Render.
- Se conecta al backend mediante la red interna de Render.
- En el plan gratuito tiene 1 GB y expira a los 30 días.
- La contraseña la genera Render y se consulta en el Dashboard.
- Las tablas y catálogos se crean con las migraciones Flyway del backend.

---

## 3. Configuración actual

### 3.1 Backend — Web Service

| Opción | Valor |
|---|---|
| Tipo | Web Service |
| Runtime | Docker |
| Nombre | `gestion-de-activos-modulo-1` |
| Repositorio | `Fredrickgit/Gestion-de-activos---Modulo-1` |
| Rama | `develop` |
| Plan | Free |
| Región | Oregon |
| Dockerfile | `backend/Dockerfile` |
| Contexto de build | `backend` |
| Auto deploy | Sí, en cada commit a `develop` |
| Health Check Path | Vacío |
| Puerto | Asignado por Render mediante `PORT` |

#### Variables de entorno del backend

| Variable | Valor | Para qué sirve |
|---|---|---|
| `SECURITY_USER` | `admin_unimag` | Usuario HTTP Basic de la API |
| `SECURITY_PASSWORD` | `Unimag2026*Activos` | Contraseña HTTP Basic |
| `CORS_ALLOWED_ORIGINS` | `https://gestion-de-activos-modulo-1-0yu6.onrender.com` | Permite las peticiones del frontend |
| `DB_HOST` | Generado por Render | Host interno de PostgreSQL |
| `DB_PORT` | Generado por Render | Puerto de PostgreSQL |
| `DB_NAME` | Generado por Render | Nombre de la base de datos |
| `DB_USERNAME` | Generado por Render | Usuario de PostgreSQL |
| `DB_PASSWORD` | Generado por Render | Contraseña de PostgreSQL |
| `KAFKA_LISTENERS_AUTO_STARTUP` | `false` | Evita intentar conectar con Kafka |
| `OUTBOX_RELAY_ENABLED` | `false` | Desactiva el relay de eventos |
| `EXTERNAL_VALIDATION_ENABLED` | `false` | Evita llamadas a los módulos 2 y 3 |

> Las variables `DB_*` no se escriben a mano si se usa el Blueprint: Render las
> inyecta desde la base de datos. Si la base se creó manualmente, se copian
> desde la sección **Connect** del Dashboard.

#### Credenciales de la API

```
Usuario:    admin_unimag
Contraseña: Unimag2026*Activos
Tipo:       HTTP Basic
```

Estas mismas credenciales deben estar configuradas en el frontend como
`VITE_API_USERNAME` y `VITE_API_PASSWORD`.

---

### 3.2 Frontend — Static Site

| Opción | Valor |
|---|---|
| Tipo | Static Site |
| Runtime | Static |
| Nombre | `gestion-de-activos-modulo-1-0yu6` |
| Repositorio | `Fredrickgit/Gestion-de-activos---Modulo-1` |
| Rama | `develop` |
| Build Command | `cd frontend && npm ci && npm run build` |
| Publish Directory | `frontend/dist` |
| Auto deploy | Sí, en cada commit a `develop` |
| Rewrite | `/*` → `/index.html` con acción **Rewrite** |

#### Variables de entorno del frontend

| Variable | Valor | Para qué sirve |
|---|---|---|
| `VITE_API_BASE_URL` | `https://gestion-de-activos-modulo-1.onrender.com/api/v1` | URL base de la API |
| `VITE_API_USERNAME` | `admin_unimag` | Usuario HTTP Basic |
| `VITE_API_PASSWORD` | `Unimag2026*Activos` | Contraseña HTTP Basic |
| `VITE_API_TIMEOUT_MS` | `60000` | Tiempo máximo por petición |
| `VITE_DEMO_MODE` | `false` | Usa el backend real, no datos de demostración |

> Las variables `VITE_*` se graban dentro del JavaScript durante el build.
> Después de cambiarlas hay que hacer **Manual Deploy → Clear build cache &
> deploy**.

#### Regla de rewrite

| Campo | Valor |
|---|---|
| Source | `/*` |
| Destination | `/index.html` |
| Action | `Rewrite` |

Esta regla es necesaria para React Router. Sin ella, al entrar directamente a
`/inventario`, `/reportes` o cualquier otra ruta, Render devolvería `404`.

---

### 3.3 Base de datos — Render Postgres

| Opción | Valor |
|---|---|
| Motor | PostgreSQL |
| Plan | Free |
| Región | La misma del backend (Oregon) |
| Nombre de la base | `gestion_activos_modulo1` |
| Usuario | `gestion_activos` |
| Contraseña | Generada por Render; visible en el Dashboard |
| Almacenamiento | 1 GB |
| Vencimiento | 30 días en el plan gratuito |

> Los nombres `gestion_activos_modulo1` y `gestion_activos` corresponden al
> Blueprint de ejemplo. Si la base se creó manualmente, usa los valores que
> muestre el Dashboard.

#### Variables de conexión

El backend se conecta usando:

```
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
```

Los valores reales están en:

```
Render Dashboard → Base de datos → Connect
```

---

### 3.4 Comunicación entre frontend, backend y base de datos

```
Navegador
   │
   │ HTTPS
   ▼
Frontend estático
https://gestion-de-activos-modulo-1-0yu6.onrender.com
   │
   │ fetch a /api/v1 con HTTP Basic
   ▼
Backend Docker
https://gestion-de-activos-modulo-1.onrender.com
   │
   │ conexión interna
   ▼
PostgreSQL en Render
```

El backend solo acepta peticiones del origen configurado en
`CORS_ALLOWED_ORIGINS`. Si el frontend cambia de dominio, hay que actualizar esa
variable y redesplegar el backend.

---

## 4. Lista organizada de la configuración

### 4.1 URLs

| Elemento | URL |
|---|---|
| Frontend | `https://gestion-de-activos-modulo-1-0yu6.onrender.com` |
| Backend | `https://gestion-de-activos-modulo-1.onrender.com` |
| API Base | `https://gestion-de-activos-modulo-1.onrender.com/api/v1` |
| Catálogos | `https://gestion-de-activos-modulo-1.onrender.com/api/v1/catalogos` |
| Recursos | `https://gestion-de-activos-modulo-1.onrender.com/api/v1/recursos` |

### 4.2 Credenciales de administración

| Dato | Valor |
|---|---|
| Usuario administrador | `admin_unimag` |
| Contraseña | `Unimag2026*Activos` |
| Tipo de autenticación | HTTP Basic |
| Permisos | Rol `DIRECCION` |

Estas credenciales se usan en:

| Servicio | Variable | Valor |
|---|---|---|
| Backend | `SECURITY_USER` | `admin_unimag` |
| Backend | `SECURITY_PASSWORD` | `Unimag2026*Activos` |
| Frontend | `VITE_API_USERNAME` | `admin_unimag` |
| Frontend | `VITE_API_PASSWORD` | `Unimag2026*Activos` |

### 4.3 Variables del backend

```
SECURITY_USER=admin_unimag
SECURITY_PASSWORD=Unimag2026*Activos

CORS_ALLOWED_ORIGINS=https://gestion-de-activos-modulo-1-0yu6.onrender.com

KAFKA_LISTENERS_AUTO_STARTUP=false
OUTBOX_RELAY_ENABLED=false
EXTERNAL_VALIDATION_ENABLED=false

DB_HOST=<host interno de Render>
DB_PORT=<puerto de Render>
DB_NAME=<base de datos>
DB_USERNAME=<usuario de la base>
DB_PASSWORD=<contraseña de la base>
```

### 4.4 Variables del frontend

```
VITE_API_BASE_URL=https://gestion-de-activos-modulo-1.onrender.com/api/v1
VITE_API_USERNAME=admin_unimag
VITE_API_PASSWORD=Unimag2026*Activos
VITE_API_TIMEOUT_MS=60000
VITE_DEMO_MODE=false
```

### 4.5 Credenciales de PostgreSQL

| Dato | Dónde consultarlo |
|---|---|
| Host | Render → base de datos → Connect |
| Puerto | Render → base de datos → Connect |
| Base de datos | Render → base de datos → Connect |
| Usuario | Render → base de datos → Connect |
| Contraseña | Render → base de datos → Connect |

No se documenta la contraseña de PostgreSQL porque Render la genera y puede
rotarse desde el Dashboard.

### 4.6 Configuración del frontend en Render

```
Tipo:              Static Site
Rama:              develop
Build Command:     cd frontend && npm ci && npm run build
Publish Directory: frontend/dist
Rewrite:           /*  →  /index.html   (Action: Rewrite)
```

### 4.7 Configuración del backend en Render

```
Tipo:              Web Service
Runtime:           Docker
Rama:              develop
Dockerfile:        backend/Dockerfile
Contexto:          backend
Health Check Path: (vacío)
```

### 4.8 Verificación rápida

Probar los catálogos:

```bash
curl -i -u "admin_unimag:Unimag2026*Activos" \
  -H "Origin: https://gestion-de-activos-modulo-1-0yu6.onrender.com" \
  "https://gestion-de-activos-modulo-1.onrender.com/api/v1/catalogos"
```

Respuesta esperada:

```http
HTTP/2 200
access-control-allow-origin: https://gestion-de-activos-modulo-1-0yu6.onrender.com
```

Probar los recursos:

```bash
curl -i -u "admin_unimag:Unimag2026*Activos" \
  "https://gestion-de-activos-modulo-1.onrender.com/api/v1/recursos"
```

### 4.9 Orden correcto para registrar información

La base de datos arranca vacía, pero los catálogos ya vienen creados por la
migración `V5__seed_catalogs.sql`.

1. Registrar primero un **espacio**.
   - El equipamiento es opcional si todavía no existen activos.
2. Registrar un **activo** y asociarlo al espacio creado.
3. Editar la ficha del activo o del espacio.
4. Cambiar el estado del recurso.
5. Consultar el historial.
6. Revisar los reportes.

### 4.10 Notas importantes

- El backend del plan gratuito se duerme tras 15 minutos sin tráfico.
  La primera petición puede tardar cerca de un minuto.
- El frontend estático no se duerme.
- La base PostgreSQL gratuita expira a los 30 días.
- Solo se permite una base PostgreSQL gratuita por workspace.
- Las credenciales del frontend quedan visibles en el bundle del navegador.
- Si se cambia `VITE_API_BASE_URL` o las credenciales del frontend, se debe
  hacer **Clear build cache & deploy**.
- Si se cambia `CORS_ALLOWED_ORIGINS`, se debe redesplegar el backend.
- Si se usa el Blueprint, actualizar también `render.yaml` con las URLs reales
  para que un **Sync** no vuelva a escribir los valores de ejemplo.
