### Test Case Specification: EspacioRepository - Persistencia y consultas
**Created** : 2026-11-08  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-006-EspacioRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P1 - Critical

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio de espacios persiste, actualiza, elimina y consulta espacios por nombre, categoría, facultad, ubicación, disponibilidad y aforo mínimo, bajo PostgreSQL real con Testcontainers y con las restricciones de integridad del esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| SPEC-HISTORIA4 | Specification Document | [SPEC-HISTORIA4.md](../../specs/SPEC-HISTORIA4.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| EspacioRepository | Domain Repository Interface (Port) | [EspacioRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/EspacioRepository.java) |
| Espacio | Domain Entity | [Espacio.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/Espacio.java) |
| CategoriaRecurso | Domain Enum | [CategoriaRecurso.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/enums/CategoriaRecurso.java) |
| EspacioRepositoryIT (planificado) | Automated Test (Testcontainers) | [EspacioRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/EspacioRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar).
* No se usa Mockito: la persistencia se prueba contra BD real.
* No se documentan pruebas de borrado lógico, `version` ni `fechaRegistro`: la entidad `Espacio` actual no los declara.

##### Dependencies & Assumptions
* TC-001 (Facultad), TC-002 (Ubicación) y TC-004 (EstadoDisponibilidad) deben pasar primero para satisfacer las FKs.
* `Espacio.categoria` se fija a `CategoriaRecurso.ESPACIO`.
* `aforo` es un entero; se prueban valores positivos y el límite inferior.
* Cada test corre aislado (limpieza de tablas en orden inverso a las FKs o rollback de transacción).

##### Preconditions & Default Setup
* **Initial State** : Catálogos con datos semilla; tabla `espacio` vacía
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar espacio nuevo sin id | `espacioRepository.save(espacio)` con `id = null` | nombre="Laboratorio 101", aforo=30, categoria=`ESPACIO`, plazoDevolucion=null, facultadId/ubicacionId/disponibilidadId válidos | Retorna el espacio con `id` UUID v4 generado por la BD; fila persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `espacioRepository.findById(id)` | id del paso 1 | `Optional` presente con todos los campos iguales | [ ] | [ ] |
| 3 | Buscar por id inexistente | `espacioRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()` | [ ] | [ ] |
| 4 | Actualizar espacio | `save(espacio)` con el `id` del paso 1 y `aforo` modificado | aforo=35 | Se actualiza la misma fila; `findAll().size()` no cambia | [ ] | [ ] |
| 5 | Listar todos | `espacioRepository.findAll()` tras guardar un segundo espacio | nombre="Auditorio" | Lista de tamaño 2 con ambos espacios | [ ] | [ ] |
| 6 | Verificar existencia | `espacioRepository.existsById(id)` | id existente y UUID aleatorio | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Eliminar por id | `deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por nombre
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por nombre exacto | `espacioRepository.findByNombre(nombre)` | "Laboratorio 101" | `Optional` presente con el espacio correcto | [ ] | [ ] |
| 2 | Buscar por nombre inexistente | `espacioRepository.findByNombre(nombre)` | "Laboratorio 999" | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por nombre | `espacioRepository.existsByNombre(nombre)` | "Laboratorio 101" y "Laboratorio 999" | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Búsqueda parcial sin importar mayúsculas | `findByNombreContainingIgnoreCase(fragmento)` | "laboratorio" | Lista con todos los espacios cuyo nombre contiene "laboratorio" | [ ] | FR-002 historia 2 |
| 5 | Búsqueda parcial sin coincidencias | `findByNombreContainingIgnoreCase("gimnasio")` | "gimnasio" | Lista vacía (`[]`), sin excepción | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Consultas por categoría y referencias
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Filtrar por categoría | `espacioRepository.findByCategoria(categoria)` | `CategoriaRecurso.ESPACIO` | Lista con todos los espacios de esa categoría; no incluye activos | [ ] | [ ] |
| 2 | Filtrar por facultad | `findByFacultadId(facultadId)` | facultadId válido | Lista con los espacios de esa facultad | [ ] | [ ] |
| 3 | Filtrar por ubicación | `findByUbicacionId(ubicacionId)` | ubicacionId válido | Lista con los espacios de esa ubicación | [ ] | [ ] |
| 4 | Filtrar por disponibilidad | `findByDisponibilidadId(id)` | id de `DISPONIBLE` | Lista con los espacios en ese estado | [ ] | [ ] |
| 5 | Verificar existencia por facultad | `existsByFacultadId(facultadId)` | facultad con y sin espacios | `true` y `false` respectivamente | [ ] | [ ] |
| 6 | Verificar existencia por ubicación | `existsByUbicacionId(ubicacionId)` | ubicación con y sin espacios | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Filtro sin coincidencias | `findByFacultadId(UUID.randomUUID())` | UUID aleatorio | Lista vacía (`[]`) | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 4: Consultas por aforo
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Aforo mayor o igual al mínimo | `findByAforoGreaterThanEqual(min)` | min=30 con espacios de aforo 20, 30 y 50 | Devuelve los de aforo 30 y 50 (incluye el límite) | [ ] | Condición `>=` |
| 2 | Aforo en el límite exacto | `findByAforoGreaterThanEqual(50)` | aforo=50 existe | El espacio de aforo 50 se incluye | [ ] | [ ] |
| 3 | Mínimo por encima de todos | `findByAforoGreaterThanEqual(1000)` | min=1000 | Lista vacía (`[]`) | [ ] | [ ] |
| 4 | Mínimo cero o negativo | `findByAforoGreaterThanEqual(0)` | min=0 | Devuelve todos los espacios con aforo válido; documentar si el aforo puede ser 0 | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 5: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar nombre duplicado | `save(espacio)` | nombre="Laboratorio 101" (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta | [ ] | UNIQUE en `nombre` |
| 2 | Guardar nombre nulo | `save(espacio)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Guardar aforo nulo | `save(espacio)` | aforo=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 4 | Aforo negativo | `save(espacio)` | aforo=-1 | Rechazado por CHECK/validación de dominio | [ ] | Definir si la BD lo restringe |
| 5 | Guardar con facultad inexistente | `save(espacio)` | facultadId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 6 | Guardar con ubicación inexistente | `save(espacio)` | ubicacionId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 7 | Guardar con disponibilidad inexistente | `save(espacio)` | disponibilidadId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 8 | Insertar con categoría ajena | `save(espacio)` | categoria=`ACTIVO` | Rechazado por la regla de categoría (CHECK o validación de dominio) | [ ] | Definir si la BD lo restringe |

--------------------------------------------------------------------------------

##### Section 6: Caracteres especiales y límites
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Nombre con tildes y eñes | `save` y `findById` | "Auditorio Ñandú" | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Nombre con comillas y comas | `save` y `findById` | Salón "A", Bloque 2 | Se recupera idéntico | [ ] | [ ] |
| 3 | Texto tipo inyección SQL en nombre | `save` y `findByNombreContainingIgnoreCase` | "'; DROP TABLE espacio;--" | Se guarda como texto literal; la tabla sigue intacta | [ ] | Consultas parametrizadas |
| 4 | Nombre más largo que el límite | `save(espacio)` | 256+ caracteres | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |
| 5 | `plazoDevolucion` en el límite | `save(espacio)` con plazo válido | p. ej. 1 y 365 días | Se persiste y recupera sin pérdida | [ ] | Definir rango permitido |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : `findByNombre` con nombres duplicados: se espera unicidad por catálogo; validar la restricción UNIQUE.
* **EC-002** : `findByNombreContainingIgnoreCase` con cadena vacía: ¿devuelve todos o ninguno? Definir.
* **EC-003** : Aforo = 0: ¿espacio válido o debe rechazarse?
* **EC-004** : Eliminar una facultad con espacios asociados: definir bloqueo por FK.
* **EC-005** : Dos `save` concurrentes con el mismo `nombre`: solo uno debe persistir.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
