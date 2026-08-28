```markdown
# UNIMAG: Gestión Integral de Activos y Espacios Universitarios

Este proyecto define el diseño para el sistema de **Gestión de Recursos de la Universidad del Magdalena (UNIMAG)**. El objetivo es centralizar el control de los recursos físicos y los espacios académicos para que la comunidad estudiantil acceda a ellos de forma organizada, garantizando la continuidad de las clases regulares.

---

### 🏫 Módulo 1: Gestión de Activos y Espacios (Inventario Físico)
Este módulo actúa como la base de datos central que digitaliza la infraestructura física y los recursos disponibles para préstamo o apartado.

#### 1.1. Tipificación y Atributos (Entidad)
Cada recurso se categoriza para definir sus reglas de uso:
*   **Recursos Físicos (Muebles/Equipos):** ID Único (Placa de inventario), tipo (Libro, Microscopio, Kit de dibujo), estado físico y ubicación (Laboratorio X, Biblioteca).
*   **Espacios (Aforo):** ID de Salón/Auditorio, capacidad de carga (aforo máximo), equipamiento fijo (Proyector, Aire acondicionado, Sillas) y facultad a la que pertenece.

#### 1.2. Ciclo de Vida y Estados del Recurso
Para evitar cruces de horarios o entregas fallidas, se gestionan los siguientes estados:
1.  **Disponible:** Listo para ser solicitado.
2.  **Reservado:** Bloqueado temporalmente por un estudiante.
3.  **Bloqueo Académico (Prioridad):** Reservado para clases regulares del semestre.
4.  **En Uso:** Recurso entregado o salón ocupado.
5.  **En Mantenimiento:** Recurso en reparación o salón bajo arreglos técnicos.

---

### 🗓️ Módulo 2: Operación de Reservas y Priorización Académica
Es el motor de reglas de negocio encargado de gestionar el uso y resolver conflictos de interés.

#### 2.1. Módulo de Carga Académica (Prioridad Institucional)
Permite a la administración asegurar la continuidad de las actividades principales de la universidad:
*   **Importar Horarios Semestrales:** Carga de clases fijas que marcan automáticamente los espacios como "Bloqueo Académico".
*   **Jerarquía de Reserva:** Las reservas académicas tienen prioridad total sobre las estudiantiles, permitiendo cancelaciones automáticas de menor rango si surge una necesidad institucional extraordinaria.

#### 2.2. Interfaz de Apartado Estudiantil
*   **Validación de Disponibilidad:** Los estudiantes solo pueden visualizar recursos que no estén en "Bloqueo Académico" o "En Uso".
*   **Diccionario de Errores (Denegación):** Se impide la reserva por conflictos académicos, haber alcanzado el límite máximo de préstamos o tener una sanción activa.

---

### 📊 Módulo 3: Control de Uso, Sanciones y Analítica
Este módulo traduce la operación en reportes de cumplimiento y control administrativo.

#### 3.1. Gestión de Devoluciones y Novedades
Al finalizar el uso, se registra el estado del recurso como un "Check-out Exitoso" o se reporta una "Novedad Técnica" en caso de daños.

#### 3.2. Matriz de Cumplimiento y Sanciones
Aplica penalizaciones automáticas para garantizar la rotación de los recursos:

| Acción del Usuario | Estado del Recurso | Consecuencia en el Sistema |
| :--- | :--- | :--- |
| **Entrega a Tiempo** | Óptimo | Incremento de "Score" de confianza. |
| **Mora (Retraso)** | En Uso (Vencido) | Suspensión temporal de reservas (X días por hora de retraso). |
| **No Asistencia (Salón)**| Reservado (Vencido)| Bloqueo de reserva de espacios por 1 semana. |
| **Daño de Recurso** | Mantenimiento | Generación de cobro por reparación/reposición. |

---

**Resultado esperado:** El software desarrollado debe permitir cargar el calendario semestral de clases, facilitar la búsqueda de equipos disponibles y automatizar las sanciones por incumplimiento en las entregas.
```
