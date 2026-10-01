# Entidades Principales: 

- **Recursos**: Entidad principal del proyecto padre de las entidades Activo y Espacio que engloba ambas entidades ya que comparten muchos atributos pero no todos.
        
- **Activo**: Representa cualquier bien o recurso físico tangible (como computadores, proyectores o microscopios) que la institución posee y gestiona. Se utiliza para controlar su ciclo de vida, disponibilidad, ubicación actual, asignación a espacios y plazos de devolución.

- **Espacio**: Corresponde a las instalaciones e infraestructura física de la universidad (como aulas, laboratorios de simulación o telecomunicaciones, e instalaciones específicas). Permite supervisar el aforo máximo permitido, su estado de ocupación/disponibilidad y el equipamiento fijo asignado.

- **Tipo**: Funciona como un catálogo de parametrización para agrupar y distinguir los distintos tipos de Recursos (por ejemplo, "laptop", "microscopio", "workstation"). Ayuda a estandarizar la clasificación, facilitar las búsquedas y filtrar los reportes del inventario.
Lista de tipos con atributo NOMBRE y DESCRIPCIÓN;
SILLA_GENERAL -- Equipo de aula de clase
MANIQUI_MEDICINA -- Material didáctico
QUEMADOR_BUNSEN -- Utensilio de laboratorio
BASCULA_DIGITAL -- Equipo de medición
BURETA_GRADUADA -- Utensilio de laboratorio
BATIDOR_MAGNETICO -- Utensilio de laboratorio
CÁMARA_DIGITAL -- Equipo fotográfico
ENRUTADOR_WIFI -- Equipo de red
ESPECTROFOTÓMETRO -- Equipo de laboratorio
IMPRESORA_3D -- Equipo de fabricación digital
IMPRESORA_LÁSER -- Equipo de oficina
MICROSCOPIO -- Utensilio de laboratorio
SISTEMA_UPS -- Equipo eléctrico
MONITOR -- Equipo informático
PORTÁTIL_LAPTOP -- Equipo informático
PC_ESCRITORIO -- Equipo informático
WORKSTATIO_ESCRITORIO -- Equipo informático
MOUSE -- Equipo informático
TECLADO -- Equipo informático
PROYECTOR_MULTIMEDIA -- Equipo audiovisual

- **Ubicación (Activo)**: Atributo de un ACTIVO que señala a la entidad ESPACIO de manera que se pueda catalogar o conocer todos los activos almacenados dentro de un espacio de la universidad (Computadores, microscopios, proyectores en un aula, etc).

- **Ubicación (Espacio)**: Entidad encargada de parametrizar y definir los lugares físicos o zonas específicas de la universidad que contienen a su vez ESPACIOS.
Lista de ESPACIOS con atributo NOMBRE y COORDENADAS;
~ CIENAGA GRANDE -- 00'00"N
~ SIERRA NEVADA
~ MAR CARIBE
~ HANGAR A
~ HANGAR B
~ BLOQUE 1
,,,
~ BLOQUE 8
~ EDIFICIO DE EMPRENDIMIENTO
~ 

- **Facultad**: Representa las unidades académicas u organizacionales de la institución (por ejemplo, "Ingeniería") a las cuales se encuentran adscritos o asociados los espacios físicos.
Lista de facultates con atributo NOMBRE;
~ INGENIERÍA 
~ SALUD
~ EMPRESARIALES
~ CIENCIAS BÁSICAS
~ HUMANIDADES
~ LICENCIATURA
~

- **Estado físico**: Funciona como control de la condición en la que el recurso es entregado y devuelto.
Lista de estados con atributo NOMBRE y DESCRIPCIÓN;
~ ÓPTIMO -- Funcionamiento correcto, aspecto visual correcto, tal como nuevo o tal como fue entregado.
~ MAL ESTADO -- Daño leve de algún tipo.
~ DAÑADO -- Daño grave que imposibilite el funcionamiento correcto.

- **Estado de Disponibilidad**: Funciona como control de la condición en la que el recurso es entregado y devuelto.
Lista de estados con atributo NOMBRE y DESCRIPCIÓN;
~ EN MANTENIMIENTO -- Ocupado por labores técnicas.
~ EN USO -- Ocupado por reserva o bloqueo acádemico.
~ DISPONIBLE -- Desocupado.

- **Reporte**: Funciona como registro de la reserva y acontecimientos que se dieron en ella si es necesario, cuenta con todos los atributos del recurso además de la fecha de registro del reporte. 


# Entidades Secundarias: 

- **Historial del recurso**: Registro de eventos encargado de almacenar la trazabilidad de los cambios de estado en el ciclo de vida de los recursos. Guarda información clave como el usuario responsable, la fecha/hora exacta del cambio, y los estados anterior y nuevo.

- **Historial de reportes**: Registro encargado de almacenar la trazabilidad de los reportes generados en el sistema. Guarda información clave como ID del recurso, Nombre del recurso, Tipo, Ubicación física, Estado, Aforo si aplica y Fecha en la que se registró el reporte.
