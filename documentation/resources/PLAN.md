## Technical Context

**Language/Version**: [e.g., Python 3.11, Swift 5.9, Rust 1.75 or NEEDS CLARIFICATION]  
**Primary Dependencies**: [e.g., FastAPI, UIKit, LLVM or NEEDS CLARIFICATION]  
**Storage**: [if applicable, e.g., PostgreSQL, CoreData, files or N/A]  
**Testing**: [e.g., pytest, XCTest, cargo test or NEEDS CLARIFICATION]  
**Target Platform**: [e.g., Linux server, iOS 15+, WASM or NEEDS CLARIFICATION]
**Project Type**: [single/web/mobile - determines source structure]  
**Performance Goals**: [domain-specific, e.g., 1000 req/s, 10k lines/sec, 60 fps or NEEDS CLARIFICATION]  
**Constraints**: [domain-specific, e.g., <200ms p95, <100MB memory, offline-capable or NEEDS CLARIFICATION]  
**Scale/Scope**: [domain-specific, e.g., 10k users, 1M LOC, 50 screens or NEEDS CLARIFICATION]

## Project Structure

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
│   │   │   ├── recurso.py              # Activo, Espacio, EstadoDisponibilidad
│   │   │   └── horario.py              # Franjas horarias, bloqueos
│   │   ├── repositories/               # Interfaces / Contratos (Ports)
│   │   │   └── recurso_repository.py   # "class RecursoRepository(ABC): ..."
│   │   └── exceptions/                 # Errores propios del negocio
│   │       └── recurso_exceptions.py   # RecursoNoEncontradoError
│   │
│   ├── application/                    # CAPA 2: Casos de uso
│   │   ├── use_cases/
│   │   │   ├── consultar_disponibilidad.py  # HU-0: Consulta por ID o categoria
│   │   │   └── listar_recursos.py
│   │   └── dtos/                       # Estructuras de datos de entrada/salida
│   │       ├── consulta_request_dto.py
│   │       └── disponibilidad_response_dto.py
│   │
│   ├── infrastructure/                 # CAPA 3: Herramientas y adaptadores externos
│   │   ├── database/                   # Configuracion de BD y modelos ORM
│   │   │   ├── connection.py
│   │   │   └── models.py               # Tablas en BD (SQLAlchemy, Mongo, etc.)
│   │   ├── repositories/               # Implementaciones reales de los contratos
│   │   │   └── postgres_recurso_repo.py# Implementa RecursoRepository
│   │   └── external/                   # Comunicacion con otros servicios
│   │       └── modulo2_client.py       # Cliente para interactuar con Modulo 2
│   │
│   └── presentation/                   # CAPA 4: Controladores y Web API
│       ├── api/                        # Rutas / Endpoints REST
│       │   └── v1/
│       │       └── recursos_controller.py
│       ├── schemas/                    # Validacion de payloads (Pydantic / Joi / DTOs web)
│       │   └── recurso_schemas.py
│       └── middlewares/                # Manejadores globales de errores, CORS, auth
│           └── error_handler.py
│
├── tests/                              # Pruebas automatizadas
│   ├── unit/                           # Pruebas a Casos de Uso y Entidades (rapidas, sin BD)
│   └── integration/                    # Pruebas a Repositorios reales y Endpoints
│
└── main                # Punto de entrada e Inyeccion de Dependencias

```

**Structure Decision**:

Estructura elegida: CLEAN ARCHITECTURE

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
