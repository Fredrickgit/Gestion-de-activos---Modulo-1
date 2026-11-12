### Test Case Specification: ActivoRepository - Persistencia y consultas
**Created** : 2026-11-08  **Spec/Plan Link** : [PLAN.md](../../specs/PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-005-ActivoRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Author** : Equipo de Desarrollo - Módulo 1
**Current Owner** : QA Lead - Módulo 1
**Priority** : P1 - Critical

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio de activos persiste, actualiza, elimina y consulta activos por código, nombre, categoría y referencias a espacio, disponibilidad y estado físico, bajo PostgreSQL real con Testcontainers y con las restricciones de integridad del esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | [Modulos-gestion-de-activos.md](../resources/class-resources/Modulos-gestion-de-activos.md) |
| SPEC-HISTORIA1 | Specification Document | [SPEC-HISTORIA1.md](../../specs/SPEC-HISTORIA1.md) |
| SPEC-HISTORIA2 | Specification Document | [SPEC-HISTORIA2.md](../../specs/SPEC-HISTORIA2.md) |
| SPEC-HISTORIA4 | Specification Document | [SPEC-HISTORIA4.md](../../specs/SPEC-HISTORIA4.md) |
| Implementation Plan | Plan Document | [PLAN.md](../../specs/PLAN.md) |
| ActivoRepository | Domain Repository Interface (Port) | [ActivoRepository.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/Ports/ActivoRepository.java) |
| Activo | Domain Entity | [Activo.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/Activo.java) |
| CategoriaRecurso | Domain Enum | [CategoriaRecurso.java](../../backend/src/main/java/com/edu/unimag/Gestion_de_activos__Modulo_1/domain/entities/enums/CategoriaRecurso.java) |
| ActivoRepositoryIT (planificado) | Automated Test (Testcontainers) | [ActivoRepositoryIT.java](../../backend/src/test/java/com/edu/unimag/Gestion_de_activos__Modulo_1/integration/repositories/ActivoRepositoryIT.java) |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar).
* No se usa Mockito: la persistencia se prueba contra BD real.
* No se documentan pruebas de borrado lógico, `version` ni `fechaRegistro`: la entidad `Activo` actual no los declara.

##### Dependencies & Assumptions
* TC-001, TC-002, TC-003 y TC-004 deben pasar primero: se requieren registros en `estado_activo`, `estado_disponibilidad`, `facultad` y `ubicacion_universitaria` para satisfacer las FKs.
* Para las pruebas por `espacioId` se debe crear previamente un `Espacio` (dependencia circular controlada: `Activo.espacioId` es nullable).
* `Activo.categoria` se fija a `CategoriaRecurso.ACTIVO`.
* Cada test corre aislado (limpieza de tablas en orden inverso a las FKs o rollback de transacción).

##### Preconditions & Default Setup
* **Initial State** : Tablas de catálogos con datos semilla; tabla `activo` vacía
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica (CRUD)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar activo nuevo sin id | `activoRepository.save(activo)` con `id = null` | nombre="Laptop Dell", codigo="ACT-001", categoria=`ACTIVO`, estadoFisicoId, disponibilidadId válidos, espacioId=null | Retorna el activo con `id` UUID v4 generado por la BD; fila persistida | [ ] | [ ] |
| 2 | Buscar por id existente | `activoRepository.findById(id)` | id del paso 1 | `Optional` presente con todos los campos iguales | [ ] | [ ] |
| 3 | Buscar por id inexistente | `activoRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()` | [ ] | [ ] |
| 4 | Actualizar activo | `activoRepository.save(activo)` con el `id` del paso 1 y `nombre` modificado | nombre="Laptop Dell Latitude" | Se actualiza la misma fila; `findAll().size()` no cambia | [ ] | [ ] |
| 5 | Listar todos | `activoRepository.findAll()` tras guardar un segundo activo | codigo="ACT-002" | Lista de tamaño 2 con ambos activos | [ ] | [ ] |
| 6 | Verificar existencia | `activoRepository.existsById(id)` | id existente y UUID aleatorio | `true` y `false` respectivamente | [ ] | [ ] |
| 7 | Eliminar por id | `activoRepository.deleteById(id)` y `existsById(id)` | id del paso 1 | `existsById` retorna `false`; `findAll()` retorna 1 elemento | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Consultas por código y nombre
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por código exacto | `activoRepository.findByCodigo(codigo)` | ACT-001 | `Optional` presente con el activo correcto | [ ] | [ ] |
| 2 | Buscar por código inexistente | `activoRepository.findByCodigo(codigo)` | ACT-999 | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por código | `activoRepository.existsByCodigo(codigo)` | ACT-001 y ACT-999 | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Buscar por nombre exacto | `activoRepository.findByNombre(nombre)` | "Laptop Dell" | Lista con el activo (o vacía si el nombre es case-sensitive) | [ ] | Definir case-sensitivity |
| 5 | Búsqueda parcial sin importar mayúsculas | `activoRepository.findByNombreContainingIgnoreCase(fragmento)` | "laptop" | Lista con todos los activos cuyo nombre contiene "laptop" (sin distinguir mayúsculas) | [ ] | FR-002 historia 2 |
| 6 | Búsqueda parcial sin coincidencias | `findByNombreContainingIgnoreCase("impresora")` | "impresora" | Lista vacía (`[]`), sin excepción | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Consultas por categoría y referencias
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Filtrar por categoría | `activoRepository.findByCategoria(categoria)` | `CategoriaRecurso.ACTIVO` | Lista con todos los activos de esa categoría; no incluye espacios | [ ] | [ ] |
| 2 | Filtrar por espacio | `activoRepository.findByEspacioId(espacioId)` | espacioId válido | Lista con los activos asignados a ese espacio | [ ] | [ ] |
| 3 | Filtrar por disponibilidad | `activoRepository.findByDisponibilidadId(id)` | id de `DISPONIBLE` | Lista con los activos en ese estado | [ ] | [ ] |
| 4 | Filtrar por estado físico | `activoRepository.findByEstadoFisicoId(id)` | id de `OPTIMO` | Lista con los activos en ese estado | [ ] | [ ] |
| 5 | Verificar existencia por espacio | `activoRepository.existsByEspacioId(espacioId)` | espacio con y sin activos | `true` y `false` respectivamente | [ ] | [ ] |
| 6 | Filtro sin coincidencias | `findByEspacioId(UUID.randomUUID())` | UUID aleatorio | Lista vacía (`[]`) | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 4: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar código duplicado | `save(activo)` | codigo="ACT-001" (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta | [ ] | UNIQUE en `codigo` |
| 2 | Guardar nombre nulo | `save(activo)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 3 | Guardar código nulo | `save(activo)` | codigo=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 4 | Guardar con espacio inexistente | `save(activo)` | espacioId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 5 | Guardar con disponibilidad inexistente | `save(activo)` | disponibilidadId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 6 | Guardar con estado físico inexistente | `save(activo)` | estadoFisicoId=UUID.randomUUID() | Lanza violación de FK; no se inserta | [ ] | [ ] |
| 7 | Insertar con categoría ajena | `save(activo)` | categoria=`ESPACIO` | Rechazado por la regla de categoría (CHECK o validación de dominio) | [ ] | Definir si la BD lo restringe |

--------------------------------------------------------------------------------

##### Section 5: Caracteres especiales y límites
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Nombre con tildes y eñes | `save` y `findById` | "Cámara Ñandú" | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 2 | Nombre con comillas y comas | `save` y `findById` | Cámara "HD", 4K | Se recupera idéntico | [ ] | [ ] |
| 3 | Texto tipo inyección SQL en nombre | `save` y `findByNombreContainingIgnoreCase` | "'; DROP TABLE activo;--" | Se guarda como texto literal; la tabla sigue intacta | [ ] | Consultas parametrizadas |
| 4 | Código con formato inválido | `save(activo)` | codigo="###" | Rechazado si existe validación de formato; documentar la regla | [ ] | Formato de `codigo` pendiente |
| 5 | Nombre/código más largos que el límite | `save(activo)` | 256+ caracteres | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : `findByNombre` con nombres duplicados: ¿devuelve lista o debería ser `Optional`? La firma actual es `List`, validar que no asuma unicidad.
* **EC-002** : `findByNombreContainingIgnoreCase` con cadena vacía: ¿devuelve todos o ninguno? Definir.
* **EC-003** : Activo sin espacio (`espacioId = null`): `findByEspacioId` no debe incluirlo.
* **EC-004** : Eliminar un espacio con activos asignados: definir `ON DELETE SET NULL` o bloqueo por FK.
* **EC-005** : Dos `save` concurrentes con el mismo `codigo`: solo uno debe persistir.

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
