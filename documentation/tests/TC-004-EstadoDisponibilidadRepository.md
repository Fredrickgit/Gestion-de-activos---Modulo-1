### Test Case Specification: EstadoDisponibilidadRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-004-EstadoDisponibilidadRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio del catálogo de estados de disponibilidad persiste y consulta sus registros correctamente, garantizando la unicidad del valor de disponibilidad y la integridad referencial usada por activos y espacios, bajo PostgreSQL real con Testcontainers.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| EstadoDisponibilidadRepository | Domain Repository Interface (Port) | [EstadoDisponibilidadRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/EstadoDisponibilidadRepository.java) |
| EstadoDisponibilidad | Domain Entity | [EstadoDisponibilidad.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/EstadoDisponibilidad.java) |
| EstadoDisponibilidadRecurso | Domain Enum | [EstadoDisponibilidadRecurso.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/enums/EstadoDisponibilidadRecurso.java) |
| EstadoDisponibilidadRepositoryIT (planificado) | Automated Test (Testcontainers) | [EstadoDisponibilidadRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/EstadoDisponibilidadRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar).
* No se usa Mockito: la persistencia se prueba contra BD real.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Catálogo sin FKs salientes: puede ejecutarse en paralelo con TC-001, TC-002 y TC-003.
* Los valores del enum `EstadoDisponibilidadRecurso` son `DISPONIBLE`, `EN_USO`, `EN_MANTENIMIENTO`, `RESERVADO` y `BLOQUEO_ACADEMICO`.
* `DISPONIBLE` es el estado inicial por defecto de todo recurso (FR de creación).
* Cada test corre aislado (limpieza de tabla o rollback de transacción).

##### Preconditions & Default Setup
* **Initial State** : Tabla `estado_disponibilidad` vacía
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar entidad nueva sin id | `estadoDisponibilidadRepository.save(entidad)` con `id = null` | disponibilidadRecurso=`DISPONIBLE`, descripcion="Listo para uso" | Retorna la entidad con `id` UUID v4 generado por la BD; fila persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `estadoDisponibilidadRepository.findById(id)` | id del paso 1 | `Optional` presente con los campos correctos | [ ] | [ ] |
| 3 | Buscar por id inexistente | `estadoDisponibilidadRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()` | [ ] | [ ] |
| 4 | Actualizar entidad existente | `save` con el `id` del paso 1 | descripcion="Disponible para préstamo" | Se actualiza la misma fila; `findAll().size()` no cambia | [ ] | [ ] |
| 5 | Listar todas | `estadoDisponibilidadRepository.findAll()` tras guardar `EN_USO` | disponibilidadRecurso=`EN_USO` | Lista de tamaño 2 con ambos estados | [ ] | [ ] |
| 6 | Verificar existencia | `estadoDisponibilidadRepository.existsById(id)` | id existente y UUID aleatorio | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Eliminar por id | `deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por disponibilidad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por disponibilidad existente | `findByDisponibilidadRecurso(estado)` | `EN_MANTENIMIENTO` | `Optional` presente con el registro correcto | [ ] | [ ] |
| 2 | Buscar por disponibilidad inexistente | `findByDisponibilidadRecurso(estado)` | `RESERVADO` sin registro | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por disponibilidad | `existsByDisponibilidadRecurso(estado)` | `EN_MANTENIMIENTO` y `RESERVADO` | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Recorrer todos los valores del enum | `save` + `findByDisponibilidadRecurso` para cada valor | `DISPONIBLE`, `EN_USO`, `EN_MANTENIMIENTO`, `RESERVADO`, `BLOQUEO_ACADEMICO` | Cada valor se persiste y recupera sin pérdida | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar disponibilidad duplicada | `save(entidad)` | disponibilidadRecurso=`DISPONIBLE` (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta | [ ] | UNIQUE en `disponibilidad_recurso` |
| 2 | Guardar disponibilidad nula | `save(entidad)` | disponibilidadRecurso=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar estado referenciado | `deleteById(id)` | Estado usado en `Activo.disponibilidadId` o `Espacio.disponibilidadId` | Lanza violación de FK; el estado sigue existiendo | [ ] | Regla pendiente: bloquear vs. cascada |

--------------------------------------------------------------------------------

##### Section 4: Caracteres especiales y longitud
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Descripción con tildes y eñes | `save` y `findById` | "En mantenimiento preventivo" | Se recupera idéntica (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Descripción tipo inyección SQL | `save` y `findById` | '; DROP TABLE estado_disponibilidad;-- | Se guarda como texto literal; la tabla sigue intacta | [ ] | Consultas parametrizadas |
| 3 | Descripción más larga que el límite | `save` | 256+ caracteres (límite a definir) | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : ¿El enum se almacena como `String` o como ordinal? Debe definirse y validarse que un valor desconocido en BD falle de forma explícita.
* **EC-002** : Dos `save` concurrentes con el mismo estado: solo uno debe persistir.
* **EC-003** : Debe existir exactamente un registro `DISPONIBLE` que sirva de estado inicial por defecto.
* **EC-004** : `deleteById` con un id inexistente: definir si es no-op o lanza excepción.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
