# HU #1: [Creacion de recurso]:

**Como** Dirección Universitaria **deseo** agregar un nuevo recurso al sistema, ya sea un activo o espacio, dependiendo del tipo de recurso que quiera agregar los atributos respectivos del recurso (nombre; ID, aforo máximo, equipamiento fijo y facultad; o ID 'Placa de inventario', tipo, estado físico y ubicación).

**Criterios de aceptación:**

- El sistema DEBE permitir registrar nuevos activos físicos en el sistema, capturando de forma obligatoria los atributos: nombre, ID único (Placa de inventario), tipo de activo, estado físico inicial y ubicación (espacio al que pertenece).

-  El sistema DEBE permitir registrar nuevos espacios académicos en el sistema, capturando de forma obligatoria los atributos: nombre, ID único del espacio, aforo máximo (capacidad de personas), equipamiento fijo y facultad a la que pertenece.

-  El sistema DEBE validar la unicidad del identificador (Placa de inventario para activos e ID para espacios) antes de persistir cualquier registro en la base de datos, impidiendo duplicidades y mostrando un mensaje de error claro.

- El sistema DEBE validar que todos los atributos obligatorios requeridos para cada tipo de recurso estén completos y no contengan valores vacíos o nulos antes de permitir su creación.

- El sistema DEBE validar que el valor de aforo máximo para los espacios sea un número entero positivo mayor a cero (aforo > 0) y acorde a las capacidades institucionales permitidas.

- El sistema DEBE asignar automáticamente un estado de disponibilidad inicial ("Disponible") a todo recurso recién creado, permitiendo su visualización inmediata en inventario y disponibilidad.

- El sistema DEBE validar la integridad referencial de los atributos parametrizados, verificando que el tipo de activo, el espacio de ubicación y la facultad existan previamente en las entidades del sistema.

- El sistema DEBE verificar y validar los permisos de acceso del usuario antes de desplegar el formulario o procesar la solicitud de creación, restringiendo la operación únicamente al rol de Dirección Universitaria y devolviendo un mensaje de acceso denegado si no cuenta con autorización.

