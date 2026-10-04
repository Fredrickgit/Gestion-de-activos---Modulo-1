# GITFLOW — GUÍA RÁPIDA (con GitHub Rulesets)

## ¿QUÉ ES?

Gitflow es una **forma de organizar las ramas** de Git según el tipo de trabajo.
No es un comando de Git, es una convención que la herramienta `git flow` automatiza.

Tiene 2 ramas permanentes y 3 tipos de ramas temporales.

> ⚠️ **Nota importante sobre este repositorio:**
> `main` y `develop` están protegidas con **Rulesets** en GitHub.
> Esto significa que **NO se puede hacer `push` directo** ni `merge` local sobre ellas.
> Todo cambio debe llegar por **Pull Request (PR)**.
> Por eso **NO usamos `git flow ... finish`** en este repositorio: ese comando fusiona en local
> y luego el push sería rechazado. En su lugar usamos PRs en GitHub.

---

## LAS RAMAS

**Permanentes (siempre existen, protegidas):**

- `main`     → código en producción (lo que ve el usuario).
- `develop`  → integración, donde se junta todo lo terminado.

**Temporales (se crean, se usan y se borran):**

- `feature/*` → una funcionalidad nueva. Sale de `develop`, vuelve a `develop` vía PR.
- `release/*` → preparar una versión. Sale de `develop`, va a `main` y `develop` vía PRs.
- `hotfix/*`  → arreglar un bug urgente en producción. Sale de `main`, va a `main` y `develop` vía PRs.

---

## REGLAS BÁSICAS

1. `main` solo recibe código desde `release/*` o `hotfix/*`, **siempre mediante PR**.
2. `develop` recibe código desde `feature/*`, `release/*` y `hotfix/*`, **siempre mediante PR**.
3. Toda funcionalidad nueva empieza con `git flow feature start`.
4. Toda versión que se publica se etiqueta (tag).
5. Las ramas temporales se borran al terminar (GitHub puede borrarlas solas al fusionar el PR).
6. **Nunca hagas `git push` directo a `main` ni a `develop`.** El Ruleset lo rechazará.
7. **Nunca uses `git flow ... finish`** en este repositorio. El merge debe ocurrir en GitHub.

---

## FLUJO 1 — DESARROLLO DIARIO (FEATURE)

Esto es el 90% del tiempo. Cada funcionalidad nueva o mejora.

**Paso 1 — Crear la rama desde develop:**
```bash
git checkout develop
git pull origin develop
git flow feature start login-usuarios
```

**Paso 2 — Escribir código y guardar cambios:**
```bash
git add .
git commit -m "agrego formulario de login"
```

**Paso 3 — Publicar la rama en el remoto (obligatorio para poder abrir un PR):**
```bash
git flow feature publish login-usuarios
# Equivalente a: git push -u origin feature/login-usuarios
```

**Paso 4 — Abrir un Pull Request en GitHub:**
- Ve a GitHub → tu repositorio → aparece un aviso "Compare & pull request".
- **Base:** `develop`  **Compare:** `feature/login-usuarios`
- Espera la revisión humana (y CI si lo hubiera).
- Fusiona el PR desde GitHub (botón "Merge pull request").

**Paso 5 — Sincronizar tu `develop` local y limpiar:**
```bash
git checkout develop
git pull origin develop
git branch -d feature/login-usuarios        # borra la rama local
git push origin --delete feature/login-usuarios  # borra la remota (si GitHub no lo hizo ya)
```

> ❌ **Lo que NO debes hacer:**
```bash
> git flow feature finish login-usuarios   # ← NO en este repo
> git push origin develop                  # ← el Ruleset lo rechazará
```

---

## FLUJO 2 — PREPARAR UNA VERSIÓN (RELEASE)

Cuando `develop` ya tiene todo lo que quieres publicar.

**Paso 1 — Crear la rama de release:**
```bash
git checkout develop
git pull origin develop
git flow release start 1.0.0
```

**Paso 2 — Ajustes finales:**
- Subir número de versión.
- Actualizar changelog.
- Corregir bugs menores.

```bash
git add .
git commit -m "preparo release 1.0.0"
git push -u origin release/1.0.0
```

**Paso 3 — Abrir PR hacia `main`:**
- **Base:** `main`  **Compare:** `release/1.0.0`
- Revisar y fusionar en GitHub.

**Paso 4 — Abrir PR hacia `develop`:**
- **Base:** `develop`  **Compare:** `release/1.0.0` (o el commit ya fusionado en `main`).
- Revisar y fusionar en GitHub.

**Paso 5 — Crear el tag en `main`:**
```bash
git checkout main
git pull origin main
git tag -a v1.0.0 -m "Release 1.0.0"
git push origin v1.0.0
```
> El tag lo creas **después** del merge, apuntando al commit de `main` que ya tiene la release.

**Paso 6 — Limpiar:**
```bash
git branch -d release/1.0.0
git push origin --delete release/1.0.0
```

---

## FLUJO 3 — ARREGLAR UN BUG EN PRODUCCIÓN (HOTFIX)

Solo cuando hay algo roto en `main` y hay que arreglarlo YA.

**Paso 1 — Crear el hotfix desde main:**
```bash
git checkout main
git pull origin main
git flow hotfix start 1.0.1
```

**Paso 2 — Corregir y guardar:**
```bash
git add .
git commit -m "corrijo error en el login"
git push -u origin hotfix/1.0.1
```

**Paso 3 — Abrir PR hacia `main`:**
- **Base:** `main`  **Compare:** `hotfix/1.0.1`
- Revisar y fusionar en GitHub.

**Paso 4 — Abrir PR hacia `develop`:**
- **Base:** `develop`  **Compare:** `hotfix/1.0.1` (o el commit ya fusionado en `main`).
- Revisar y fusionar en GitHub.
- ⚠️ **Este paso es obligatorio**: si no sincronizas el fix a `develop`, se perderá en el próximo release.

**Paso 5 — Crear el tag en `main`:**
```bash
git checkout main
git pull origin main
git tag -a v1.0.1 -m "Hotfix 1.0.1"
git push origin v1.0.1
```

**Paso 6 — Limpiar:**
```bash
git branch -d hotfix/1.0.1
git push origin --delete hotfix/1.0.1
```

---

## CHULETA DE COMANDOS

| Etapa                | Comando                                          |
|----------------------|--------------------------------------------------|
| Feature (crear)      | `git flow feature start nombre`                  |
| Feature (publicar)   | `git flow feature publish nombre`                |
| Feature (cerrar)     | Abrir PR en GitHub → fusionar → `git pull`       |
| Release (crear)      | `git flow release start 1.0.0`                   |
| Release (cerrar)     | PR a `main` + PR a `develop` + tag + push tag    |
| Hotfix (crear)       | `git flow hotfix start 1.0.1`                    |
| Hotfix (cerrar)      | PR a `main` + PR a `develop` + tag + push tag    |
| Sincronizar develop  | `git checkout develop && git pull origin develop`|
| Sincronizar main     | `git checkout main && git pull origin main`      |
| Publicar rama        | `git push -u origin <rama>`                      |
| Borrar rama remota   | `git push origin --delete <rama>`                |
| Ver ramas            | `git branch -a`                                  |
| Ver tags             | `git tag`                                        |
| Estado               | `git status`                                     |
| Ver diferencia       | `git branch -vv`                                 |

---

## PREGUNTAS FRECUENTES

**¿Por qué no puedo usar `git flow feature finish`?**
Porque ese comando fusiona en tu `develop` **local** y luego intentarías `git push origin develop`. El Ruleset de GitHub rechazará ese push. En este repo, el merge ocurre en GitHub mediante PR.

**¿Qué subo al remoto?**
Las ramas temporales (`feature/*`, `release/*`, `hotfix/*`) se suben para poder abrir PRs. `main` y `develop` **nunca** se suben con push directo; se actualizan al fusionar PRs.

**¿Qué hago si `git flow ... finish` ya se ejecutó por error?**
Si solo afectó a tu repo local (no llegaste a hacer push), puedes deshacerlo con:
```bash
git checkout develop
git reset --hard origin/develop
```
Si ya hiciste push y fue rechazado, simplemente ignora el merge local y limpia la rama.

**¿Puedo tener varias features a la vez?**
Sí. Cada una es una rama aparte. Publica cada una y abre su PR cuando esté lista.

**¿Cuándo hago un hotfix y cuándo una feature?**
Si el bug está en producción (`main`) y es urgente → hotfix (sale de `main`, va a `main` y `develop`).
Si es algo nuevo o un bug que aún no llegó a producción → feature (sale de `develop`, va a `develop`).

**¿Dónde estoy parado después de cada cierre?**
- Cierre de feature → `develop` (tras `git checkout develop && git pull`).
- Cierre de release → `develop` (tras sincronizar) y el tag queda en `main`.
- Cierre de hotfix → `develop` (tras sincronizar) y el tag queda en `main`.

**¿Cómo verifico que todo llegó al remoto?**
```bash
git fetch --all --prune
git log --graph --oneline --all --decorate -30
```
Deberías ver los commits de merge (`Merge pull request #N`) conectando las ramas.

**¿Qué pasa si intento hacer `git push origin main`?**
GitHub te responderá con un error tipo:
```
remote: error: GH006: Protected branch update failed for refs/heads/main.
remote: error: Changes must be made through a pull request.
```
Es la señal de que el Ruleset está funcionando correctamente.

---

## RESUMEN

Trabaja siempre en una rama temporal (`feature`), publícala y fusiónala a `develop`
mediante un **Pull Request** en GitHub. Cuando tengas una versión lista, crea una
`release` y ábrela como PR hacia `main` y hacia `develop`. Si algo se rompe en
producción, crea un `hotfix` desde `main` y ábrelo como PR hacia `main` y hacia
`develop`. **Nunca hagas push directo a `main` ni a `develop`, y nunca uses
`git flow ... finish` en este repositorio.**

---

## DIFERENCIA CLAVE CON GITFLOW CLÁSICO

| Aspecto                    | Gitflow clásico (sin Rulesets) | Este repositorio (con Rulesets)       |
|----------------------------|--------------------------------|----------------------------------------|
| Cierre de feature          | `git flow feature finish`       | PR en GitHub → merge → pull local      |
| Cierre de release          | `git flow release finish`       | PR a `main` + PR a `develop` + tag     |
| Cierre de hotfix           | `git flow hotfix finish`        | PR a `main` + PR a `develop` + tag     |
| Push a `main` / `develop`  | Permitido                       | **Bloqueado por Ruleset**              |
| Dónde ocurre el merge      | En local                        | En GitHub (servidor)                   |
| Borrado de ramas           | Lo hace `git flow`              | Manual o automático al fusionar el PR  |
| Tag de versión             | Lo crea `git flow`              | Manual (`git tag -a` + `git push`)     |
