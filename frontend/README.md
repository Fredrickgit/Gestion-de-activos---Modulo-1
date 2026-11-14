# Gestión de activos universitarios — Módulo 1

Frontend del módulo de inventario, registro y reportes de recursos universitarios, construido a partir de los mockups de Figma (`PantallaFIGMAmockup.fig`, `FIGMAscreen.pdf`).

## Stack

- Vite + React 19 + TypeScript
- Tailwind CSS v4 (`@tailwindcss/vite`)
- React Router
- lucide-react
- Tipografías: Plus Jakarta Sans (títulos), Inter (interfaz), Cousine (IDs)

## Scripts

```bash
npm install
npm run dev        # servidor de desarrollo
npm run typecheck  # verificación de tipos
npm run build      # build de producción
npm run preview    # previsualizar el build
```

## Rutas

| Ruta | Pantalla |
| --- | --- |
| `/inventario` | Inventario — todos los recursos |
| `/inventario/activos` | Inventario — activos |
| `/inventario/espacios` | Inventario — espacios |
| `/registrar` | Registrar nuevo recurso |
| `/reportes` | Generar reporte |

La raíz redirige a `/inventario`.

## Notas

- El detalle del recurso se abre por defecto en escritorio; en pantallas grandes (≥1700 px) ocupa un riel de 460 px dentro del layout y por debajo de ese ancho se muestra como panel superpuesto.
- Los datos son de demostración y viven en memoria (`src/data`): al recargar la página el inventario vuelve a su estado inicial.
- Los recursos creados, editados o con cambio de estado se reflejan de inmediato en el inventario y en el reporte.
- Los assets de marca están en `public/assets`.
