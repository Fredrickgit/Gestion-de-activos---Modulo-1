### Test Case Specification: EstadoActivoRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-003-EstadoActivoRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio del catálogo de estados físicos de activos persiste y consulta sus registros de forma correcta, garantizando la unicidad del valor de estado y la integridad referencial que usan los activos, bajo PostgreSQL real con Testcontainers.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA4 | Specification Document | [SPEC-HISTORIA4.md](../../specs/SPEC-HISTORIA4.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| EstadoActivoRepository | Domain Repository Interface (Port) | [EstadoActivoRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/EstadoActivoRepository.java) |
| EstadoActivo | Domain Entity | [EstadoActivo.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/EstadoActivo.java) |
| EstadoFisicoActivo | Domain Enum | [EstadoFisicoActivo.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/enums/EstadoFisicoActivo.java) |
| EstadoActivoRepositoryIT (planificado) | Automated Test (Testcontainers) | [EstadoActivoRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/EstadoActivoRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar).
* No se usa Mockito: la persistencia se prueba contra BD real.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Catálogo sin FKs salientes: puede ejecutarse en paralelo con TC-001 y TC-002.
* Los valores del enum `EstadoFisicoActivo` son `NUEVO`, `OPTIMO`, `MAL_ESTADO` y `AVERIADO`.
* Cada test corre aislado (limpieza de tabla o rollback de transacción).

##### Preconditions & Default Setup
* **Initial State** : Tabla `estado_activo` vacía
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar entidad nueva sin id | `estadoActivoRepository.save(entidad)` con `id = null` | estado=`NUEVO`, descripcion="Activo recién adquirido" | Retorna la entidad con `id` UUID v4 generado por la BD; fila persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `estadoActivoRepository.findById(id)` | id del paso 1 | `Optional` presente con `estado` y `descripcion` correctos | [ ] | [ ] |
| 3 | Buscar por id inexistente | `estadoActivoRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()` | [ ] | [ ] |
| 4 | Actualizar entidad existente | `estadoActivoRepository.save(entidad)` con el `id` del paso 1 | descripcion="Equipo nuevo en inventario" | Se actualiza la misma fila; `findAll().size()` no cambia | [ ] | [ ] |
| 5 | Listar todas | `estadoActivoRepository.findAll()` tras guardar `OPTIMO` | estado=`OPTIMO` | Lista de tamaño 2 con ambos estados | [ ] | [ ] |
| 6 | Verificar existencia | `estadoActivoRepository.existsById(id)` | id existente y UUID aleatorio | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Eliminar por id | `estadoActivoRepository.deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por estado
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por estado existente | `estadoActivoRepository.findByEstado(estado)` | `EstadoFisicoActivo.AVERIADO` | `Optional` presente con el registro correcto | [ ] | [ ] |
| 2 | Buscar por estado inexistente | `estadoActivoRepository.findByEstado(estado)` | `EstadoFisicoActivo.MAL_ESTADO` sin registro | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por estado | `estadoActivoRepository.existsByEstado(estado)` | `AVERIADO` y `MAL_ESTADO` | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Recorrer todos los valores del enum | `save` + `findByEstado` para cada valor | `NUEVO`, `OPTIMO`, `MAL_ESTADO`, `AVERIADO` | Cada valor se persiste y recupera sin pérdida de información | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar estado duplicado | `estadoActivoRepository.save(entidad)` | estado=`NUEVO` (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta | [ ] | UNIQUE en `estado` |
| 2 | Guardar estado nulo | `estadoActivoRepository.save(entidad)` | estado=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar estado referenciado por un activo | `estadoActivoRepository.deleteById(id)` | Estado usado en `Activo.estadoFisicoId` | Lanza violación de FK; el estado sigue existiendo | [ ] | Regla pendiente: bloquear vs. cascada |

--------------------------------------------------------------------------------

##### Section 4: Caracteres especiales y longitud
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Descripción con tildes y eñes | `save` y `findById` | "Óptimo para uso académico" | Se recupera idéntica (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Descripción tipo inyección SQL | `save` y `findById` | '; DROP TABLE estado_activo;-- | Se guarda como texto literal; la tabla sigue intacta | [ ] | Consultas parametrizadas |
| 3 | Descripción más larga que el límite | `save` | 256+ caracteres (límite a definir) | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : ¿El enum se almacena como `String` o como ordinal? Debe definirse y validarse que un valor desconocido en BD falle de forma explícita.
* **EC-002** : Dos `save` concurrentes con el mismo estado: solo uno debe persistir.
* **EC-003** : `deleteById` con un id inexistente: definir si es no-op o lanza excepción.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
