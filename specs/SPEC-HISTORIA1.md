# Feature Specification: [CREACIÓN DE UN RECURSO]
**Created**: [29/08/2026]

### User Story 1 - [Creación de un recurso] (Priority: P1)

[Dirección Universitaria desea agregar un nuevo recurso al sistema, ya sea un activo o espacio, dependiendo del tipo de recurso Dirección Universitaria quiere agregar los atributos respectivos del recurso (nombre; ID, aforo máximo, equipamiento fijo y facultad; o ID 'Placa de inventario', tipo, estado físico y ubicación)]

**Por qué esta prioridad?**; [El modelo de creación es la base de los recursos los cuales son el eje central de la aplicación, esto es importante para la creación, el crecimiento y el mantenimiento del sistema a largo plazo. Esta opción está definida por prerrequisitos dependiendo del tipo de recurso lo que permite a los usuarios explorar las variedades de opciones de creación de estos.]

**Test independiente**; [Puede probarse de manera independiente verificando la creación exitosa de un activo o espacio a través de una interfaz o API, constatando que el registro se almacene en la base de datos con todos sus atributos requeridos y su estado inicial "Disponible", y que el sistema rechace solicitudes con campos obligatorios vacíos o IDs duplicados.]

**Escenarios de aceptación**;

1. **Scenario**: [Creación de activo exitosa]
   - **Given** [Dado que soy un usuario de Dirección Universitaria debidamente autenticado en el sistema]
   - **When** [Ingreso el nombre, placa de inventario (ID), tipo "computador", ubicación "laboratorio simulación", estado físico "nuevo" de un recurso nuevo y lo envío]
   - **Then** [El sistema valida la información y se crea el activo en la base de datos con todos sus atributos y su estado inicial en "Disponible"]

2. **Scenario**: [Información faltante en activo]
   - **Given** [Dado que soy un usuario de Dirección Universitaria]
   - **When** [Ingreso el formulario de creación sin llenar el atributo obligatorio 'tipo' (o cualquier otro campo requerido) de un activo y lo envío]
   - **Then** [El sistema indica la información faltante mediante un mensaje claro y evita la creación del activo]

3. **Scenario**: [Creación de espacio exitosa]
   - **Given** [Dado que soy un usuario de Dirección Universitaria debidamente autenticado en el sistema]
   - **When** [Ingreso el nombre, ID del espacio, aforo máximo 30, equipamiento fijo "25 computadoras, 25 sillas", ubicación "hangares", facultad "ingeniería" y lo envío]
   - **Then** [El sistema valida la información y se crea el espacio en la base de datos con todos sus atributos y su estado inicial en "Disponible"]

4. **Scenario**: [Información faltante en un espacio]
   - **Given** [Dado que soy un usuario de Dirección Universitaria]
   - **When** [Ingreso el formulario de creación sin llenar el atributo obligatorio 'aforo máximo' (o cualquier otro campo requerido) de un espacio y lo envío]
   - **Then** [El sistema indica la información faltante mediante un mensaje claro y evita la creación del espacio]

5. **Scenario**: [Identificador único duplicado]
   - **Given** [Dado que soy un usuario de Dirección Universitaria y ya existe en la base de datos un recurso con la placa de inventario o ID especificado]
   - **When** [Intento registrar un nuevo recurso con esa misma placa de inventario o ID de espacio]
   - **Then** [El sistema bloquea la transacción y devuelve un mensaje de error indicando que el identificador ya se encuentra registrado]

6. **Scenario**: [Control de acceso para creación de recursos]
   - **Given** [Dado que soy un usuario no autorizado (sin rol de Dirección Universitaria, ej. Monitor de Recursos o estudiante)]
   - **When** [Intento acceder a la interfaz de creación o enviar una petición para crear un recurso]
   - **Then** [El sistema bloquea la acción y devuelve un mensaje de acceso denegado]

### Edge Cases

- [El usuario intenta acceder al módulo o funcionalidad de creación sin tener permisos de Dirección Universitaria]
El sistema debe validar los permisos del usuario y devolver un mensaje de acceso denegado.

- [El usuario intenta registrar un activo o espacio con un identificador (ID o Placa de inventario) que ya existe en la base de datos]
El sistema debe verificar la unicidad del identificador antes de realizar la inserción y devolver un mensaje de error claro indicando que el identificador ya está en uso.

- [El usuario intenta crear un espacio ingresando un valor de aforo máximo no válido (menor o igual a cero, número no entero o caracteres no numéricos)]
El sistema debe validar que el aforo sea un número entero positivo mayor a cero y rechazar la creación informando el error específico.

- [El usuario realiza el envío del formulario con campos que contienen caracteres especiales o potencialmente maliciosos (inyección SQL o scripts XSS)]
El sistema debe sanitizar y validar todas las entradas de texto para prevenir ataques de inyección y garantizar la integridad de los datos.

- [Al crear un espacio y definir su equipamiento fijo]
El sistema referencia a activos previamente creados en el inventario al definir el equipamiento fijo.

- [Al registrar un activo o espacio el estado de disponibilidad inicial debe ser asignado automáticamente por el sistema ("Disponible")]
El sistema debe definir "Disponible" como el estado default.

- [El usuario intenta asociar un activo a un espacio que no existe, o un espacio a una facultad no registrada en los catálogos del sistema]
El sistema debe validar la integridad referencial y devolver un mensaje de error indicando que la entidad foránea seleccionada es inválida o no existe.

- [El usuario envía campos de texto que exceden el límite máximo de caracteres establecido para nombres, códigos o descripciones]
El sistema debe truncar o rechazar la solicitud con un mensaje informativo indicando el límite de caracteres permitido para cada campo.

- [Se produce un fallo de conectividad o error en la base de datos durante la transacción de creación del recurso y sus relaciones]
El sistema debe ejecutar una reversión atómica (rollback) garantizando que no se guarden registros inconsistentes o huérfanos, y notificar al usuario sobre el fallo temporal para que reintente.

## Requirements

### Functional Requirements

- **FR-001**: El sistema DEBE permitir a los usuarios de Dirección Universitaria registrar nuevos activos físicos en el sistema, capturando de forma obligatoria los atributos: nombre, ID único (Placa de inventario), tipo de activo, estado físico inicial y ubicación (espacio al que pertenece).

- **FR-002**: El sistema DEBE permitir a los usuarios de Dirección Universitaria registrar nuevos espacios académicos en el sistema, capturando de forma obligatoria los atributos: nombre, ID único del espacio, aforo máximo (capacidad de personas), equipamiento fijo y facultad a la que pertenece.

- **FR-003**: El sistema DEBE validar la unicidad del identificador (Placa de inventario para activos e ID para espacios) antes de persistir cualquier registro en la base de datos, impidiendo duplicidades y mostrando un mensaje de error claro.

- **FR-004**: El sistema DEBE validar que todos los atributos obligatorios requeridos para cada tipo de recurso estén completos y no contengan valores vacíos o nulos antes de permitir su creación.

- **FR-005**: El sistema DEBE validar que el valor de aforo máximo para los espacios sea un número entero positivo mayor a cero (aforo > 0) y acorde a las capacidades institucionales permitidas.

- **FR-006**: El sistema DEBE asignar automáticamente un estado de disponibilidad inicial ("Disponible") a todo recurso recién creado, permitiendo su visualización inmediata en inventario y disponibilidad.

- **FR-007**: El sistema DEBE validar la integridad referencial de los atributos parametrizados, verificando que el tipo de activo, el espacio de ubicación y la facultad existan previamente en las entidades del sistema.

- **FR-008**: El sistema DEBE verificar y validar los permisos de acceso del usuario antes de desplegar el formulario o procesar la solicitud de creación, restringiendo la operación únicamente al rol de Dirección Universitaria y devolviendo un mensaje de acceso denegado si no cuenta con autorización.

- **FR-009**: El sistema DEBE sanitizar y validar todas las entradas de usuario (nombres, identificadores, descripciones, equipamiento) para prevenir ataques de inyección (SQL, NoSQL, XSS) y manejar caracteres especiales adecuadamente.

- **FR-010**: El sistema DEBE manejar la persistencia mediante transacciones atómicas que garanticen la consistencia de los datos, revirtiendo la operación (rollback) en caso de fallo técnico e informando al usuario.

### Key Entities *(include if feature involves data)*

- **[Entity 1]**: [Activo] [ID / Placa de inventario, nombre, tipo {enlazado a tipo-Activo}, estado físico, ubicación {enlazado a espacio}, estado de disponibilidad inicial (ej. Disponible)]

- **[Entity 2]**: [Espacio] [ID, nombre, aforo máximo, equipamiento {enlazado a activo o catálogo de equipamiento}, ubicación {enlazado a su propia entidad}, facultad {enlazado a su propia entidad}, estado de disponibilidad inicial (ej. Disponible)]

- **[Entity 3]**: [tipo-Activo] [ID, nombre, descripción]

- **[Entity 4]**: [ubicacion] [ID, nombre, descripcion] {identidad que categoriza los espacios}

- **[Entity 5]**: [facultad] [ID, nombre, descripción]

- **Entidades representadas en el diagrama ENTIDAD-RELACION ubicado en /Gestion-de-activos---Modulo=1/documentation/resources/Diagrama E-R.png**

## Success Criteria

### Measurable Outcomes

- **FR-001**: El 100% de los usuarios autorizados (Dirección Universitaria) pueden registrar un nuevo activo o espacio en el sistema con un tiempo de respuesta de procesamiento y guardado inferior a 5 segundos bajo condiciones normales de carga (hasta 100 operaciones concurrentes).

- **FR-002**: El sistema maneja exitosamente hasta 500 solicitudes de registro concurrentes en períodos pico manteniendo un tiempo de respuesta inferior a 5 segundos sin pérdida de información.

- **FR-003**: El 100% de los intentos de registro con identificadores duplicados (ID o Placa de inventario ya existente) son interceptados y bloqueados por el sistema, previniendo en su totalidad conflictos de clave primaria o duplicidad de registros.

- **FR-004**: El 100% de los formularios con campos obligatorios vacíos o datos con formato inválido (ej. aforo negativo o texto no permitido) son rechazados por el sistema, mostrando mensajes de validación claros y específicos para cada campo afectado.

- **FR-005**: El 100% de los recursos creados exitosamente quedan inmediatamente persistidos y visibles para consulta en el inventario.

- **FR-006**: El 100% de los intentos de creación realizados por usuarios no autorizados (roles sin permisos de Dirección Universitaria) son denegados con un mensaje de "Acceso denegado" y registrados en el log de auditoría.

- **FR-007**: El sistema mantiene un 100% de consistencia transaccional, asegurando que ninguna falla durante la persistencia deje entidades a medio crear o registros huérfanos en la base de datos.
