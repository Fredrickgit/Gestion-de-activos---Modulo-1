### Test Case Specification: EstadoDisponibilidadRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-004-EstadoDisponibilidadRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Priority** : P1 - Critical

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio del catálogo de disponibilidad persiste y consulta los cinco estados usados por la máquina de estados (DISPONIBLE, EN_USO, EN_MANTENIMIENTO, RESERVADO, BLOQUEO_ACADEMICO), bajo las condiciones operativas especificadas: PostgreSQL real levantado con Testcontainers, UUID v4 generados por la base de datos y restricciones de integridad aplicadas por el esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | Modulos-gestion-de-activos.md |
| SPEC-HISTORIA0 | Specification Document | specs/SPEC-HISTORIA0.md |
| SPEC-HISTORIA3 | Specification Document | specs/SPEC-HISTORIA3.md |
| Implementation Plan | Plan Document | PLAN.md |
| EstadoDisponibilidadRepository | Domain Repository Interface (Port) | src/domain/repositories/EstadoDisponibilidadRepository.java |
| EstadoDisponibilidad | Domain Entity | src/domain/entities/EstadoDisponibilidad.java |
| EstadoDisponibilidadRepositoryIT | Automated Test (Testcontainers) | tests/integration/repositories/EstadoDisponibilidadRepositoryIT.java |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host (Windows/Linux/Mac) para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar; el plan solo indica PostgreSQL).
* No se usa Mockito: la persistencia se prueba contra BD real. Mockito queda para pruebas unitarias de casos de uso.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Se asume un registro por valor de `EstadoDisponibilidadRecurso`.
* El esquema se crea con PKs `UUID DEFAULT gen_random_uuid()` y extensión `pgcrypto` habilitada (T004 / T004b del plan).
* Cada test corre aislado: se limpian las tablas (o rollback de transacción) entre casos.

##### Preconditions & Default Setup
* **Initial State** : Base de datos con esquema creado; tabla de disponibilidad vacía (o con seed, según decisión de diseño)
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia, sin autenticación)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar entidad nueva sin id | `estadoDisponibilidadRepository.save(entidad)` con `id = null` | disponibilidadRecurso=DISPONIBLE, descripcion="Listo para reservar" | Retorna la entidad con `id` UUID v4 no nulo generado por la BD; la fila queda persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `estadoDisponibilidadRepository.findById(id)` | id obtenido en el paso 1 | `Optional` presente con todos los campos iguales a los guardados | [ ] | [ ] |
| 3 | Buscar por id inexistente | `estadoDisponibilidadRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()`, sin excepción | [ ] | [ ] |
| 4 | Actualizar entidad existente | `estadoDisponibilidadRepository.save(entidad)` con el `id` del paso 1 y un campo modificado | descripcion="Recurso libre" | Se actualiza la misma fila; no se crea una nueva (el conteo no cambia) | [ ] | [ ] |
| 5 | Listar todas | `estadoDisponibilidadRepository.findAll()` tras guardar una segunda entidad | disponibilidadRecurso=EN_USO | Lista de tamaño 2 con ambas entidades | [ ] | [ ] |
| 6 | Verificar existencia | `estadoDisponibilidadRepository.existsById(id)` | id existente y UUID aleatorio | `true` para el existente, `false` para el aleatorio | [ ] | [ ] |
| 7 | Eliminar por id | `estadoDisponibilidadRepository.deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por disponibilidad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por estado persistido | `estadoDisponibilidadRepository.findByDisponibilidadRecurso(valor)` | DISPONIBLE | `Optional` presente con `disponibilidadRecurso = DISPONIBLE` | [ ] | [ ] |
| 2 | Buscar por estado no persistido | `estadoDisponibilidadRepository.findByDisponibilidadRecurso(valor)` | RESERVADO sin guardar | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por estado | `estadoDisponibilidadRepository.existsByDisponibilidadRecurso(valor)` | DISPONIBLE y BLOQUEO_ACADEMICO sin guardar | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Recorrer todos los valores del enum | Para cada `EstadoDisponibilidadRecurso`: `save` y luego `findByDisponibilidadRecurso` | Los 5 valores del enum | Cada valor retorna exactamente un registro | [ ] | [ ] |
| 5 | Estado inicial de recursos nuevos | Con el catálogo completo, `findByDisponibilidadRecurso(DISPONIBLE)` | Catálogo con los 5 valores | Existe un único registro DISPONIBLE | [ ] | Historia 1 (FR-006): todo recurso nuevo nace DISPONIBLE |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar estado duplicado | `estadoDisponibilidadRepository.save(entidad)` | disponibilidadRecurso=DISPONIBLE (ya existe) | Lanza excepción de integridad (UNIQUE) | [ ] | [ ] |
| 2 | Guardar estado nulo | `estadoDisponibilidadRepository.save(entidad)` | disponibilidadRecurso=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar estado en uso | `estadoDisponibilidadRepository.deleteById(id)` | Estado referenciado por un Recurso | Lanza violación de FK; el estado sigue existiendo | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : El enum se almacena como STRING (no ORDINAL).
* **EC-002** : Si falta alguno de los 5 estados en el catálogo, la creación o transición de recursos fallaría: el seed debe ser obligatorio.
* **EC-003** : La historia 3 menciona el estado "Prestado", que no existe en el enum: confirmar si se agrega o es sinónimo de EN_USO.
* **EC-004** : Dos `save` concurrentes del mismo estado: solo uno debe persistir.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
