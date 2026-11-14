# Despliegue en Render.com (plan gratuito)

Guía del Módulo 1 — Spring Boot 4 + PostgreSQL + Kafka.

> **Resumen:** Render no necesita una carpeta llamada `/dockerfile`. Lo que
> necesita es la **ruta** a un `Dockerfile` dentro del repositorio. En este
> proyecto está en `backend/Dockerfile` y su contexto de build es `backend/`.

---

## 1. Qué se agregó al proyecto

| Archivo | Para qué sirve |
| --- | --- |
| `backend/Dockerfile` | Build multi-etapa: Maven + JDK 21 para compilar, JRE 21 para ejecutar. |
| `backend/.dockerignore` | Evita enviar `target/`, `.git`, IDE y tests al contexto de Docker. |
| `render.yaml` | Blueprint opcional: crea el servicio web y la base de datos de Render. |
| `application.yaml` → `server.port: ${PORT:8080}` | Render inyecta `PORT` (por defecto **10000**). |
| `application.yaml` → datasource por variables de entorno | `DB_URL` o `DB_HOST`/`DB_PORT`/`DB_NAME` + credenciales. |
| `RenderDatabaseEnvironmentPostProcessor` | Convierte `DATABASE_URL` de Render (`postgresql://...`) a una URL JDBC válida. |
| `spring-boot-starter-flyway` en `pom.xml` | En Spring Boot 4 la autoconfiguración de Flyway ya no viene con `flyway-core`. Sin esto las migraciones **no se ejecutan**. |
| `KAFKA_LISTENERS_AUTO_STARTUP` | Permite arrancar sin broker Kafka. |
| `KAFKA_PRODUCER_*` | Timeouts cortos del productor para que el outbox no bloquee 60 s si Kafka no responde. |
| `SecurityConfig` | HTTP Basic, sesión sin estado, CSRF desactivado y roles `DIRECCION`/`MONITOR`. |
| `OUTBOX_RELAY_ENABLED` | Permite desactivar el relay del outbox sin Kafka. |
| `EXTERNAL_VALIDATION_ENABLED` | Permite desactivar las llamadas a los módulos 2 y 3. |

### ¿Por qué no se usa `${DATABASE_URL}` directamente?

Render entrega la URL así:

```
postgresql://usuario:clave@host:5432/base
```

El driver JDBC de PostgreSQL **no acepta** ese esquema; necesita:

```
jdbc:postgresql://host:5432/base
```

Por eso el `EnvironmentPostProcessor` la traduce automáticamente. Si prefieres
evitar la conversión, define `DB_URL` ya en formato JDBC (tiene prioridad).

---

## 2. Requisitos previos

1. El repositorio debe estar en GitHub/GitLab/Bitbucket y conectado a Render.
2. La rama que se despliegue **debe compilar**. Render ejecuta
   `mvn package` dentro del Dockerfile; si el código no compila, el build falla.
3. Flujo Gitflow del proyecto: trabajar en `feature/*`, PR hacia `develop`.
   No hacer push directo a `main` ni a `develop`.

> **Estado actual:** el backend de `feature/application` compila correctamente y
> la suite pasa con `./mvnw clean test` (157 tests). Incluye Flyway, outbox,
> seguridad HTTP Basic con roles, corrección de lectura tras escritura y
> Dockerfile listo para Render. Antes de desplegar, verifica siempre que la rama
> seleccionada pase tests y build.

---

## 3. Opción A — Blueprint (recomendada)

El archivo `render.yaml` ya está en la raíz del repositorio.

1. En Render: **New → Blueprint**.
2. Conecta el repositorio y selecciona la rama a desplegar (`develop` cuando
   esté listo el merge).
3. Render crea:
   - Base de datos PostgreSQL gratuita `gestion-activos-db`.
   - Servicio web Docker `gestion-activos-m1`.
4. Cuando lo solicite, define:
   - `SECURITY_USER`
   - `SECURITY_PASSWORD`
5. Espera el primer build (puede tardar varios minutos).
6. Abre `https://<nombre>.onrender.com`.

Las variables de base de datos (`DB_HOST`, `DB_PORT`, `DB_NAME`,
`DB_USERNAME`, `DB_PASSWORD`) se inyectan automáticamente desde la base de
datos. Kafka y los módulos externos quedan desactivados.

---

## 4. Opción B — Configuración manual en el Dashboard

### 4.1 Crear la base de datos

1. **New → Postgres**.
2. Plan **Free**, misma región que el servicio web (por defecto `oregon`).
3. Define nombre, base y usuario (o deja los generados).
4. Copia los datos de conexión desde **Connect**:
   - Host interno, puerto, base, usuario y contraseña, **o**
   - La *Internal Database URL* (`postgresql://...`).

### 4.2 Crear el servicio web

1. **New → Web Service** y conecta el repositorio.
2. Configura:

| Campo | Valor |
| --- | --- |
| Branch | `develop` (o la rama que corresponda) |
| Language / Runtime | **Docker** |
| Dockerfile Path | `backend/Dockerfile` |
| Docker Build Context Directory | `backend` |
| Plan | **Free** |

> Alternativa: si usas **Root Directory = `backend`**, entonces el
> *Dockerfile Path* debe ser `Dockerfile`. No existe ninguna carpeta
> obligatoria llamada `dockerfile`.

3. **No definas el puerto manualmente.** Render asigna `PORT` (por defecto
   10000) y la aplicación ya lo lee.
4. **Health Check Path:** déjalo vacío. La API está protegida con HTTP Basic y
   un health check a una ruta protegida devolvería `401`, por lo que Render
   podría marcar el despliegue como fallido. Sin ruta configurada, Render
   detecta el puerto abierto.

### 4.3 Variables de entorno del servicio

**Base de datos (opción 1, recomendada):**

```
DB_HOST=<host interno>
DB_PORT=5432
DB_NAME=<nombre de la base>
DB_USERNAME=<usuario>
DB_PASSWORD=<contraseña>
```

**Base de datos (opción 2):**

```
DATABASE_URL=postgresql://usuario:clave@host:5432/base
```

La aplicación la convierte a JDBC automáticamente. Si usas `DB_URL` en lugar
de `DATABASE_URL`, debe empezar por `jdbc:postgresql://`.

**Desactivar dependencias no disponibles en el plan gratuito:**

```
KAFKA_LISTENERS_AUTO_STARTUP=false
OUTBOX_RELAY_ENABLED=false
EXTERNAL_VALIDATION_ENABLED=false
```

**Seguridad (cambia el admin/admin por defecto):**

```
SECURITY_USER=<usuario>
SECURITY_PASSWORD=<contraseña fuerte>
```

> `SECURITY_USER` tiene el rol **DIRECCION**: puede consultar, crear
> activos/espacios y cambiar estados. Si necesitas un usuario de solo lectura,
> define además `SECURITY_MONITOR_USER` y `SECURITY_MONITOR_PASSWORD` (rol
> **MONITOR**). Los orígenes permitidos para CORS se ajustan con
> `CORS_ALLOWED_ORIGINS` (por defecto `*`).

> Opcional: `JAVA_OPTS` ya se define en el Dockerfile con
> `-XX:MaxRAMPercentage=75` para no exceder los 512 MB del plan gratuito.

---

## 5. Verificación

La API usa HTTP Basic. Ejemplo:

```bash
curl -u "$SECURITY_USER:$SECURITY_PASSWORD" \
     https://<nombre>.onrender.com/api/v1/recursos
```

Respuestas esperadas:

| Código | Significado |
| --- | --- |
| `200` | Servicio y base de datos operativos. |
| `401` | Credenciales ausentes o incorrectas (la app está viva). |
| `500` | Revisar logs: normalmente migraciones o conexión a BD. |
| `502` | El contenedor no arrancó; revisar *Logs* en Render. |

En los logs debe aparecer:

```
Flyway ... Successfully applied N migrations
Tomcat started on port 10000 (http)
Started GestionDeActivosModulo1Application
```

---

## 6. Prueba local del contenedor

```bash
cd backend
docker build -t gestion-activos-m1 .

docker run --rm -p 10000:10000 \
  -e PORT=10000 \
  -e DB_URL=jdbc:postgresql://host:5432/base \
  -e DB_USERNAME=usuario \
  -e DB_PASSWORD=clave \
  -e KAFKA_LISTENERS_AUTO_STARTUP=false \
  -e OUTBOX_RELAY_ENABLED=false \
  -e EXTERNAL_VALIDATION_ENABLED=false \
  gestion-activos-m1
```

---

## 7. Limitaciones del plan gratuito de Render

- El servicio web **se duerme tras 15 minutos sin tráfico**; la primera
  petición puede tardar ~1 minuto en despertarlo.
- La base PostgreSQL gratuita **expira a los 30 días** de creación y tiene
  **1 GB** de almacenamiento. No es apta para producción.
- Se otorgan **750 horas de instancia** por mes.
- No hay Kafka incluido. Por eso el despliegue desactiva listeners y outbox.
  Si más adelante se contrata un broker, basta con definir
  `KAFKA_BOOTSTRAP_SERVERS` y volver a poner los flags en `true`.
- No hay acceso SSH ni discos persistentes.
- Render puede reiniciar el servicio en cualquier momento.

---

## 8. Problemas comunes

| Síntoma | Causa probable | Solución |
| --- | --- | --- |
| `Driver ... claims to not accept jdbcUrl, postgresql://...` | Se configuró la URL de Render en `DB_URL` sin convertirla. | Usar `DATABASE_URL` (se convierte sola) o `DB_URL` con prefijo `jdbc:`. |
| `Schema validation: missing table [activo]` | Flyway no se ejecutó. | Verificar que el `pom.xml` tenga `spring-boot-starter-flyway` (ya corregido). |
| El deploy falla por health check | Health Check Path apunta a una ruta con `401`. | Dejar el campo vacío. |
| Build falla con errores de compilación | La rama no compila. | Corregir y mergear a `develop` antes de desplegar. |
| `OutOfMemoryError` | Heap mayor a 512 MB. | Mantener `-XX:MaxRAMPercentage=75` y no subir el pool de conexiones. |
| La app arranca pero logs llenos de `Connection to node -1 could not be established` | Listeners de Kafka activos. | `KAFKA_LISTENERS_AUTO_STARTUP=false`. |
| Timeout al crear/actualizar recursos | Validación externa intentando llegar a los módulos 2 y 3. | `EXTERNAL_VALIDATION_ENABLED=false`. |
| `401` en `POST`/`PATCH` | Falta configuración de seguridad o CSRF activo. | La rama ya incluye `SecurityConfig`; usar HTTP Basic con las credenciales configuradas. |
| `404` justo después de crear un recurso | Lectura CQRS antes del flush del INSERT. | Ya corregido con `saveAndFlush` en los repositorios de escritura. |

---

## 9. Orden recomendado de trabajo

1. Ejecutar `cd backend && ./mvnw clean test` y confirmar que pasa (157 tests).
2. Abrir PR de `feature/application` → `develop` y mergear.
3. Crear el Blueprint o el Web Service en Render apuntando a `develop`.
4. Configurar `SECURITY_USER`/`SECURITY_PASSWORD`.
5. Verificar `/api/v1/recursos` con `curl`.
