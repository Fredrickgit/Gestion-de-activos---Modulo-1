# Comunicación Síncrona basada en API REST — Perspectiva del Módulo 1 (M1)

**Proyecto:** Gestión de Activos y Espacios Universitarios — Módulo 1  
**Tecnología:** HTTP/1.1 RESTful API over JSON  
**Última actualización:** 02/10/2026  
**Ámbito:** Comunicación síncrona, ligera y sin estado (*stateless*) entre el Módulo 1 (M1) y los módulos M2, M3 y usuarios autorizados.

---

## 1. Propósito

**Citado de PLAN.md; Servidor principal de inventario (autoridad de activos, espacios y estados) y cliente ocasional para validaciones cruzadas.**  
Este documento establece el contrato técnico formal de las comunicaciones **síncronas, eficientes y desacopladas** entre el Módulo 1 (M1) y los demás componentes del sistema. Este canal se utiliza exclusivamente para **consultas de catálogo, disponibilidad, operaciones de lectura/escritura simples y administración de inventario** donde no se requiere persistencia de eventos distribuidos complejos.

---

## 2. Principios de Diseño REST Aplicados

| Principio | Implementación Técnica en M1 |
| :--- | :--- |
| **Stateless (Sin Estado)** | Cada petición HTTP contiene toda la información de autenticación y contexto necesaria; M1 no almacena ninguna sesión del cliente entre llamadas. |
| **Cliente-Servidor** | M1 actúa como servidor autoritativo de recursos; M2, M3 y las interfaces de usuario actúan como clientes. |
| **Cacheable** | Las respuestas de consultas masivas o catálogos (`GET`) incluyen cabeceras `Cache-Control` cuando aplica para optimizar rendimiento. |
| **Interfaz Uniforme** | Uso estricto de métodos HTTP estándar (`GET`, `POST`, `PATCH`) y URIs semánticas que representan recursos normalizados. |
| **Sistema en Capas** | M1 aísla su lógica interna mediante Clean Architecture, permitiendo delegar a otros servicios sin que los clientes lo noten. |
| **Versionado de API** | Toda ruta se expone obligatoriamente bajo el prefijo `/api/v1/` para garantizar evolución sin romper contratos existentes |

---

## 3. Rol del Módulo 1 frente a M2 y M3

Desde la perspectiva de M1, este módulo es la **autoridad absoluta del inventario físico**. Su función principal es exponer los datos maestros de activos y espacios. Ocasionalmente, M1 actúa como cliente REST para validaciones puntuales de solo lectura.

| Rol de M1 | Descripción |
| :--- | :--- |
| **Servidor REST Principal (Autoridad)** | M1 expone catálogos, consultas de disponibilidad, registros de activos/espacios y actualizaciones manuales de estado. Es la fuente de verdad de los atributos y ciclos de vida (`Disponible`, `En Uso`, `En Mantenimiento`). |
| **Cliente REST Ocasional (Validación Cruzada)** | Antes de permitir ciertas transiciones críticas, M1 realiza consultas puntuales y de *timeout* corto hacia M2 o M3 para validar dependencias externas de lectura. |
| **Garante de Seguridad y Paginación** | M1 intercepta y valida roles estrictos (Dirección Universitaria y Monitores), aplicando paginación fija obligatoria de 20 elementos por página y sanitización contra inyecciones. |
| **NO es Responsable de** | M1 **no** valida sanciones de usuarios, **no** gestiona reservas y **no** aplica penalizaciones. Esas responsabilidades pertenecen exclusivamente a M2 y M3. |

---

## 4. Especificaciones Técnicas y Estándares de la API

*   **Prefijo Base:** `/api/v1/`
*   **Formato de Intercambio:** JSON (`application/json`) estrictamente tipado para requests y responses.
*   **Paginación Estandarizada:** Todas las consultas masivas soportan de forma predeterminada bloques de `size = 20` elementos por página, con controles de navegación y reseteo automático a la página 1 ante aplicación de filtros.
*   **Sanitización y Seguridad:** Validación estricta de parámetros de entrada para prevenir ataques de inyección SQL y XSS, además de restricción por cabeceras de autorización de roles.

---

## 5. Catálogo de Contratos y Endpoints Síncronos

### A. Catálogo, Consultas e Inventarios (US0, US2, US4)

#### 1. Listado paginado de recursos
*   **Método y Ruta:** `GET /api/v1/recursos`
*   **Propósito:** Permite filtrar el inventario o generar reportes institucionales según tipo, estado actual o facultad.
*   **Query Parameters:**
    *   `tipo` (opcional): `activo` | `espacio`
    *   `estado` (opcional): `DISPONIBLE` | `EN_USO` | `EN_MANTENIMIENTO`
    *   `facultad` (opcional): ID o nombre de la facultad
    *   `page` (opcional, por defecto `0`)
    *   `size` (opcional, por defecto `20`)
*   **Respuesta Exitosa (`200 OK`):**
    ```json
    {
      "content": [
        {
          "id": "uuid-recurso-123",
          "codigo": "ACT-FI-001",
          "nombre": "Videoabinador Epson Pro",
          "tipo": "ACTIVO",
          "ubicacionFisica": "Laboratorio 1",
          "aforoCapacidad": "N/A",
          "estado": "DISPONIBLE",
          "facultad": "Ingeniería",
          "fechaRegistro": "2026-09-26T10:00:00Z"
        }
      ],
      "page": 0,
      "size": 20,
      "totalElements": 1
    }
    ```

#### 2. Detalle completo de un recurso
*   **Método y Ruta:** `GET /api/v1/recursos/{id}`
*   **Propósito:** Obtener la información maestra, metadatos y atributos específicos de un activo o espacio mediante su identificador único.
*   **Path Parameters:** `id` (UUID del recurso).

#### 3. Consultar disponibilidad actual (US0)
*   **Método y Ruta:** `GET /api/v1/recursos/{id}/disponibilidad`
*   **Propósito:** Consultar en tiempo real si un recurso se encuentra disponible o el motivo específico de su inhabilitación (consultado frecuentemente por M2).
*   **Respuesta Exitosa (`200 OK`):**
    ```json
    {
      "id": "uuid-recurso-123",
      "disponible": true,
      "estadoActual": "DISPONIBLE",
      "horarioOcupacion": null,
      "motivoNoDisponibilidad": null
    }
    ```

---

### B. Registro de Nuevos Recursos (US1)

#### 4. Registrar un nuevo activo físico
*   **Método y Ruta:** `POST /api/v1/activos`
*   **Restricción de Rol:** Exclusivo para usuarios con rol de **Dirección Universitaria**.
*   **Payload Request Body:**
    ```json
    {
      "nombre": "Microscopio Óptico Binocular",
      "serial": "MIC-998877",
      "facultad": "Ciencias Básicas",
      "estadoFisico": "NUEVO",
      "ubicacion": "uuid-espacio-456"
    }
    ```
*   **Respuesta Exitosa (`201 Created`):** Retorna el recurso creado con su ID generado y estado inicial automático en `DISPONIBLE`.

#### 5. Registrar un nuevo espacio físico
*   **Método y Ruta:** `POST /api/v1/espacios`
*   **Restricción de Rol:** Exclusivo para usuarios con rol de **Dirección Universitaria**.
*   **Payload Request Body:**
    ```json
    {
      "nombre": "Laboratorio de Redes 2",
      "edificio": "Bienestar Universitario",
      "aforoMaximo": 30,
      "facultad": "Ingeniería"
    }
    ```
*   **Respuesta Exitosa (`201 Created`):** Retorna los datos del espacio registrado.

---

### C. Actualización de Estados (US3)

#### 6. Actualización manual del estado de un recurso
*   **Método y Ruta:** `PATCH /api/v1/recursos/{id}/estado`
*   **Propósito:** Permitir a la administración modificar de forma manual el estado del recurso (ej. envío a mantenimiento), generando automáticamente un registro de auditoría.
*   **Payload Request Body:**
    ```json
    {
      "nuevoEstado": "EN_MANTENIMIENTO",
      "observacion": "Daño reportado en puerto de red principal",
      "usuarioResponsable": "admin.infraestructura"
    }
    ```
*   **Respuesta Exitosa (`200 OK`):** Devuelve el recurso actualizado con su respectiva traza de auditoría.

---

## 6. Códigos de Estado HTTP y Manejo de Errores

M1 estandariza las respuestas de error mediante códigos HTTP claros acompañados de un cuerpo JSON descriptivo:

| Código HTTP | Significado | Escenario de Uso en M1 |
| :--- | :--- | :--- |
| **200 OK** | Éxito | Consultas de catálogos, detalles y actualizaciones completadas. |
| **201 Created** | Creado | Registro exitoso de nuevos activos o espacios. |
| **400 Bad Request** | Solicitud Incorrecta | Fallas de validación de campos obligatorios, formatos de fecha inválidos o aforo negativo. |
| **401 Unauthorized** | No Autenticado | Ausencia o invalidez de credenciales de seguridad en la cabecera. |
| **403 Forbidden** | Acceso Denegado | Intento de ejecución de endpoints protegidos (como creación o modificación) por perfiles sin privilegios de Dirección Universitaria. |
| **404 Not Found** | No Encontrado | Consulta de un ID de recurso o endpoint inexistente. |
| **500 Internal Server Error** | Error de Servidor | Fallas inesperadas en infraestructura o base de datos durante la transacción. |
