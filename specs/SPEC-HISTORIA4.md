# Historia de Usuario 4 - Generar Reportes (Prioridad: P2)

[La Dirección Universitaria y los monitores encargados de revisar los recursos necesitan generar reportes sobre la información del inventario. De esta manera, podrán consultar la información de los recursos registrados y facilitar su revisión y análisis.]

**¿Por qué esta prioridad?**: [Esta funcionalidad es importante porque facilita la consulta y el análisis de la información del inventario. Sin embargo, el sistema puede realizar sus funciones principales de registro y consulta de recursos sin la necesidad de generar reportes obligatorios.]

**Test independiente**: [Puede probarse de manera independiente generando un reporte a partir de los recursos registrados y verificando que la información mostrada corresponde con los datos almacenados en el sistema.]

**Escenarios de aceptación**:

1. **Scenario**: [Generar reporte general con atributos base]

-  **Given** [Soy un usuario de Dirección universitaria o monitor de recursos]
-  **When** [solicito generar un reporte del inventario sin aplicar filtros adicionales]
- **Then** [el sistema debe generar un reporte que incluya obligatoriamente los siguientes atributos base para cada recurso:
Identificador único (ID), 
nombre de recurso, 
tipo de recurso, 
Ubicación Fisica, 
Aforo/Capacidad (si aplica, o "N/A" en caso contrario) 
y fecha de registro del reporte]

2. **Scenario**: [Generar reporte filtrado por tipo de recurso o estado]
-  **Given** [soy un monitor encargado de espacios academicos]
-  **When** [aplico un filtro manual por "Tipo de recurso = Aula" o "Estado = disponible" y solicito el reporte]
-  **Then** [el sistema debe generar el reporte mostrando únicamente los recursos que coinciden con el filtro, manteniendo todos los atributos base obligatorios]


3. **Scenario** [Previsualizar reporte con paginación]
- **Given** [Soy un usuario de Dirección universitaria o monitor de recursos]
- **When** [Solicito ver la lista de reportes]
- **Then** [el sistema debe mostrar los reportes en paginas de 20 elementos estandarizados e incluir controles de navegación (anterior/siguiente) y un selector de pagina]


### Edge Cases

- [Usuario sin rol asignado. Si un usuario intenta acceder al módulo de reportes sin tener rol de Dirección Universitaria o Monitor de Recursos, el sistema debe denegar el acceso y mostrar un mensaje de permisos insuficientes.]

- [Aplica a toda la navegación. Si el usuario va a una página inexistente (ej. página 15 cuando solo hay 5), el sistema debe redirigir a la última página válida o mostrar cero resultados.]

- [Recursos sin facultad Asignada. las aulas que no tengan campo de "Facultad" (NULL) no seran visibles para ningún monitor; solo Dirección Universitaria podrá verlos y corregirlos]

- [Inyección y XSS en campos de filtro y ordenamiento: El usuario podría intentar enviar caracteres maliciosos, el sistema debe validar que los filtros sean seguros y asegurarse de que cualquier texto mostrado en la interfaz esté correctamente escapado para evitar la ejecución de scripts no deseados.]

- [Filtros sin resultados. Si el usuario aplica filtros que no coinciden con ningún recurso, el sistema debe mostrar un mensaje claro indicando que no hay datos para los criterios seleccionados, sin generar errores.]

- [Ordenamiento con valores repetidos: Si 100 recursos se llaman "Proyector", el sistema debe agregar el ID como segundo criterio de orden para garantizar que los registros no salten entre paginas]

- [Solicitudes concurrentes: Si múltiples usuarios solicitan reportes pesados al mismo tiempo, el sistema debe limitar el número de generaciones simultáneas y poner las demás en cola con un mensaje de espera.]

- [El usuario consulta todos los reportes cuando hay una gran cantidad de registros (ej: 10,000 reportes).
El sistema debe implementar paginación para mostrar los resultados de manera ordenada y eficiente, evitando sobrecarga en el cliente.]

## Requirements

### Functional Requirements
- **FR-001**: [El sistema DEBE permitir a los usuarios con roles de Dirección Universitaria y Monitor de Recursos generar reportes del inventario.]

- **FR-002**: [El sistema DEBE incluir en todo reporte generado los siguientes atributos base obligatorios: 
Identificador único (ID), 
nombre de recurso, 
tipo de recurso, 
Ubicación Fisica, 
Aforo/Capacidad (o "N/A") 
y fecha de registro del reporte]

- **FR_003**: [El sistema DEBE permitir filtrar el contenido del reporte por al menos dos criterios: Tipo de recurso y Estado actual.]

- **FR_004**: [El sistema DEBE mostrar la previsualización del reporte con paginación fija de 20 elementos por página, incluyendo controles de navegación (Anterior/Siguiente) y un selector de página numérico que permita ir a cualquier página.]

- **FR_005**: [El sistema DEBE soportar ordenamiento en la vista previa al hacer clic en las cabeceras de columna (al menos para: Nombre, Tipo, Estado y Fecha de registro), manteniendo la estabilidad mediante el ID como segundo criterio de orden]

- **FR_006**: [El sistema DEBE inyectar el filtro de facultad en la consulta de datos a nivel de backend, ignorando cualquier parámetro de facultad enviado desde el frontend para evitar manipulaciones.]

- **FR_007**: [El sistema DEBE sanitizar y validar todas las entradas del módulo de reportes]

- **FR_008**: [El sistema DEBE incluir metadatos visibles en el reporte: Fecha y hora de generación del reporte]

- **FR_009**: [El sistema DEBE manejar páginas inexistentes en la navegación: si el usuario solicita una página mayor que el total disponible, debe redirigir a la última página válida o mostrar un mensaje de "No hay más registros".]

- **FR_010**: [El sistema DEBE resetear la página a 1 automáticamente cuando el usuario aplique cualquier filtro (por Tipo o Estado) para evitar mostrar páginas vacías.]

## Success Criteria

### Measurable Outcomes

- **SC-001**: La carga de la primera página de previsualización (20 registros) debe tomar menos de 2 segundos en condiciones normales de red y servidor.

- **SC-002**: La navegación entre páginas (Anterior/Siguiente y salto a página específica) debe ser fluida y responder en menos de **1 segundo**.

- **SC-003**: [El sistema debe manejar al menos 5 usuarios concurrentes generando reportes o navegando en la previsualización sin caídas ni degradación significativa del rendimiento.]

- **SC-004**: [El ordenamiento de columnas debe ser estable: al navegar entre páginas, ningún registro debe aparecer en más de una página ni desaparecer (verificado con conjuntos de datos que tengan valores repetidos en el campo ordenado).]

- **SC-005**: [En caso de filtros sin resultados, el sistema debe mostrar un mensaje claro ("No hay recursos que coincidan con los filtros") en menos de 2 segundos, sin generar errores ni páginas vacías.]

- **SC-006**: [La usabilidad inicial (primera generación de reporte por parte de un usuario sin capacitación previa) debe tener una tasa de éxito superior al 90% en pruebas de usuario.]

- **SC-007**: [El selector de página debe manejar correctamente entradas fuera de rango: si el usuario ingresa un número mayor al total de paginas, debe redirigir a la última página disponible sin mostrar errores.]


- **SC-008**: [Los reportes generados deben manejar correctamente caracteres especiales (tildes, eñes, comillas, comas) en el **100%** de los casos, sin romper la estructura del reporte ni mostrar caracteres corruptos.]
