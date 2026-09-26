# Implementation Plan: Módulo 1 - Gestión de Activos y Espacios
    **Date**: [21/Sept/2026]
    **Spec**: [...Modulos-gestion-de-activos.md]
    
    
## Summary

El MODULO 1 de GESTION DE ACTIVOS UNIVERSITARIOS es la fuente de verdad única y autoridad del inventario físico de recursos de la Universidad del Magdalena (UNIMAG), custodiando Activos y Espacios. Es un programa API REST para consultas de catálogo y disponibilidad (/api/v1/), acoplada a un consumidor transaccional que procesa eventos de Pila/Cola con garantías ACID, idempotencia y auditoría de cambios de estado.
      
      
## Technical Context

    **Language/Version**: [Java LTS 21]
    **Primary Dependencies**: [Springboot,React] 
    **Storage**: [Base de datos en PostgreSQL]
    **Testing**: [Mockito, testcontainers]
    **Target Platform**: [Dispositivos Windows/Linux/Mac en desktop]
    **Project Type**: [Web para manejo de una base de datos alojada por la Universidad]
    **Performance Goals**: [Fuerte seguridad transaccional, velocidades de consulta de disponibilidad e inventario menores a 2.5 segundos con conexión estable y paginación, estabilidad con gran carga de datos a la base de datos y/o peticiones]
    **Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]
    **Scale/Scope**: [+25.0000 usuarios diferentes con capacidad de revisión de la BD, ±1k usuarios en un día hábil de uso]


### Documentation (this feature)

```text
gestion-de-activos---modulo-1/documentation/
├── /resources 
    └── /
├── /screens
    └── /    
```


### Source Code (repository root)

```text
gestion-de-activos---modulo-1/backend/
├── src/
│   │
│   ├── domain/                         # CAPA 1: Negocio puro (sin dependencias)
│   │   ├── entities/                   # Modelos de dominio
│   │   │   ├── example              # Activo, Espacio, EstadoDisponibilidad
│   │   │   └── example              # Franjas horarias, bloqueos
│   │   ├── repositories/               # Interfaces / Contratos (Ports)
│   │   │   └── example   # "class RecursoRepository(ABC): ..."
│   │   └── exceptions/                 # Errores propios del negocio
│   │       └── example   # RecursoNoEncontradoError
│   │
│   ├── application/                    # CAPA 2: Casos de uso
│   │   ├── use_cases/
│   │   │   ├── consulta_disponibilidad  # HU-0: Consulta por ID o categoria
│   │   │   └── creacion_recurso
│   │   │   └── actualización_recurso
│   │   │   └── generar_reporte
│   │   │   └── consulta_inventarios
│   │   └── dtos/                       # Estructuras de datos de entrada/salida
│   │       ├── consulta_request_dto
│   │       └── disponibilidad_response_dto
│   │
│   ├── infrastructure/                 # CAPA 3: Herramientas y adaptadores externos
│   │   ├── database/                   # Configuracion de BD y modelos ORM
│   │   │   ├── connection
│   │   │   └── models               # Tablas en BD (SQLAlchemy, Mongo, etc.)
│   │   ├── repositories/               # Implementaciones reales de los contratos
│   │   │   └── postgres_recurso_repo   # Implementa RecursoRepository
│   │   └── external/                   # Comunicacion con otros servicios
│   │       └── modulo2_client
│   │       └── modulo3_client          # Cliente para interactuar con Modulo 2
│   │
│   └── presentation/                   # CAPA 4: Controladores y Web API
│       ├── api/                        # Rutas / Endpoints REST
│       │   └── v1/
│       │       └── recursos_controller
│       ├── schemas/                    # Validacion de payloads (Pydantic / Joi / DTOs web)
│       │   └── recurso_schemas
│       └── middlewares/                # Manejadores globales de errores, CORS, auth
│           └── error_handler
│
├── tests/                              # Pruebas automatizadas
│   ├── unit/                           # Pruebas a Casos de Uso y Entidades (rapidas, sin BD)
│   └── integration/                    # Pruebas a Repositorios reales y Endpoints
│
└── main                # Punto de entrada e Inyeccion de Dependencias

```


## Architecture Design:

**Estructura elegida: CLEAN ARCHITECTURE**

Definición:

El objetivo principal es separar las reglas de negocio de los detalles tecnicos
(como la base de datos, los frameworks web o las APIs externas).
LA REGLA DE ORO (Regla de Dependencia):
Las dependencias en el codigo SOLO pueden apuntar hacia adentro:

  [ Presentation / API ] 
         ↓ (depende de)
  [ Infrastructure ] 
         ↓ (depende de)
  [ Application (Casos de Uso) ] 
         ↓ (depende de)
  [ Domain (Reglas de Negocio) ]

* El DOMAIN nunca debe importar nada de Application, Infrastructure ni API.
* Los frameworks (FastAPI, Express, Spring, etc.) y bases de datos son "detalles" 
  reemplazables que viven en la capa mas externa.
* Si mañana cambias de PostgreSQL a MongoDB o de framework web, el corazon 
  (Dominio y Casos de Uso) no cambia ni una sola linea.
  
**Orden de Implementación por Capas (Clean Architecture):**

- Paso 0: Test individuales en cada paso
- Paso 1: Domain Entities, Enums y Excepciones propias (sin dependencias).
- Paso 2: Domain Repository Interfaces (definición de contratos).
- Paso 3: Application DTOs y Casos de Uso (orquestación y reglas).
- Paso 4: Infrastructure Repositories (implementación SQL) y Messaging Consumers (colas).
- Paso 5: Presentation Controllers (endpoints REST FastAPI) y validadores Pydantic.

       
### Technical extras
  
**A. CONTRATO DE COMUNICACIÓN ASÍNCRONA (PILA / COLA)**
   - **Propósito y Principios:** Comunicación asíncrona, transaccional y priorizada (Garantía ACID, At-least-once, Orden FIFO/LIFO, ACK + Reintentos, DLQ).
   - **Rol del Módulo 1:** Consumidor primario de eventos críticos (uso, daños) para transiciones de estado, productor secundario de cambios manuales y garante de idempotencia.
   - Eventos a consumir (Inbound): Notificaciones de inicio de uso/reserva (M2), `reserva.finalizada` y `reporte.daños` (M3).
   - Eventos a producir (Outbound): `estado.actualizado` ante cambios manuales de la Dirección Universitaria.
   - Estrategia de Dead Letter Queue (DLQ): Envío de mensajes fallidos tras N reintentos para revisión manual.

**B. CONTRATOS DE API REST (ENDPOINTS SÍNCRONOS)**
   - **Principios de Diseño:** Stateless, cliente-servidor, cacheable, interfaz uniforme y versionado `/api/v1/`.
   - **Rol del Módulo 1:** Servidor principal de inventario (autoridad de activos, espacios y estados) y cliente ocasional para validaciones cruzadas.
   - GET  /api/v1/recursos                  -> Listado paginado con filtros (tipo, estado, facultad).
   - GET  /api/v1/recursos/{id}             -> Detalle completo del activo o espacio.
   - GET  /api/v1/recursos/{id}/disponibilidad -> Estado de disponibilidad actual.
   - POST /api/v1/activos                   -> Registro de nuevo activo físico.
   - POST /api/v1/espacios                  -> Registro de nuevo espacio físico.
   - PATCH /api/v1/recursos/{id}/estado     -> Actualización manual de estado (con registro de auditoría).

**C. ESTRATEGIA DE TESTING Y COBERTURA**
   - Definir cobertura mínima (>80% en Application y Domain).
   - Pruebas unitarias aisladas para la máquina de estados de recursos.
   - Pruebas de integración con base de datos real usando Testcontainers.
   - Pruebas de idempotencia simulando entrega duplicada de eventos de cola.
  

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create project structure per implementation plan
- [ ] T002 Initialize [language] project with [framework] dependencies
- [ ] T003 Configure linting and formatting tools

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

Examples of foundational tasks (adjust based on your project):

- [ ] T004 Setup database schema and migrations framework
- [ ] T005 Implement authentication/authorization framework
- [ ] T006 Setup API routing and middleware structure
- [ ] T007 Create base models/entities that all stories depend on
- [ ] T008 Configure error handling and logging infrastructure
- [ ] T009 Setup environment configuration management

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - [Title] (Priority: P1) 

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 1 

- [ ] T010 [P] [US1] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T011 [P] [US1] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 1

- [ ] T012 [P] [US1] Create [Entity1] model in src/models/[entity1].py
- [ ] T013 [P] [US1] Create [Entity2] model in src/models/[entity2].py
- [ ] T014 [US1] Implement [Service] in src/services/[service].py (depends on T012, T013)
- [ ] T015 [US1] Implement [endpoint/feature] in src/[location]/[file].py
- [ ] T016 [US1] Add validation and error handling
- [ ] T017 [US1] Add logging for user story 1 operations

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - [Title] (Priority: P2)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 2 

- [ ] T018 [P] [US2] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T019 [P] [US2] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 2

- [ ] T020 [P] [US2] Create [Entity] model in src/models/[entity].py
- [ ] T021 [US2] Implement [Service] in src/services/[service].py
- [ ] T022 [US2] Implement [endpoint/feature] in src/[location]/[file].py
- [ ] T023 [US2] Integrate with User Story 1 components (if needed)

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - [Title] (Priority: P3)

**Goal**: [Brief description of what this story delivers]

**Independent Test**: [How to verify this story works on its own]

### Tests for User Story 3 

- [ ] T024 [P] [US3] Contract test for [endpoint] in tests/contract/test_[name].py
- [ ] T025 [P] [US3] Integration test for [user journey] in tests/integration/test_[name].py

### Implementation for User Story 3

- [ ] T026 [P] [US3] Create [Entity] model in src/models/[entity].py
- [ ] T027 [US3] Implement [Service] in src/services/[service].py
- [ ] T028 [US3] Implement [endpoint/feature] in src/[location]/[file].py

**Checkpoint**: All user stories should now be independently functional

---

[Add more user story phases as needed, following the same pattern]

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] TXXX Documentation updates in docs/
- [ ] TXXX Code cleanup and refactoring
- [ ] TXXX Performance optimization across all stories
- [ ] TXXX Additional unit tests in tests/unit/
- [ ] TXXX Security hardening

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3)
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - May integrate with US1 but should be independently testable
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - May integrate with US1/US2 but should be independently testable

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority
- Tests after implementation

## Notes

- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Verify tests pass
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence
