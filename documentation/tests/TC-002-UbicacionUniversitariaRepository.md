### Test Case Specification: UbicacionUniversitariaRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-002-UbicacionUniversitariaRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio de ubicaciones persiste, consulta y protege la integridad del catálogo de ubicaciones universitarias, bajo las condiciones operativas especificadas: PostgreSQL real levantado con Testcontainers, UUID v4 generados por la base de datos y restricciones de integridad aplicadas por el esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| UbicacionUniversitariaRepository | Domain Repository Interface (Port) | [UbicacionUniversitariaRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/UbicacionUniversitariaRepository.java) |
| UbicacionUniversitaria | Domain Entity | [UbicacionUniversitaria.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/UbicacionUniversitaria.java) |
| UbicacionUniversitariaRepositoryIT (planificado) | Automated Test (Testcontainers) | [UbicacionUniversitariaRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/UbicacionUniversitariaRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host (Windows/Linux/Mac) para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar; el plan solo indica PostgreSQL).
* No se usa Mockito: la persistencia se prueba contra BD real. Mockito queda para pruebas unitarias de casos de uso.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Catálogo sin FKs: puede ejecutarse en paralelo con TC-001.
* El esquema se crea con PKs `UUID DEFAULT gen_random_uuid()` y extensión `pgcrypto` habilitada (T004 / T004b del plan).
* Cada test corre aislado: se limpian las tablas (o rollback de transacción) entre casos.

##### Preconditions & Default Setup
* **Initial State** : Base de datos con esquema creado y tablas vacías
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia, sin autenticación)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar entidad nueva sin id | `ubicacionRepository.save(entidad)` con `id = null` | nombre="Laboratorio de Redes", descripcion="Bloque de Ingeniería" | Retorna la entidad con `id` UUID v4 no nulo generado por la BD; la fila queda persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `ubicacionRepository.findById(id)` | id obtenido en el paso 1 | `Optional` presente con todos los campos iguales a los guardados | [ ] | [ ] |
| 3 | Buscar por id inexistente | `ubicacionRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()`, sin excepción | [ ] | [ ] |
| 4 | Actualizar entidad existente | `ubicacionRepository.save(entidad)` con el `id` del paso 1 y un campo modificado | descripcion="Bloque de Ingeniería, piso 2" | Se actualiza la misma fila; no se crea una nueva (`findAll().size()` no cambia) | [ ] | [ ] |
| 5 | Listar todas | `ubicacionRepository.findAll()` tras guardar una segunda entidad | nombre="Hangares" | Lista de tamaño 2 con ambas entidades | [ ] | [ ] |
| 6 | Verificar existencia | `ubicacionRepository.existsById(id)` | id existente y UUID aleatorio | `true` para el existente, `false` para el aleatorio | [ ] | [ ] |
| 7 | Eliminar por id | `ubicacionRepository.deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por nombre
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por nombre exacto | `ubicacionRepository.findByNombre(nombre)` | Laboratorio de Redes | `Optional` presente con la ubicación correcta | [ ] | [ ] |
| 2 | Buscar por nombre inexistente | `ubicacionRepository.findByNombre(nombre)` | Auditorio Principal | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por nombre | `ubicacionRepository.existsByNombre(nombre)` | Laboratorio de Redes y Auditorio Principal | `true` y `false` respectivamente | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar nombre duplicado | `ubicacionRepository.save(entidad)` | nombre=Laboratorio de Redes (ya existe) | Lanza excepción de integridad (UNIQUE) | [ ] | Requiere definir UNIQUE en `nombre` |
| 2 | Guardar nombre nulo | `ubicacionRepository.save(entidad)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar ubicación con espacios asociados | `ubicacionRepository.deleteById(id)` | Ubicación referenciada por un `Espacio` | Lanza violación de FK; la ubicación sigue existiendo | [ ] | Regla pendiente: bloquear vs. cascada |

--------------------------------------------------------------------------------

##### Section 4: Caracteres especiales y longitud
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Nombre con tildes y eñes | `ubicacionRepository.save(entidad)` y `findById` | Ingeniería de Diseño Ñandú | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Nombre con comillas y comas | `ubicacionRepository.save(entidad)` y `findById` | Cámara "HD", 4K | Se recupera idéntico, sin romper la consulta | [ ] | [ ] |
| 3 | Texto tipo inyección SQL | `ubicacionRepository.save(entidad)` y `findByNombre` | '; DROP TABLE ubicacion_universitaria;-- | Se guarda como texto literal; la tabla sigue intacta y `findByNombre` lo encuentra | [ ] | Consultas parametrizadas (FR-009 historia 1) |
| 4 | Nombre más largo que el límite | `ubicacionRepository.save(entidad)` | Nombre de 256+ caracteres (límite a definir, p. ej. 255) | Lanza excepción de integridad (valor demasiado largo); no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : ¿`findByNombre` distingue mayúsculas y tildes? Definir y probar la regla.
* **EC-002** : Dos `save` concurrentes con el mismo nombre: solo uno debe persistir.
* **EC-003** : `deleteById` con un id inexistente: definir si es no-op o lanza excepción.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
