### Test Case Specification: RecursoRepository - Persistencia, baja lógica, concurrencia y paginación
**Created** : 2026-10-29  **Spec/Plan Link** : [PLAN.md](../PLAN.md)  **Status** : Draft

#### Test Overview  *(mandatory)*

**Test Case ID** : TC-005-RecursoRepository
**Product Name & Version** : Módulo 1 - Gestión de Activos y Espacios (UNIMAG) v1.0.0
**Priority** : P1 - Critical

--------------------------------------------------------------------------------

#### Objectives & Context  *(mandatory)*

##### Test Objectives
Este caso de prueba verifica que el repositorio de recursos (activos y espacios combinados) persiste ambas categorías, aplica baja lógica y control de versión, consulta por sus atributos, pagina el inventario con orden estable y respeta la integridad referencial, bajo las condiciones operativas especificadas: PostgreSQL real levantado con Testcontainers, UUID v4 generados por la base de datos y restricciones de integridad aplicadas por el esquema.

##### Document References
| Reference Name | Type of Reference | Document Location / Link |
| ------ | ------ | ------ |
| Modulo 1 Spec | Specification Document | Modulos-gestion-de-activos.md |
| SPEC-HISTORIA0 | Specification Document | specs/SPEC-HISTORIA0.md |
| SPEC-HISTORIA1 | Specification Document | specs/SPEC-HISTORIA1.md |
| SPEC-HISTORIA2 | Specification Document | specs/SPEC-HISTORIA2.md |
| SPEC-HISTORIA3 | Specification Document | specs/SPEC-HISTORIA3.md |
| SPEC-HISTORIA4 | Specification Document | specs/SPEC-HISTORIA4.md |
| Implementation Plan | Plan Document | PLAN.md |
| RecursoRepository | Domain Repository Interface (Port) | src/domain/repositories/RecursoRepository.java |
| Recurso | Domain Entity | src/domain/entities/Recurso.java |
| RecursoRepositoryIT | Automated Test (Testcontainers) | tests/integration/repositories/RecursoRepositoryIT.java |

--------------------------------------------------------------------------------

#### Prerequisites & Environment

##### Test Case Limitations
* Requiere Docker disponible en el host (Windows/Linux/Mac) para ejecutar Testcontainers.
* Imagen PostgreSQL `postgres:16-alpine` (versión por confirmar; el plan solo indica PostgreSQL).
* No se usa Mockito: la persistencia se prueba contra BD real. Mockito queda para pruebas unitarias de casos de uso.

##### Dependencies & Assumptions
* TC-001-FacultadRepository debe pasar primero.
* TC-002-UbicacionUniversitariaRepository debe pasar primero.
* TC-003-EstadoActivoRepository debe pasar primero.
* TC-004-EstadoDisponibilidadRepository debe pasar primero.
* Las validaciones de negocio (aforo, coherencia por categoría, permisos) viven en la capa de aplicación; este test solo verifica persistencia y restricciones de la BD.
* El esquema se crea con PKs `UUID DEFAULT gen_random_uuid()` y extensión `pgcrypto` habilitada (T004 / T004b del plan).
* Cada test corre aislado: se limpian las tablas (o rollback de transacción) entre casos.

##### Preconditions & Default Setup
* **Initial State** : Tablas vacías, salvo fixtures: 2 Facultades, 2 Ubicaciones, EstadoActivo (4 valores), EstadoDisponibilidad (5 valores)
* **Environment** : Testcontainers (PostgreSQL efímero), perfil `test`, Java 21 + Spring Boot
* **Test Accounts** : N/A (capa de persistencia, sin autenticación)

--------------------------------------------------------------------------------

#### Test Execution Flow  *(mandatory)*

##### Section 1: Persistencia básica
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar ACTIVO nuevo sin id | `recursoRepository.save(recurso)` con `id = null` | categoria=ACTIVO, nombre="Videobeam Epson Pro", codigo="ACT-FI-001", plazoDevolucion=2, estadoFisicoId=NUEVO, disponibilidadId=DISPONIBLE | Retorna el recurso con `id` UUID v4, `fechaRegistro` no nula, `dadoDeBaja=false` y `version` inicial | [ ] | [ ] |
| 2 | Buscar por id existente | `recursoRepository.findById(id)` | id del paso 1 | `Optional` presente con todos los campos iguales a los guardados | [ ] | [ ] |
| 3 | Buscar por id inexistente | `recursoRepository.findById(UUID.randomUUID())` | UUID aleatorio | `Optional.empty()`, sin excepción | [ ] | [ ] |
| 4 | Actualizar recurso existente | `recursoRepository.save(recurso)` con el `id` del paso 1 y `estadoFisicoId=OPTIMO` | estadoFisicoId=OPTIMO | Se actualiza la misma fila; la `version` aumenta en 1; `fechaRegistro` no cambia | [ ] | [ ] |
| 5 | Verificar existencia | `recursoRepository.existsById(id)` | id existente y UUID aleatorio | `true` para el existente, `false` para el aleatorio | [ ] | [ ] |
| 6 | Guardar ESPACIO nuevo sin id | `recursoRepository.save(recurso)` con `id = null` | categoria=ESPACIO, nombre="Laboratorio de Telecomunicaciones", codigo="ESP-FI-001", aforo=30, facultadId, ubicacionId, disponibilidadId=DISPONIBLE | Retorna el recurso con `id` UUID v4 y `fechaRegistro` no nula | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 2: Persistencia por categoría
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Recargar un ACTIVO guardado | `recursoRepository.findById(id)` de un activo | Activo con estadoFisicoId y espacioId | `esActivo()` es `true`; `aforo`, `facultadId` y `ubicacionId` son `null` | [ ] | [ ] |
| 2 | Recargar un ESPACIO guardado | `recursoRepository.findById(id)` de un espacio | Espacio con aforo=30, facultadId, ubicacionId | `esEspacio()` es `true`; `estadoFisicoId` y `espacioId` son `null`; `aforo` conserva 30 | [ ] | [ ] |
| 3 | Activo dentro de un espacio | `recursoRepository.save(activo)` con `espacioId` de un espacio existente | espacioId=id del espacio | Se persiste la referencia (FK sobre la misma tabla) | [ ] | [ ] |
| 4 | Almacenamiento de la categoría | Leer la columna `categoria` por SQL directo | Un ACTIVO y un ESPACIO | Se guarda como texto `ACTIVO` / `ESPACIO`, no como ordinal | [ ] | [ ] |
| 5 | Fecha de registro en UTC | Guardar y recargar; leer `fechaRegistro` | Recurso nuevo | Se almacena en UTC y se recupera con el mismo instante (la conversión a UTC-5 es de presentación) | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 3: Baja lógica
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Dar de baja un recurso | `recursoRepository.darDeBaja(id)` | Recurso existente | Retorna `true`; la fila sigue existiendo en la BD (verificar por SQL directo) | [ ] | [ ] |
| 2 | Recurso de baja oculto en consultas | `findById(id)` y `existsById(id)` | id dado de baja | `Optional.empty()` y `false` | [ ] | El plan define 404 para recursos dados de baja |
| 3 | Recurso de baja visible para corrección | `recursoRepository.findByIdIncluyendoBajas(id)` | id dado de baja | `Optional` presente con `dadoDeBaja=true` | [ ] | [ ] |
| 4 | Baja repetida o de id inexistente | `recursoRepository.darDeBaja(id)` por segunda vez y con UUID aleatorio | id ya dado de baja / UUID aleatorio | Retorna `false` en ambos casos, sin excepción | [ ] | [ ] |
| 5 | Código de un recurso de baja | `findByCodigo(codigo)` y `existsByCodigo(codigo)` | codigo de un recurso dado de baja | `findByCodigo` vacío; `existsByCodigo` retorna `true` | [ ] | Regla pendiente: ¿el código puede reutilizarse tras la baja? |
| 6 | Baja y referencias | `existsByEspacioId`, `existsByFacultadId` con solo recursos de baja | Recursos de baja que referencian el catálogo | Retornan `true`: siguen referenciando | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 4: Control de concurrencia (versión)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Guardar con la versión actual | `recursoRepository.save(recurso)` con la `version` leída | Recurso leído sin cambios intermedios | Se actualiza y la `version` aumenta en 1 | [ ] | [ ] |
| 2 | Guardar con versión desactualizada | Dos lecturas de la misma versión; guardar la primera, luego la segunda | Dos copias con la misma `version` | El segundo guardado lanza `ConflictoDeVersionException`; no sobrescribe los cambios del primero | [ ] | Historia 3: bloquear al detectar información obsoleta |
| 3 | Actualizaciones simultáneas | Dos hilos guardan el mismo recurso al mismo tiempo | 2 hilos, misma versión inicial | Exactamente uno tiene éxito; el otro recibe `ConflictoDeVersionException` | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 5: Consultas por identificación y categoría
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Buscar por código | `recursoRepository.findByCodigo(codigo)` | ACT-FI-001 | `Optional` presente con el recurso correcto | [ ] | [ ] |
| 2 | Buscar por código inexistente | `recursoRepository.findByCodigo(codigo)` | ACT-XX-999 | `Optional.empty()` | [ ] | [ ] |
| 3 | Verificar existencia por código | `recursoRepository.existsByCodigo(codigo)` | ACT-FI-001 y ACT-XX-999 | `true` y `false` respectivamente | [ ] | [ ] |
| 4 | Buscar por nombre con repetidos | `recursoRepository.findByNombre(nombre)` | 2 activos llamados "Videobeam Epson Pro" | Lista con los 2 recursos | [ ] | [ ] |
| 5 | Buscar por nombre inexistente | `recursoRepository.findByNombre(nombre)` | Nombre no registrado | Lista vacía (no `null`) | [ ] | [ ] |
| 6 | Filtrar por categoría ACTIVO | `recursoRepository.findByCategoria(ACTIVO)` | 3 activos y 2 espacios | Retorna solo los 3 activos | [ ] | [ ] |
| 7 | Filtrar por categoría ESPACIO | `recursoRepository.findByCategoria(ESPACIO)` | 3 activos y 2 espacios | Retorna solo los 2 espacios | [ ] | [ ] |
| 8 | Filtrar por disponibilidad | `recursoRepository.findByDisponibilidadId(id)` | Recursos en cada uno de los 5 estados | Retorna solo los del estado consultado, de ambas categorías | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 6: Consultas específicas de ACTIVO
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Filtrar por estado físico | `recursoRepository.findByEstadoFisicoId(id)` | Activos NUEVO, OPTIMO, AVERIADO | Retorna solo el activo con ese estado; nunca espacios | [ ] | [ ] |
| 2 | Equipamiento de un espacio | `recursoRepository.findByEspacioId(espacioId)` | 2 activos en el espacio 1, 1 en el espacio 2 | Retorna solo los 2 activos del espacio 1 | [ ] | [ ] |
| 3 | Espacio sin equipamiento | `recursoRepository.findByEspacioId(espacioId)` | Espacio vacío | Lista vacía (no `null`) | [ ] | [ ] |
| 4 | Existencia por espacio | `existsByEspacioId(id)` | Ids con y sin activos | `true` con activos, `false` sin ellos | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 7: Consultas específicas de ESPACIO
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Filtrar por facultad | `recursoRepository.findByFacultadId(facultadId)` | 2 espacios de Ingeniería y 1 de Salud | Retorna solo los 2 de Ingeniería; nunca activos | [ ] | [ ] |
| 2 | Facultad sin espacios | `recursoRepository.findByFacultadId(facultadId)` | Facultad sin espacios | Lista vacía (no `null`) | [ ] | [ ] |
| 3 | Filtrar por ubicación | `recursoRepository.findByUbicacionId(ubicacionId)` | Espacios en 2 ubicaciones distintas | Retorna solo los de la ubicación consultada | [ ] | [ ] |
| 4 | Filtrar por aforo mínimo (frontera) | `recursoRepository.findByAforoGreaterThanEqual(30)` | Espacios con aforo 29, 30 y 31, más activos | Retorna los de aforo 30 y 31; excluye el 29 y todos los activos | [ ] | Prueba de frontera |
| 5 | Existencia por facultad y ubicación | `existsByFacultadId(id)` y `existsByUbicacionId(id)` | Ids con y sin espacios | `true` con espacios, `false` sin ellos | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 8: Listado paginado y orden (GET /api/v1/recursos)
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Primera página de 20 | `recursoRepository.buscar(FiltroRecurso.sinFiltros(), OrdenRecurso.porDefecto(), 0, 20)` | 25 recursos guardados | `content` con 20 elementos; `page=0`, `size=20`, `totalElements=25` | [ ] | [ ] |
| 2 | Última página parcial | `recursoRepository.buscar(sinFiltros, porDefecto, 1, 20)` | 25 recursos guardados | `content` con 5 elementos; `totalElements=25` | [ ] | [ ] |
| 3 | Página fuera de rango | `recursoRepository.buscar(sinFiltros, porDefecto, 10, 20)` | 25 recursos guardados | `content` vacío y `totalElements=25`; sin excepción | [ ] | [ ] |
| 4 | Orden estable con valores repetidos | Recorrer todas las páginas ordenando por NOMBRE | 100 recursos llamados "Proyector" | Se obtienen los 100 sin duplicados ni omisiones (el id desempata) | [ ] | Historia 4, SC-004 |
| 5 | Ordenar por cada campo | `buscar` con `NOMBRE`, `ESTADO` y `FECHA_REGISTRO`, ascendente y descendente | Recursos con valores distintos | El orden coincide con el campo elegido y se invierte en descendente | [ ] | [ ] |
| 6 | Filtro por nombre parcial | `buscar(filtro con nombre="proy", ...)` | "Proyector" y "Proyector 4K" | Ambos aparecen sin distinguir mayúsculas | [ ] | [ ] |
| 7 | Filtro por código | `buscar(filtro con codigo="ACT-FI-001", ...)` | Recursos varios | Solo el recurso con ese código | [ ] | [ ] |
| 8 | Filtro por aforo mínimo | `buscar(filtro con aforoMinimo=30, ...)` | Espacios 29/30/31 y activos | Solo espacios con aforo 30 o más | [ ] | [ ] |
| 9 | Filtro por categoría | `buscar(filtro con categoria=ESPACIO, ...)` | Activos y espacios | Solo espacios y `totalElements` correcto | [ ] | [ ] |
| 10 | Filtro por cada estado | `buscar(filtro con estado=X, ...)` para los 5 valores | Recursos en distintos estados | Solo recursos cuyo estado coincide, incluidos RESERVADO y BLOQUEO_ACADEMICO | [ ] | [ ] |
| 11 | Filtro por facultad | `buscar(filtro con facultadId, ...)` | Espacios de 2 facultades y activos | Solo espacios de esa facultad | [ ] | Regla pendiente: ¿los activos heredan la facultad del espacio? |
| 12 | Filtros combinados | `buscar(filtro con ESPACIO, DISPONIBLE, facultadId, ...)` | Mezcla de categorías, estados y facultades | Solo espacios DISPONIBLES de esa facultad | [ ] | [ ] |
| 13 | Filtros nulos o parciales | `buscar(filtro con campos null, ...)` | Filtro con solo `estado` informado | Los campos null se ignoran; se aplica solo `estado` | [ ] | [ ] |
| 14 | Filtros sin resultados | `buscar(filtro ACTIVO + EN_MANTENIMIENTO, ...)` | Ningún activo en mantenimiento | `content` vacío y `totalElements=0` | [ ] | [ ] |
| 15 | Excluye recursos de baja | `buscar(sinFiltros, ...)` tras dar de baja 3 recursos | 25 recursos, 3 de baja | `totalElements=22`; ninguno de los 3 aparece | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 9: Restricciones de integridad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Código duplicado | `recursoRepository.save(recurso)` | codigo=ACT-FI-001 (ya existe) | Lanza excepción de integridad (UNIQUE); no se inserta la fila | [ ] | Historia 1 FR-003; el plan define 409 |
| 2 | Código duplicado entre categorías | `recursoRepository.save(espacio)` | codigo de un activo existente | Lanza excepción de integridad (UNIQUE sobre toda la tabla) | [ ] | [ ] |
| 3 | Nombre nulo | `recursoRepository.save(recurso)` | nombre=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 4 | Categoría nula | `recursoRepository.save(recurso)` | categoria=null | Lanza excepción de integridad (NOT NULL) | [ ] | [ ] |
| 5 | Disponibilidad inexistente | `recursoRepository.save(recurso)` | disponibilidadId=UUID aleatorio | Lanza violación de FK | [ ] | [ ] |
| 6 | Estado físico inexistente | `recursoRepository.save(activo)` | estadoFisicoId=UUID aleatorio | Lanza violación de FK | [ ] | [ ] |
| 7 | Espacio contenedor inexistente | `recursoRepository.save(activo)` | espacioId=UUID aleatorio | Lanza violación de FK | [ ] | [ ] |
| 8 | Facultad inexistente | `recursoRepository.save(espacio)` | facultadId=UUID aleatorio | Lanza violación de FK | [ ] | El plan define 422 |
| 9 | Ubicación inexistente | `recursoRepository.save(espacio)` | ubicacionId=UUID aleatorio | Lanza violación de FK | [ ] | [ ] |
| 10 | Aforo en cero o negativo | `recursoRepository.save(espacio)` | aforo=0 y aforo=-5 | Se rechazan si la BD define CHECK `aforo IS NULL OR aforo > 0` | [ ] | La regla principal está en la capa de aplicación; el CHECK es respaldo |
| 11 | Caracteres especiales | `save` y `findById` | nombre=Cámara "HD", 4K Ñ | Se recupera idéntico (UTF-8 íntegro) | [ ] | [ ] |
| 12 | Texto tipo inyección SQL | `save` y `findByNombre` | nombre="'; DROP TABLE recurso;--" | Se guarda como texto; la tabla sigue intacta y el recurso se encuentra | [ ] | Historia 1 FR-009 |
| 13 | Nombre o código más largo que el límite | `recursoRepository.save(recurso)` | nombre de 256+ caracteres (límite a definir) | Lanza excepción de integridad; no se trunca en silencio | [ ] | [ ] |

--------------------------------------------------------------------------------

##### Section 10: Atomicidad
| Step # | Step Description | Path & Action | Test Data | Expected Results | Pass/Fail | Comments |
| :---: | ------ | ------ | ------ | ------ | :---: | ------ |
| 1 | Rollback ante fallo | En una transacción, `save(recurso)` seguido de un fallo forzado | Fallo de FK en el segundo guardado | No queda ninguna de las dos escrituras (historia 1 FR-010) | [ ] | [ ] |
| 2 | Eliminar espacio con activos dentro | Intentar borrar físicamente el espacio por SQL, o `darDeBaja(espacioId)` con activos dentro | Espacio con 2 activos | El borrado físico falla por FK; la baja lógica se decide en el caso de uso | [ ] | Regla pendiente: bloquear vs. permitir |

--------------------------------------------------------------------------------

##### Edge Cases & Boundary Conditions
* **EC-001** : `findByCodigo` distingue mayúsculas (`act-fi-001` vs `ACT-FI-001`): definir y probar la regla.
* **EC-002** : `findByCodigo(null)`: debe retornar vacío o fallar de forma controlada.
* **EC-003** : Dos `save` concurrentes con el mismo `codigo`: solo uno persiste y el otro falla por UNIQUE.
* **EC-004** : `buscar` con `page` negativo o `size` menor o igual a 0: definir si el repository lanza `IllegalArgumentException` o si la API lo bloquea antes.
* **EC-005** : `buscar` con `size` mayor a 20: la historia 2 fija máximo 20 por página; definir quién lo hace cumplir.
* **EC-006** : Con 10 000 recursos, `buscar` debe responder en menos de 2 s (historias 2 y 4; el plan indica 2.5 s).
* **EC-007** : Aulas sin facultad (NULL): la historia 4 las muestra solo a Dirección Universitaria, pero la historia 1 hace obligatoria la facultad.
* **EC-008** : `plazoDevolucion` negativo: definir si la BD lo rechaza (CHECK `>= 0`).

--------------------------------------------------------------------------------

#### Defect Severity Reference

| Severity Level | Code | Definition & Impact |
| ------ | :---: | ------ |
| **Critical** | **C** | System crash, data corruption, or complete block of critical user journey with no workaround. |
| **Major** | **M** | Core feature broken or significant functionality loss, but an awkward workaround exists. |
| **Small / Minor** | **S** | Cosmetic issue, minor UI alignment, typo, or non-blocking defect. |
