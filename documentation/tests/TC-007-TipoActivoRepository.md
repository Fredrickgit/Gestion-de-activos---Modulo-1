### Test Case Specification: TipoActivoRepository - Persistencia y consultas
**Created** : 2026-11-08  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-007-TipoActivoRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P2 - High

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio del catálogo de tipos de activo persiste, consulta y protege la integridad de los tipos (nombre único, descripción y plazo de devolución), bajo PostgreSQL real con Testcontainers.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| TipoActivoRepository | Domain Repository Interface (Port) | [TipoActivoRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/TipoActivoRepository.java) |
| TipoActivo | Domain Entity | [TipoActivo.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/TipoActivo.java) |
| TipoActivoRepositoryIT (planificado) | Automated Test (Testcontainers) | [TipoActivoRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/TipoActivoRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar).
* No se usa Mockito: la persistencia se prueba contra BD real.
* No se documentan pruebas de borrado lógico, `version` ni `fechaRegistro`: la entidad `TipoActivo` actual no los declara.

##### Dependencies & Assumptions
* TC-INFRA-001 (migraciones y esquema base) debe pasar primero.
* Catálogo sin FKs salientes: puede ejecutarse en paralelo con los demás catálogos.
* `plazoDevolucion` es un entero que representa días; puede ser nulo si el tipo no presta devolución.
* Cada test corre aislado (limpieza de tabla o rollback de transacción).

##### Preconditions & Default Setup
* **Initial State** : Tabla `tipo_activo` vacía
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar tipo nuevo sin id | `tipoActivoRepository.save(tipo)` con `id = null` | nombre="Laptop", descripcion="Equipo portátil", plazoDevolucion=15 | Retorna el tipo con `id` UUID v4 generado por la BD; fila persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `tipoActivoRepository.findById(id)` | id del paso 1 | `Optional` presente con todos los campos iguales | [ ] | [ ] |
| 3 | Buscar por id inexistente | `tipoActivoRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()` | [ ] | [ ] |
| 4 | Actualizar tipo | `save(tipo)` con el `id` del paso 1 y `plazoDevolucion` modificado | plazoDevolucion=30 | Se actualiza la misma fila; `findAll().size()` no cambia | [ ] | [ ] |
| 5 | Listar todos | `tipoActivoRepository.findAll()` tras guardar un segundo tipo | nombre="Proyector" | Lista de tamaño 2 con ambos tipos | [ ] | [ ] |
| 6 | Verificar existencia | `tipoActivoRepository.existsById(id)` | id existente y UUID aleatorio | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Eliminar por id | `deleteById(id)` y luego `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por nombre
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por nombre exacto | `tipoActivoRepository.findByNombre(nombre)` | "Laptop" | `Optional` presente con el tipo correcto | [ ] | [ ] |
| 2 | Buscar por nombre inexistente | `findByNombre(nombre)` | "Impresora" | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por nombre | `existsByNombre(nombre)` | "Laptop" e "Impresora" | `true` y `false` respectivamente | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Búsqueda parcial
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Búsqueda parcial sin importar mayúsculas | `findByNombreContainingIgnoreCase(fragmento)` | "lap" | Lista con todos los tipos cuyo nombre contiene "lap" | [ ] | [ ] |
| 2 | Búsqueda parcial en mayúsculas | `findByNombreContainingIgnoreCase("LAP")` | "LAP" | Mismo resultado que el paso 1 | [ ] | Case-insensitive |
| 3 | Búsqueda parcial sin coincidencias | `findByNombreContainingIgnoreCase("xyz")` | "xyz" | Lista vacía (`[]`), sin excepción | [ ] | [ ] |
| 4 | Búsqueda parcial con varios resultados | `findByNombreContainingIgnoreCase("ora")` | "Proyector" y "Impresora" | Lista con ambos tipos | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 4: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar nombre duplicado | `save(tipo)` | nombre="Laptop" (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta | [ ] | UNIQUE en `nombre` |
| 2 | Guardar nombre nulo | `save(tipo)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | `plazoDevolucion` nulo | `save(tipo)` | plazoDevolucion=null | Permitido (tipo sin devolución) | [ ] | Confirmar que la columna es nullable |
| 4 | `plazoDevolucion` negativo | `save(tipo)` | plazoDevolucion=-5 | Rechazado por CHECK/validación de dominio | [ ] | Definir si la BD lo restringe |
| 5 | Eliminar tipo referenciado por un activo | `deleteById(id)` | Tipo usado por un `Activo` | Lanza violación de FK; el tipo sigue existiendo | [ ] | Regla pendiente: bloquear vs. cascada |

--------------------------------------------------------------------------------

##### Section 5: Caracteres especiales y límites
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Nombre con tildes y eñes | `save` y `findById` | "Cámara Ñandú" | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Descripción con comillas y comas | `save` y `findById` | Equipo "HD", 4K | Se recupera idéntico | [ ] | [ ] |
| 3 | Texto tipo inyección SQL en nombre | `save` y `findByNombreContainingIgnoreCase` | "'; DROP TABLE tipo_activo;--" | Se guarda como texto literal; la tabla sigue intacta | [ ] | Consultas parametrizadas |
| 4 | Nombre/descripción más largos que el límite | `save(tipo)` | 256+ caracteres | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : `plazoDevolucion = 0`: ¿válido (devolución el mismo día) o debe ser mínimo 1?
* **EC-002** : `findByNombreContainingIgnoreCase` con cadena vacía: ¿devuelve todos o ninguno? Definir.
* **EC-003** : Dos `save` concurrentes con el mismo `nombre`: solo uno debe persistir.
* **EC-004** : `deleteById` con un id inexistente: definir si es no-op o lanza excepción.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
