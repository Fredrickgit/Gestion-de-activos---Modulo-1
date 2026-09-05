# Feature Specification: [CONSULTAR INVENTARIOS]
**Created**: [29/08/2026]

### User Story 1 - [Consultar Inventarios] (Priority: P2)

[La Dirección Universitaria y los monitores encargados de revisar los recursos necesitan un medio para consultar el inventario de recursos. De esta manera, podrán visualizar los recursos registrados y sus respectivos atributos. Asimismo, si necesitan revisar un recurso en concreto, podrán encontrarlo mediante la búsqueda utilizando estos mismos atributos.]

**Por qué esta prioridad?**; [Es importante para la utilidad del sistema permitir el registro y la consulta de los recursos almacenados en la base de datos, ya que sin esta funcionalidad los usuarios no podrían acceder a la información de los activos y espacios registrados, limitando la operatividad del sistema.]

**Test independiente**; [Puede probarse de manera independiente verificando que al ingresar a la pantalla de inventario se muestren todos los recursos registrados con sus atributos, y que al utilizar los filtros de búsqueda aparezcan únicamente los recursos que coinciden con la búsqueda.]

**Escenarios de aceptación**;

1. **Scenario**: [Visualización de inventario]
   - **Given** [Dado que soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **When** [Accedo al inventario para revisar los recursos]
   - **Then** [El sistema debe mostrar los recursos previamente registrados con todos sus atributos visibles (nombre, tipo, ubicación, estado, etc.)]

2. **Scenario**: [Buscar un recurso por atributo]
   - **Given** [Dado que soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **When** [Realizo una consulta utilizando un criterio válido, como el nombre del recurso, aforo, ID o facultad]
   - **Then** [El sistema debe mostrar los recursos cuyos atributos coincidan con los criterios de búsqueda]

3. **Scenario**: [Inventario sin resultados]
   - **Given** [Dado que soy un usuario de Dirección Universitaria o Monitor de Recursos]
   - **When** [Realizo una consulta y los criterios de búsqueda no coinciden con ningún recurso]
   - **Then** [El sistema debe informar que no se encontraron resultados para los criterios de búsqueda]

### Edge Cases

- [El usuario intenta acceder al inventario sin tener permisos de Dirección Universitaria o Monitor de Recursos]
El sistema debe validar los permisos del usuario y devolver un mensaje de acceso denegado.

- [El usuario realiza una consulta con un filtro que contiene caracteres especiales o maliciosos (inyección SQL)]
El sistema debe sanitizar y validar todas las entradas del usuario para prevenir ataques de inyección.

- [El usuario intenta buscar un recurso con un atributo que no existe en el sistema (ej: buscar por "color" cuando el sistema solo soporta nombre, ID, aforo, facultad)]
El sistema debe devolver un mensaje claro indicando que el atributo de búsqueda no es válido.

- [El inventario no mostrará los recursos dados de baja]
El sistema no mostrará los recursos de la base de datos que se hayan dado de baja, pero seguirán existiendo. ################# falta definir

- [El usuario consulta todos los recursos del inventario cuando hay una gran cantidad de registros (ej: 10,000 recursos)]
El sistema debe implementar paginación de máximo 20 recursos por página para mostrar los resultados de manera ordenada y eficiente, evitando sobrecarga en el cliente.

- [El usuario realiza una búsqueda con criterios múltiples pero algunos de ellos están vacíos o son inválidos]
El sistema debe manejar filtros parciales, ignorando los criterios vacíos y aplicando solo los válidos, o en su defecto mostrar un mensaje de error claro.

## Requirements

### Functional Requirements

- **FR-001**: El sistema DEBE permitir [visualizar todos los recursos (activos y espacios) registrados en el sistema con todos sus atributos relevantes: nombre, tipo, ubicación, estado físico (para activos), aforo máximo (para espacios), facultad asociada, entre otros]

- **FR-002**: El sistema DEBE soportar [búsqueda de recursos utilizando criterios como nombre del recurso, ID, aforo (para espacios), o facultad, mostrando únicamente los recursos que coincidan con el criterio de búsqueda]

- **FR-003**: El sistema DEBE mostrar [un mensaje claro de "No se encontraron resultados" cuando la búsqueda no coincida con ningún recurso en el inventario]

- **FR-004**: El sistema DEBE validar [los permisos de acceso del usuario (Dirección Universitaria o Monitor de Recursos) antes de mostrar el inventario, devolviendo un mensaje de acceso denegado si no tiene autorización]

- **FR-005**: El sistema DEBE sanitizar [todas las entradas de usuario (nombres de recursos, filtros de búsqueda, IDs) para prevenir ataques de inyección y manejar caracteres especiales adecuadamente]

- **FR-006**:El sistema DEBE implementar [paginación con un máximo de 20 recursos por página cuando las consultas devuelvan grandes conjuntos de datos, previniendo degradación del rendimiento y problemas de tiempo de espera]

- **FR-007**: El sistema DEBE [validar que los atributos de búsqueda utilizados existan en el sistema, devolviendo un mensaje de error si el usuario intenta buscar por un atributo no soportado]

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [Activo] [ID, nombre, tipo {enlazado a una categoria}, estado físico{enlazado a una categoria}, ubicacion{enlazado a un Espacio}, estado de disponibilidad, plazo máximo de devolución]

- **[Entity 2]**: [Espacio] [ID, nombre, aforo maximo, equipamiento{enlazado a varios Activos}, ubicacion{enlazado a su propia entidad}, facultad{enlazado a su propia entidad}, estado de disponibilidad ]

- **[Entity 3]**: [tipo-Activo] [ID, nombre, descripción]

- **[Entity 4]**: [ubicación-Espacio] [ID, nombre, descripción]

- **[Entity 5]**: [facultad] [ID, nombre, descripción]

## Success Criteria

### Measurable Outcomes

- **FR-001**: El 100% de los usuarios autorizados (Dirección Universitaria y Monitores de Recursos) pueden acceder al inventario y visualizar todos los recursos registrados en menos de 2 segundos bajo condiciones de carga normal (hasta 100 consultas concurrentes).

- **FR-002**: El sistema maneja exitosamente 500 consultas de inventario concurrentes durante períodos de uso pico sin que la degradación de respuesta supere los 5 segundos.

- **FR-003**: El 95% de las búsquedas de recursos devuelven resultados precisos cuando se validan contra la base de datos, con una tasa de falsos negativos inferior al 2% (recursos existentes que no aparecen en la búsqueda).

- **FR-004**: El 100% de las consultas de recursos con filtros inválidos devuelven un mensaje de error claro y accionable, guiando al usuario hacia los atributos de búsqueda soportados.

- **FR-005**:El sistema reduce en un 90% los tiempos de espera para consultas de inventario mediante la implementación de paginación de máximo 20 recursos por página para conjuntos de datos que superen ese límite.

- **FR-006**: El 100% de los intentos de acceso no autorizados al inventario son bloqueados con un mensaje de "Acceso denegado", garantizando la seguridad de la información.
