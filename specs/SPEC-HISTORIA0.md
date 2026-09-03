# Feature Specification: [CONSULTAR DISPONIBILIDAD DEL RECURSO]
**Created**: [31/08/2026]

### User Story 0 - [Consultar disponibilidad del recurso] (Priority: P1) 

[El monitor y/o el modulo 2 desea consultar la disponibilidad y el horario asignado de alguno de los recursos ya almacenados en el sistema, ya sea un activo o espacio, para conocer su disponibilidad actual y para una hora o fecha en específico de forma rápida y eficiente.]

**Por qué esta prioridad?** ; [La consulta de horarios libres y reservados es fundamental para el funcionamiento del sistema, de parte del actor que monitorea y del modulo 2 de la aplicación, sin esta el resto de funcionalidades objetivo del sistema no tienen forma de funcionar. ]

**Test independiente** ; [La historia de usuario se puede testear con una peticion de consulta que devuelva correctamente la disponibilidad actual de un recurso en específico y su horario asignado en caso de existir. ]

**Escenarios de aceptación** ;

1. **Scenario**: [Verificación exitosa de disponibilidad de un activo]
   - **Given** [Dado que soy el monitor de recursos o el modulo 2]
   - **When** [Consulto por la información del 'microscopio #5']
   - **Then** [Puedo ver su estado actual de disponibilidad (En uso)]
   
2. **Scenario**: [Verificación exitosa de disponibilidad de un espacio]
   - **Given** [Dado que soy el monitor de recursos o el modulo 2]
   - **When** [Consulto por la información del laboratorio 'Telecomunicaciones']
   - **Then** [Puedo ver su estado actual de disponibilidad (Disponible) y su horario de ocupación.]

3. **Scenario**: [Verificación exitosa de una categoria de activo]
   - **Given** [Dado que soy el monitor de recursos o el modulo 2]
   - **When** [Cuando ingreso un filtro de categoria para consultar los activos 'laptop']        #####################NEEDS CLARIFICATION##########################
   - **Then** [El sistema valida la informacion y devuelve la disponibilidad actual de los activos de esa categoria]
   
### Edge Cases

- What happens when [boundary condition]?
- How does system handle [error scenario]?

[Usuario consulta un recurso inexistente o eliminado]
El sistema debe devolver un mensaje claro de recurso no encontrado.

[Usuario consulta un recurso colocando una fecha/hora invalida]
El sistema debe devolver un mensaje claro de fecha inválida.

[Usuario consulta un recurso colocando una fecha/hora que ya pasó]
El sistema debe devolver un mensaje claro de fecha inválida.

[Usuario intenta hacer una consulta pero la disponibilidad cambia mientras el usuario está consultando (otro usuario reserva el recurso en ese instante).]
Implementar mecanismos de consistencia (transacciones, locks) para evitar inconsistencias y si todo falla mostrar un mensaje de "Disponibilidad actualizada, por favor reintente".

[Usuario consulta todos los recursos de la categoría "Laptops" cuando hay 800 activos.]
El sistema debe implementar paginación para tener un limite de resultados inmediatos.

## Requirements

### Functional Requirements

- **FR-001**: El sistema DEBE permitir [consultar el estado de disponibilidad actual (Disponible/Reservado/Bloqueo Academico/En uso/En mantenimiento) de cualquier recurso específico (activo o espacio) almacenado en el sistema]
- **FR-002**: El sistema DEBE permitir [consultar el horario asignado (horarios de ocupación) de un recurso específico, incluyendo hora de inicio y hora de finalización]
- **FR-003**: El sistema DEBE soportar [el filtrado de recursos por categoría (ej: "laptop", "microscopio", "workstation", "proyector") y devolver el estado de disponibilidad actual para todos los recursos que coincidan con el filtro]
- **FR-004**: El sistema DEBE corroborar [todas las entradas de usuario (nombres de recursos, filtros de categoría, campos de fecha/hora) para prevenir ataques de inyección y manejar caracteres especiales adecuadamente]
- **FR-005**: El sistema DEBE normalizar [todos los valores de fecha/hora a una zona horaria consistente (UTC -5 Colombia)]
- **FR-006**: El sistema DEBE implementar [paginación o limitación de resultados cuando las consultas devuelvan grandes conjuntos de datos, previniendo degradación del rendimiento y problemas de tiempo de espera]

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [Activo] [ID, nombre, tipo {enlazado a una categoria}, estado físico{enlazado a una categoria}, ubicacion{enlazado a un Espacio}]
- **[Entity 2]**: [Espacio] [ID, nombre, aforo maximo, equipamiento{enlazado a varios Activos}, ubicacion{enlazado a su propia entidad}, facultad{enlazado a su propia entidad} ]
- **[Entity 3]**: [tipo-Activo] [ID, nombre, descripcion] #####################NEEDS CLARIFICATION##########################
- **[Entity 4]**: [ubicacion] [ID, nombre, descripcion] {identidad que categoriza los espacios}
- **[Entity 5]**: [facultad] [ID, nombre, descripcion]

## Success Criteria 

### Measurable Outcomes

- **FR-001**: Los usuarios (monitor de recursos y módulo 2) pueden consultar disponibilidad para cualquier recurso específico y recibir una respuesta en menos de 5 segundos bajo condiciones de carga normal (hasta 100 consultas concurrentes).

- **FR-002**: El sistema maneja exitosamente 500 consultas de disponibilidad concurrentes durante períodos de uso pico sin que la degradación de respuesta supere los 10 segundos.

- **FR-003**: El 100% de las consultas de disponibilidad devuelven información precisa y consistente cuando se validan contra el estado actual de la base de datos durante escenarios de prueba.

- **FR-004**: El 100% de las consultas de recursos inválidos (recursos inexistentes) devuelven un mensaje de error "Recurso no encontrado" apropiado, eliminando fallos silenciosos o información de disponibilidad engañosa.

- **FR-005**: El 100% de las entradas de fecha/hora mal formadas son rechazadas con mensajes de error claros y accionables que guían al usuario hacia el formato correcto, reduciendo la confusión del usuario y tickets de soporte.
