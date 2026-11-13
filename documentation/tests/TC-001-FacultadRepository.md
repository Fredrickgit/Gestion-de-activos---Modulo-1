### Test Case Specification: FacultadRepository - Persistencia y consultas
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-001-FacultadRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio de facultades persiste, consulta y protege la integridad del catálogo de facultades, bajo las condiciones operativas especificadas: PostgreSQL real levantado con Testcontainers, UUID v4 generados por la base de datos y restricciones de integridad aplicadas por el esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| FacultadRepository | Domain Repository Interface (Port) | [FacultadRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/FacultadRepository.java) |
| Facultad | Domain Entity | [Facultad.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/Facultad.java) |
| FacultadRepositoryIT (planificado) | Automated Test (Testcontainers) | [FacultadRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/FacultadRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host (Windows/Linux/Mac) para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar; el plan solo indica PostgreSQL).
* No se usa Mockito: la persistencia se prueba contra BD real. Mockito queda para pruebas unitarias de casos de uso.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Catálogo sin FKs: primera entidad del orden de ejecución.
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
| 1 | Guardar entidad nueva sin id | `facultadRepository.save(entidad)` con `id = null` | nombre="Ingeniería", descripcion="Facultad de Ingeniería" | Retorna la entidad con `id` UUID v4 no nulo generado por la BD; la fila queda persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `facultadRepository.findById(id)` | id obtenido en el paso 1 | `Optional` presente con todos los campos iguales a los guardados | [ ] | [ ] |
| 3 | Buscar por id inexistente | `facultadRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()`, sin excepción | [ ] | [ ] |
| 4 | Actualizar entidad existente | `facultadRepository.save(entidad)` con el `id` del paso 1 y un campo modificado | descripcion="Facultad de Ingeniería y Tecnología" | Se actualiza la misma fila; no se crea una nueva (`findAll().size()` no cambia) | [ ] | [ ] |
| 5 | Listar todas | `facultadRepository.findAll()` tras guardar una segunda entidad | nombre="Ciencias de la Salud" | Lista de tamaño 2 con ambas entidades | [ ] | [ ] |
| 6 | Verificar existencia | `facultadRepository.existsById(id)` | id existente y UUID aleatorio | `true` para el existente, `false` para el aleatorio | [ ] | [ ] |
| 7 | Eliminar por id | `facultadRepository.deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por nombre
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por nombre exacto | `facultadRepository.findByNombre(nombre)` | Ingeniería | `Optional` presente con la facultad correcta | [ ] | [ ] |
| 2 | Buscar por nombre inexistente | `facultadRepository.findByNombre(nombre)` | Derecho | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por nombre | `facultadRepository.existsByNombre(nombre)` | Ingeniería y Derecho | `true` y `false` respectivamente | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar nombre duplicado | `facultadRepository.save(entidad)` | nombre=Ingeniería (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta la fila | [ ] | Requiere definir UNIQUE en `nombre` |
| 2 | Guardar nombre nulo | `facultadRepository.save(entidad)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Eliminar facultad con espacios asociados | `facultadRepository.deleteById(id)` | Facultad referenciada por un `Espacio` | Lanza violación de FK; la facultad sigue existiendo | [ ] | Regla pendiente: bloquear vs. cascada |

--------------------------------------------------------------------------------

##### Section 4: Caracteres especiales y longitud
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Nombre con tildes y eñes | `facultadRepository.save(entidad)` y `findById` | Ingeniería de Diseño Ñandú | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Nombre con comillas y comas | `facultadRepository.save(entidad)` y `findById` | Cámara "HD", 4K | Se recupera idéntico, sin romper la consulta | [ ] | [ ] |
| 3 | Texto tipo inyección SQL | `facultadRepository.save(entidad)` y `findByNombre` | '; DROP TABLE facultad;-- | Se guarda como texto literal; la tabla sigue intacta y `findByNombre` lo encuentra | [ ] | Consultas parametrizadas (FR-009 historia 1) |
| 4 | Nombre más largo que el límite | `facultadRepository.save(entidad)` | Nombre de 256+ caracteres (límite a definir, p. ej. 255) | Lanza excepción de integridad (valor demasiado largo); no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : ¿`findByNombre` distingue mayúsculas y tildes (`ingenieria` vs `Ingeniería`)? Definir y probar la regla.
* **EC-002** : ¿Cómo se comporta con nombres con espacios sobrantes (`" Ingeniería "`)?
* **EC-003** : Dos `save` concurrentes con el mismo nombre: solo uno debe persistir.
* **EC-004** : `deleteById` con un id inexistente: definir si es no-op o lanza excepción.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
