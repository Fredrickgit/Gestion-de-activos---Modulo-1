# Comunicación Asíncrona basada en Eventos — Perspectiva del Módulo 1 (M1)

**Proyecto:** Gestión de Activos y Espacios Universitarios — Módulo 1
**Broker Tecnológico:** Apache Kafka 3.6
**Última actualización:** 02/10/2026
**Ámbito:** Comunicación asíncrona, transaccional y prioriazada entre M1, M2 y M3 mediante persistencia con Apache Kafka.

---

## 1. Propósito

**Citado de PLAN.md; Consumidor primario de eventos críticos (uso, daños) para transiciones de estado, productor secundario de cambios manuales y garante de idempotencia.**
Document con propósito de establecimiento del contrato técnico formal de las comunicaciones **asíncronas, confiables y con garantía de consistencia transaccional (ACID)** entre el Módulo 1 (M1) y los módulos M2 y M3. Este canal se utiliza exclusivamente para **eventos críticos del ciclo de vida de los recursos** donde no se admite pérdida de mensajes, se requiere tolerancia a fallos de red y debe asegurarse la trazabilidad y la no duplicidad.

---

## 2. Principios de Diseño aplicados

| Principio | Implementación Técnica en M1 / Kafka |
| :--- | :--- |
| **Persistencia Distribuida** | Los mensajes se conservan en disco en el clúster de Kafka según una política de retención de una semana (7 días), permitiendo recuperación ante caídas de cualquier módulo. | %%%
| **Garantía ACID Local** | Cada evento consumido se procesa en M1 dentro de una transacción atómica de base de datos en PostgreSQL (`@Transactional`); o se actualiza el estado del recurso, el historial y la tabla de deduplicación, o se revierte por completo. |
| **Entrega At-least-once** | Kafka garantiza la entrega al menos una vez. M1 implementará el patrón **Idempotent Consumer (Transactional Inbox)** para solucionar problemmaticas con duplicados. |
| **Prioridad** | Los eventos críticos (uso, daños) tienen mayor prioridad que los informativos. |
| **Orden Estricto por Recurso** | Se utilizará `recurso_id` como **Message Key** en Kafka. De esta forma, todos los eventos que afecten al mismo activo o espacio ingresan a la misma partición y se consumen en orden secuencial garantizado. |
| **Commit Manual de Offset** | M1 confirma el desplazamiento (*commit offset*) únicamente **después** de haber completado exitosamente la transacción ACID en PostgreSQL. |
| **Dead Letter Queue / Dead Letter Topic (DLQ)** | Todo mensaje que falle tras la política de reintentos configurada se desvía a un tópico de descarte (`.DLT`) con cabeceras de diagnóstico para su inspección técnica. |

---

## 3. Rol del Módulo 1 frente a M2 y M3

Desde la perspectiva de M1, este módulo es el **consumidor ACID principal** de los eventos críticos que M2 y M3 generan durante la operación del sistema. M1 traduce esos eventos en **transiciones de estado** sobre los recursos que custodia, garantizando atomicidad, auditoría y consistencia. Ocasionalmente, M1 también **produce** eventos cuando la Dirección Universitaria cambia un estado manualmente, para que M2 y M3 mantengan su información sincronizada.

| Rol de M1 | Descripción |
| :--- | :--- |
| **Consumidor Primario (Transiciones Físicas)** | M1 escucha eventos originados en M2 y M3. Al recibirlos, valida la máquina de estados física y actualiza la disponibilidad del recurso en su base de datos. |
| **Productor Secundario (Cambios Manuales)** | Si la Dirección Universitaria o Monitor cambian manualmente un recurso desde la API REST de M1 (ej. enviar equipo a mantenimiento), M1 produce el evento `estado.actualizado` para que M2 y M3 ajusten sus calendarios y analítica. |
| **Garante de Idempotencia y Consistencia** | M1 asegura que ningún evento repetido afecte los registros mediante KAFKA impelementando Idempotent Consumer |
| **NO es Responsable de** | M1 **no** valida sanciones de usuarios, **no** valida cupos ni límites de reservas, **no** calcula cobros por daños. Esas decisiones pertenecen a M2 y M3. |
---

## 4. Topología de Apache Kafka (Tópicos, Claves y Particiones)

### 4.1. Definición de Tópicos

| Nombre del Tópico | Dirección | Productor | Consumidor | Key del Mensaje | Particiones Sugeridas %%% | Propósito |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `unimag.m2.reservas-eventos.v1` @@@ | Inbound | Módulo 2 | M1 (`m1-activos-group`) | `recurso_id` (String) | 3 particiones %%% | Notificación de ocupación/inicio de reserva activa. |
| `unimag.m3.uso-novedades.v1` @@@ | Inbound | Módulo 3 | M1 (`m1-activos-group`) | `recurso_id` (String) | 3 particiones %%% | Check-out regular y reportes de averías o daños. |
| `unimag.m1.recursos-estados.v1` @@@ | Outbound | Módulo 1 | M2, M3 | `recurso_id` (String) | 3 particiones %%% | Cambios manuales de estado ejecutados directamente en M1. |
| `unimag.m1.inbox.DLT` | Interno | Módulo 1 | M1 (Admin / Alerting) | `recurso_id` (String) | 1 partición %%% | Almacén de eventos venenosos o fallidos tras reintentos. |

### 4.2. Especificaciones de Configuración de Kafka
*   **Consumer Group ID de M1:** `m1-activos-consumer-group` ### Aclarar identificador de Consumer Group en application.properties ###
*   **Formato de Serialización:** JSON (UTF-8).
*   **Estrategia de Particionamiento:** Basada en clave hash (`DefaultPartitioner` de Kafka con `recurso_id`). Esto asegura que todos los eventos que pertenezcan al mismo activo (ej. ID `104`) se encolen en la misma partición y se procesen de forma estrictamente secuencial.

---

## 5. Catálogo de Contratos y Esquemas de Mensajes (Eventos)

### 5.1. Estándar de Cabeceras (Kafka Headers)
Todos los mensajes producidos y consumidos a través de Kafka deben incluir las siguientes cabeceras estándar en el registro:

| Header Key | Tipo | Descripción | Ejemplo |
| :--- | :--- | :--- | :--- |
| `X-Correlation-Id` | String | Identificador único de trazabilidad distribuida para logs. | `corr-4b6e82c1-84ef-4b2a` |
| `X-Event-Type` | String | Nombre del tipo de evento (para ruteo en el consumidor). | `notificacion.uso` |
| `X-Source-Module` | String | Identificador del módulo emisor. | `MODULO_2` | %%%
| `X-Event-Version` | String | Versión del esquema del contrato. | `1.0` |

---

### 5.2. Eventos de Entrada (Inbound consumidos por M1)

#### A. Evento `notificacion.uso` (Origen: M2) 
*   **Disparador:** Un usuario inicia la ocupación de un espacio o retira físicamente el activo en ventanilla tras validación satisfactoria de M2.
*   **Acción en M1:** Transiciona el recurso al estado `EN_USO` y genera registro de historial.
*   **Tópico:** `unimag.m2.reservas-eventos.v1` %%%
*   **Message Key:** ID del recurso
*   **Esquema Payload JSON:**

#### B. Evento `reserva.finalizada` (Origen: M3) 
*   **Disparador:** Módulo 3 registra un "Check-out Exitoso" (entrega a tiempo y en óptimas condiciones físicas del activo, o desocupación de espacio).
*   **Acción en M1:** Transiciona el recurso al estado `DISPONIBLE` y genera registro de auditoría.
*   **Tópico:** `unimag.m3.uso-novedades.v1` %%%
*   **Message Key:** ID del recurso
*   **Esquema Payload JSON:**


#### C. Evento `reporte.danos` (Origen: M3) 
*   **Disparador:** Módulo 3 registra una novedad técnica o daño físico durante la entrega o inspección técnica.
*   **Acción en M1:** Transiciona la disponibilidad del recurso a `EN_MANTENIMIENTO`, actualiza el `id_estado_fisico` a `MAL_ESTADO` o `DAÑADO` y registra la novedad en `Historial_Recurso`.
*   **Tópico:** `unimag.m3.uso-novedades.v1` %%%
*   **Message Key:** ID del recurso
*   **Esquema Payload JSON:**

#### D. Evento `bloqueo.academico` (Origen: M2)
*   **Disparador:** M2 importa la carga académica semestral fija y notifica qué espacios quedan apartados para clases regulares.
*   **Acción en M1:** Transiciona el recurso al estado `EN_USO` y genera registro de historial. %%%
*   **Tópico:** `unimag.m2.reservas-eventos.v1` %%%
*   **Message Key:** ID del recurso
*   **Esquema Payload JSON:**

---

### 5.3. Eventos de Salida (Outbound producidos por M1)

#### Evento `estado.actualizado` (M1 ➔ M2 y M3) 
*   **Disparador:** Dirección Universitaria o Monitor cambian manualmente el estado de un recurso a través de la API REST (`PATCH /api/v1/recursos/{id}/estado`).
*   **Propósito:** Que M2 inhabilite de inmediato reservas sobre ese recurso.
*   **Tópico:** `unimag.m1.recursos-estados.v1`
*   **Message Key:** ID del recurso
*   **Esquema Payload JSON:**


---

## 6. Garantía Transaccional ACID, Idempotencia y Commit de Offsets

### 6.1. Patrón Idempotent Consumer (Tabla de Deduplicación en PostgreSQL)

Para evitar que mensajes duplicados reenviados por Kafka corrompan el estado del inventario, M1 mantiene una tabla EVENTOS PROCESADOS dedicada dentro del esquema relacional.


### 6.2. Algoritmo Transaccional de Consumo (Paso a Paso)

```text
Mensaje recibido desde Kafka Partition
   │
   ▼
[1] Iniciar Transacción DB (@Transactional en Spring Boot)
   │
   ├─► [2] SELECT evento_id FROM Eventos_Procesados WHERE evento_id = :evento_id FOR UPDATE;
   │      │
   │      ├─► ¿Existe ya en DB? (Duplicado)
   │      │      └─► Registrar advertencia en logs.
   │      │      └─► Commit DB y Acknowledge Kafka (Ignorar sin reprocesar).
   │      │
   │      └─► NO existe (Primer intento legítimo):
   │             │
   │             ├─► [3] SELECT * FROM Activos WHERE id_activo = :recurso_id FOR UPDATE;
   │             │       (Bloqueo pesimista de fila para evitar condiciones de carrera)
   │             │
   │             ├─► [4] Validar máquina de estados:
   │             │       ¿Es válida la transición (ej. DISPONIBLE -> EN_USO)?
   │             │       - Si es inválida -> Lanzar excepción de negocio (enviar a DLQ).
   │             │
   │             ├─► [5] UPDATE Activos SET id_estado_de_disponibilidad = :nuevo_estado;
   │             │
   │             ├─► [6] INSERT INTO Historial_Recurso (recurso_id, fecha, usuario, ...);
   │             │
   │             └─► [7] INSERT INTO Eventos_Procesados (evento_id, ..., resultado = 'EXITOSO');
   │
   ▼
[8] COMMIT de la Transacción en PostgreSQL
   │
   ▼
[9] Acknowledge a Kafka (Commit Offset de la partición)
   │
Fin del ciclo
```

Si ocurre una falla antes del paso 8 (DB Commit), la transacción se revierte por completo (`ROLLBACK`), no se confirma el offset en Kafka y el mensaje se redestina a la política de reintentos.

---

## 7. Estrategia de Reintentos, Backoff y Dead Letter Queue (DLQ)

### 7.1. Clasificación de Fallos en Consumo
Kafka procesa reintentos distinguiendo dos categorías de error:

1.  **Errores Recuperables (Transitorios):**
    *   *Ejemplos:* Pérdida momentánea de conexión con PostgreSQL, timeout de socket, bloqueo temporal de tabla.
    *   *Acción:* **Reintento automático** con retroceso exponencial (*exponential backoff*).
2.  **Errores No Recuperables (Mensajes Venenosos):**
    *   *Ejemplos:* JSON con estructura sintáctica corrupta, violación de tipos de datos, recurso ID inexistente en la BD de M1, transición no permitida por la máquina de estados.
    *   *Acción:* **No reintentar**. Se enruta inmediatamente a `unimag.m1.inbox.DLT` para no bloquear el consumo de la partición.

### 7.2. Política de Reintentos y Parámetros
*   **Número de Reintentos Máximos:** 3 intentos. 
*   **Intervalo Inicial de Espera:** 1.000 ms (1 segundo). 
*   **Multiplicador de Backoff:** 2.0 (Intento 1: 1s, Intento 2: 2s, Intento 3: 4s). 
*   **Manejador de Error en Spring:** `DefaultErrorHandler` con `DeadLetterPublishingRecoverer`. ##

### 7.3. Esquema del Registro enviado a la DLT
Cuando un mensaje se desvía a `unimag.m1.inbox.DLT`, Kafka deberá añadir cabeceras técnicas adicionales con el motivo del fallo:

| Kafka DLT Header | Valor / Contenido |
| :--- | :--- |
| `kafka_dlt-original-topic` | Nombre del tópico origen (ej. `unimag.m2.reservas-eventos.v1`). |
| `kafka_dlt-original-partition` | Número de partición original. |
| `kafka_dlt-original-offset` | Offset en el que falló el mensaje. |
| `kafka_dlt-exception-message` | Traza del error (ej. `RecursoNoEncontradoException: ID 999`). |
| `kafka_dlt-exception-stacktrace` | Stacktrace completo de la excepción. |

---

## 8. Priorización en Apache Kafka %%%%%%%

Kafka no cuenta con un atributo nativo numérico de prioridad (`x-max-priority`) por cola. Para satisfacer los requerimientos del sistema donde las novedades por daños técnicos deben procesarse con urgencia frente a eventos informativos ordinarios, se define la siguiente opción:

### Tópicos de Alta Prioridad Separados
*   El consumidor de M1 asigna mayor concurrencia de hilos (*concurrency threads*) al listener de los tópico importantes para procesar las novedades inmediatamente sin quedar detrás de ráfagas de reservas. Los tópicos informativos no tendrían esa asignación.


---

## 9. Trazabilidad con Auditoría del Negocio

Cada mensaje que transiciona un estado en M1 impacta directamente la entidad `Historial_Recurso` definida en [Diccionario.md]
| Campo en `Historial_Recurso` | Origen del Dato en el Evento Kafka |
| :--- | :--- |
| `id_recurso` | `datos.recurso_id` |
| `estado_anterior` | Estado leído en la BD de M1 antes del update |
| `estado_nuevo` | Estado destino (`EN_USO`, `DISPONIBLE`, `EN_MANTENIMIENTO`) |
| `usuario_responsable` |  |
| `fecha_evento` | `timestamp` del evento |
| `motivo` | `tipo_evento` |
