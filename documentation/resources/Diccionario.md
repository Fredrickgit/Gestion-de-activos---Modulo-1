 # Entidades Principales: 

- **Recursos**: Entidad principal del proyecto padre de las entidades Activo y Espacio que engloba ambas entidades ya que comparten muchos atributos pero no todos.
        
- **Activo**: Representa cualquier bien o recurso físico tangible (como computadores, proyectores o microscopios) que la institución posee y gestiona. Se utiliza para controlar su ciclo de vida, disponibilidad, ubicación actual, asignación a espacios y plazos de devolución.

- **Espacio**: Corresponde a las instalaciones e infraestructura física de la universidad (como aulas, laboratorios de simulación o telecomunicaciones, e instalaciones específicas). Permite supervisar el aforo máximo permitido, su estado de ocupación/disponibilidad y el equipamiento fijo asignado.

- **Tipo**: Funciona como un catálogo de parametrización para agrupar y distinguir los distintos tipos de Recursos (por ejemplo, "laptop", "microscopio", "workstation"). Ayuda a estandarizar la clasificación, facilitar las búsquedas y filtrar los reportes del inventario.

- **Ubicación**: Entidad encargada de parametrizar y definir los lugares físicos o zonas específicas donde se localizan tanto los espacios como los activos del sistema.

- **Facultad**: Representa las unidades académicas u organizacionales de la institución (por ejemplo, "Ingeniería") a las cuales se encuentran adscritos o asociados los espacios físicos.

- **Estado físico**: Funciona como control de la condición en la que el recurso es entregado y devuelto (por ejemplo, "Óptimo").

- **Reporte**: Funciona como registro de la reserva y acontecimientos que se dieron en ella si es necesario, cuenta con todos los atributos del recurso además de la fecha de registro del reporte. 


# Entidades Secundarias: 

- **Historial del recurso**: Registro de eventos encargado de almacenar la trazabilidad de los cambios de estado en el ciclo de vida de los recursos. Guarda información clave como el usuario responsable, la fecha/hora exacta del cambio, y los estados anterior y nuevo.

- **Historial de reportes**: Registro encargado de almacenar la trazabilidad de los reportes generados en el sistema. Guarda información clave como ID del recurso, Nombre del recurso, Tipo, Ubicación física, Estado, Aforo si aplica y Fecha en la que se registró el reporte.