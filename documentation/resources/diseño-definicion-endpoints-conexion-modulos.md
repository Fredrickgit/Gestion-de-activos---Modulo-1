# Diseño y Definición de Endpoints de la API para Conexión de Módulos — Módulo 1 (M1)

Para permitir la integración síncrona y segura con los demás módulos, el Módulo 1 expone una API REST bajo los principios de Clean Architecture y RESTful[cite: 1, 2].

## 1. Estándares Generales de Conexión Inter-módulos
* **Prefijo base:** `/api/v1/`[cite: 1, 2]
* **Formato de intercambio:** `application/json` en payloads de entrada y salida.
* **Autenticación:** Cabeceras de seguridad o API Keys internas de servicio a servicio.
* **Códigos de Estado HTTP:** Uso estricto de códigos estándar (`200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`, `500 Internal Server Error`).

---

## 2. Endpoints de Consulta y Validación (Consumidos por M2 y M3)

### A. Consultar Inventario y Atributos Generales
* **Método y Ruta:** `GET /api/v1/recursos`[cite: 1]
* **Descripción:** Permite a M2 y M3 listar y filtrar recursos por tipo, estado o facultad para procesos de validación[cite: 1].
* **Parámetros de consulta (Query Params):** `tipo`, `estado`, `facultad`, `page`, `size`[cite: 1].

### B. Obtener Detalle de un Recurso
* **Método y Ruta:** `GET /api/v1/recursos/{id}`[cite: 1]
* **Descripción:** Consulta la información maestra y atributos específicos de un activo o espacio requerido por otro módulo[cite: 1].

### C. Consultar Disponibilidad en Tiempo Real
* **Método y Ruta:** `GET /api/v1/recursos/{id}/disponibilidad`[cite: 1]
* **Descripción:** Endpoint crítico consultado frecuentemente (ej. por M2) antes de procesar una reserva para verificar si el recurso está libre u ocupado[cite: 1].

---

## 3. Endpoints de Registro y Operación

### A. Registro de Activos
* **Método y Ruta:** `POST /api/v1/activos`[cite: 1]
* **Descripción:** Permite dar de alta un nuevo activo físico en el sistema[cite: 1].

### B. Registro de Espacios
* **Método y Ruta:** `POST /api/v1/espacios`[cite: 1]
* **Descripción:** Permite registrar un nuevo espacio físico (laboratorios, aulas)[cite: 1].

---

## 4. Endpoints de Transición y Auditoría de Estado

### A. Actualización Manual de Estado
* **Método y Ruta:** `PATCH /api/v1/recursos/{id}/estado`[cite: 1]
* **Descripción:** Permite actualizar el estado del recurso y genera un registro automático en la bitácora de auditoría interna de M1[cite: 1].