# Integración REST con el módulo 3

El backend consulta `GET /api/v1/novedades/pendientes?recursoId={uuid}`
sin autenticación antes de permitir una transición a `DISPONIBLE`.
El UUID debe identificar el mismo recurso en ambos módulos.

Respuesta esperada: un objeto con `recursoId` y el booleano
`tieneNovedadPendiente`.

## Configuración del backend compartido

Después de desplegar el código, sincronizar el Blueprint o configurar estas
variables en el servicio backend de Render y redesplegar:

```env
EXTERNAL_VALIDATION_ENABLED=false
MODULO2_VALIDATION_ENABLED=false
MODULO3_VALIDATION_ENABLED=true
MODULO3_BASE_URL=https://gestionu-mod-3.onrender.com
```

La URL no lleva `/api/v1`. Los interruptores por módulo tienen prioridad sobre
`EXTERNAL_VALIDATION_ENABLED`; si no se especifican, heredan su valor.
Estas variables también pueden usarse al ejecutar el backend localmente,
junto con la configuración de base de datos del entorno.

Con M2 desactivado se omite la comprobación REST de reservas. La configuración
de los listeners Kafka es independiente.

## Comprobación

1. Consultar el endpoint de M3 con un UUID conocido y comprobar su respuesta.
2. Intentar pasar un recurso a `DISPONIBLE` usando la API del módulo 1.
3. Sin novedades debe permitirlo, sujeto a las demás reglas del recurso.
4. Con novedades debe rechazarlo.
5. Si M3 no está disponible o excede el tiempo de espera, debe rechazarlo
   indicando que no pudo validar; no asumir que no existen novedades.

Los tiempos configurables son `MODULO3_CONNECT_TIMEOUT` (2000 ms por defecto)
y `MODULO3_READ_TIMEOUT` (3000 ms). Un servicio dormido en Render puede excederlos;
comprobar su disponibilidad antes de la prueba.

Esta configuración reemplaza la indicación de la guía inicial de despliegue
que desactivaba conjuntamente las validaciones REST de M2 y M3.
