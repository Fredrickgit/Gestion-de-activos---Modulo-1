# GITFLOW — GUÍA RÁPIDA

## ¿QUÉ ES?

Gitflow es una **forma de organizar las ramas** de Git según el tipo de trabajo.
No es un comando de Git, es una convención que la herramienta `git flow` automatiza.

Tiene 2 ramas permanentes y 3 tipos de ramas temporales.

---

## LAS RAMAS

**Permanentes (siempre existen):**

- `main`     → código en producción (lo que ve el usuario).
- `develop`  → integración, donde se junta todo lo terminado.

**Temporales (se crean, se usan y se borran):**

- `feature/*` → una funcionalidad nueva. Sale de `develop`, vuelve a `develop`.
- `release/*` → preparar una versión. Sale de `develop`, va a `main` y `develop`.
- `hotfix/*`  → arreglar un bug urgente en producción. Sale de `main`, va a `main` y `develop`.

---

## REGLAS BÁSICAS

1. `main` solo recibe código desde `release/*` o `hotfix/*`.
2. `develop` recibe código desde `feature/*`, `release/*` y `hotfix/*`.
3. Toda funcionalidad nueva empieza con `git flow feature start`.
4. Toda versión que se publica se etiqueta (tag).
5. Las ramas temporales se borran al terminar (git flow lo hace solo).

---

## FLUJO 1 — DESARROLLO DIARIO (FEATURE)

Esto es el 90% del tiempo. Cada funcionalidad nueva o mejora.

**Paso 1 — Crear la rama desde develop:**
```bash
git flow feature start login-usuarios
```

**Paso 2 — Escribir código y guardar cambios:**
```bash
git add .
git commit -m "agrego formulario de login"
```

**Paso 3 — (Opcional) Subirla al remoto** si trabajas en equipo o quieres respaldo:
```bash
git flow feature publish login-usuarios
```

**Paso 4 — Terminar la funcionalidad:**
```bash
git flow feature finish login-usuarios
```
Esto hace automáticamente:
- Fusiona la feature en `develop`.
- Borra la rama local.
- Te deja parado en `develop`.

**Paso 5 — Subir develop al remoto:**
```bash
git push origin develop
```

Si la habías publicado, borra la rama remota:
```bash
git push origin --delete feature/login-usuarios
```

---

## FLUJO 2 — PREPARAR UNA VERSIÓN (RELEASE)

Cuando `develop` ya tiene todo lo que quieres publicar.

**Paso 1 — Crear la rama de release:**
```bash
git flow release start 1.0.0
```

**Paso 2 — Ajustes finales:**
- Subir número de versión.
- Actualizar changelog.
- Corregir bugs menores.

```bash
git add .
git commit -m "preparo release 1.0.0"
```

**Paso 3 — Terminar la release:**
```bash
git flow release finish 1.0.0
```
Esto hace automáticamente:
- Fusiona en `main`.
- Crea el tag `v1.0.0`.
- Fusiona en `develop`.
- Borra la rama local.

Te pedirá un mensaje para el tag. Escribe algo como `Release 1.0.0`.

**Paso 4 — Subir todo al remoto:**
```bash
git push origin main
git push origin develop
git push origin --tags
```

---

## FLUJO 3 — ARREGLAR UN BUG EN PRODUCCIÓN (HOTFIX)

Solo cuando hay algo roto en `main` y hay que arreglarlo YA.

**Paso 1 — Crear el hotfix desde main:**
```bash
git flow hotfix start 1.0.1
```

**Paso 2 — Corregir y guardar:**
```bash
git add .
git commit -m "corrijo error en el login"
```

**Paso 3 — Terminar el hotfix:**
```bash
git flow hotfix finish 1.0.1
```
Esto hace automáticamente:
- Fusiona en `main`.
- Crea el tag `v1.0.1`.
- Fusiona en `develop`.
- Borra la rama local.

**Paso 4 — Subir todo al remoto:**
```bash
git push origin main
git push origin develop
git push origin --tags
```

---

## CHULETA DE COMANDOS

| Etapa       | Comando                                  |
|-------------|------------------------------------------|
| Feature     | `git flow feature start nombre`          |
| Feature     | `git flow feature publish nombre`        |
| Feature     | `git flow feature finish nombre`         |
| Release     | `git flow release start 1.0.0`           |
| Release     | `git flow release finish 1.0.0`          |
| Hotfix      | `git flow hotfix start 1.0.1`            |
| Hotfix      | `git flow hotfix finish 1.0.1`           |
| Subir       | `git push origin main`                   |
| Subir       | `git push origin develop`                |
| Subir tags  | `git push origin --tags`                 |
| Ver ramas   | `git branch -a`                          |
| Ver tags    | `git tag`                                |
| Estado      | `git status`                             |

---

## PREGUNTAS FRECUENTES

**¿Qué subo al remoto?**
Siempre `main` y `develop`. Las temporales solo si trabajas en equipo.

**¿Tengo que hacer push de la feature antes de terminar?**
No es obligatorio. Solo si otra persona la necesita o quieres respaldo.

**¿Qué pasa si me equivoco en un `finish`?**
Puedes revertir con `git reset` o `git revert`, pero es más fácil revisar antes de terminar.

**¿Puedo tener varias features a la vez?**
Sí. Cada una es una rama aparte.

**¿Cuándo hago un hotfix y cuándo una feature?**
Si el bug está en producción (`main`) y es urgente → hotfix.
Si es algo nuevo o un bug que aún no llegó a producción → feature.

**¿Dónde estoy parado después de cada finish?**
- `feature finish` → te deja en `develop`.
- `release finish` → te deja en `develop`.
- `hotfix finish` → te deja en `develop`.

---

## RESUMEN

Trabaja siempre en una rama temporal (`feature`), fusiónala a `develop`,
cuando tengas una versión lista crea una `release` que va a `main`,
y si algo se rompe en producción crea un `hotfix` desde `main`.
