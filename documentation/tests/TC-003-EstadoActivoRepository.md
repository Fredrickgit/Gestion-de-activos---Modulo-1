### Test Case Specification: EstadoActivoRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-003-EstadoActivoRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio del catálogo de estados físicos persiste y consulta cada valor de `EstadoFisicoActivo` (NUEVO, OPTIMO, MAL_ESTADO, AVERIADO), bajo las condiciones operativas especificadas: PostgreSQL real levantado con Testcontainers, UUID v4 generados por la base de datos y restricciones de integridad aplicadas por el esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | Modulos-gestion-de-activos.md |
| SPEC-HISTORIA1 | Specification Document | specs/SPEC-HISTORIA1.md |
| Implementation Plan | Plan Document | PLAN.md |
| EstadoActivoRepository | Domain Repository Interface (Port) | src/domain/repositories/EstadoActivoRepository.java |
| EstadoActivo | Domain Entity | src/domain/entities/EstadoActivo.java |
| EstadoActivoRepositoryIT | Automated Test (Testcontainers) | tests/integration/repositories/EstadoActivoRepositoryIT.java |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host (Windows/Linux/Mac) para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar; el plan solo indica PostgreSQL).
* No se usa Mockito: la persistencia se prueba contra BD real. Mockito queda para pruebas unitarias de casos de uso.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Se asume un registro por valor del enum `EstadoFisicoActivo`.
* El esquema se crea con PKs `UUID DEFAULT gen_random_uuid()` y extensión `pgcrypto` habilitada (T004 / T004b del plan).
* Cada test corre aislado: se limpian las tablas (o rollback de transacción) entre casos.

##### Preconditions & Default Setup
* **Initial State** : Base de datos con esquema creado; tabla de estados vacía (o con seed, según decisión de diseño)
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia, sin autenticación)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar entidad nueva sin id | `estadoActivoRepository.save(entidad)` con `id = null` | estado=OPTIMO, descripcion="Funcionamiento correcto" | Retorna la entidad con `id` UUID v4 no nulo generado por la BD; la fila queda persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `estadoActivoRepository.findById(id)` | id obtenido en el paso 1 | `Optional` presente con todos los campos iguales a los guardados | [ ] | [ ] |
| 3 | Buscar por id inexistente | `estadoActivoRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()`, sin excepción | [ ] | [ ] |
| 4 | Actualizar entidad existente | `estadoActivoRepository.save(entidad)` con el `id` del paso 1 y un campo modificado | descripcion="Sin novedades" | Se actualiza la misma fila; no se crea una nueva (el conteo no cambia) | [ ] | [ ] |
| 5 | Listar todas | `estadoActivoRepository.findAll()` tras guardar una segunda entidad | estado=NUEVO | Lista de tamaño 2 con ambas entidades | [ ] | [ ] |
| 6 | Verificar existencia | `estadoActivoRepository.existsById(id)` | id existente y UUID aleatorio | `true` para el existente, `false` para el aleatorio | [ ] | [ ] |
| 7 | Eliminar por id | `estadoActivoRepository.deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por estado
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por estado persistido | `estadoActivoRepository.findByEstado(estado)` | OPTIMO | `Optional` presente con `estado = OPTIMO` | [ ] | [ ] |
| 2 | Buscar por estado no persistido | `estadoActivoRepository.findByEstado(estado)` | AVERIADO sin guardar | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por estado | `estadoActivoRepository.existsByEstado(estado)` | OPTIMO y AVERIADO sin guardar | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Recorrer todos los valores del enum | Para cada `EstadoFisicoActivo`: `save` y luego `findByEstado` | NUEVO, OPTIMO, MAL_ESTADO, AVERIADO | Cada valor retorna exactamente un registro | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar estado duplicado | `estadoActivoRepository.save(entidad)` | estado=OPTIMO (ya existe) | Lanza excepción de integridad (UNIQUE sobre `estado`) | [ ] | [ ] |
| 2 | Guardar estado nulo | `estadoActivoRepository.save(entidad)` | estado=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar estado en uso | `estadoActivoRepository.deleteById(id)` | Estado referenciado por un Recurso ACTIVO | Lanza violación de FK; el estado sigue existiendo | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : El enum se almacena como STRING (no ORDINAL): reordenar el enum no debe corromper los datos.
* **EC-002** : Si la BD contiene un texto que ya no existe en el enum, ¿cómo falla la lectura?
* **EC-003** : Definir si el catálogo se siembra al arrancar (seed) o lo crea el test.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
