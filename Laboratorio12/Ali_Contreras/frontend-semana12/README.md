# Ali_Contreras — Guía de Laboratorio 12

**Curso:** Desarrollo Web Integrado — Semana 12 (Unidad 3)
**Proyecto:** `frontend-semana12` — Rutas en Angular y comunicación entre componentes

SPA "DevStore" con Angular Router (`RouterLink`, `RouterLinkActive`, `RouterOutlet`), ruta
parametrizada `/productos/:id`, ruta comodín `**`, navegación programática con `Router.navigate()` y
comunicación padre ↔ hijo con `input()` / `output()`. Usa datos locales (`productos.mock.ts`);
todavía no consume el back-end.

## Entorno

```
node -v      → v24.13.1
npm -v       → 11.8.0
ng version   → Angular CLI 21.2.14 · Angular 21.2
```

> La guía sugiere Angular 22, pero el CLI 22 exige Node.js ≥ 24.15 y el equipo del laboratorio
> tiene Node 24.13. Se usó Angular 21, que ya incluye todas las APIs de la práctica
> (`input()`, `input.required()`, `output()`, `@if` / `@for`, `provideRouter`).

## Cómo ejecutar

```bash
npm install
npm start                 # http://localhost:4200/
ng build                  # build de producción
ng test --watch=false     # pruebas (Vitest)
```

## Creación del proyecto

```bash
ng new frontend-semana12 --routing --style=scss --skip-git --ssr=false
ng g c pages/inicio
ng g c pages/productos
ng g c pages/producto-detalle
ng g c pages/no-encontrado
ng g c pages/categorias          # Reto A
ng g c components/producto-card
```

## Estructura

```
src/app/
├── app.ts / app.html / app.scss   # layout: barra de navegación + <router-outlet />
├── app.config.ts                  # provideRouter(routes)
├── app.routes.ts                  # mapa URL → componente
├── app.spec.ts                    # pruebas de rutas y comunicación
├── models/producto.ts             # interfaz Producto
├── data/productos.mock.ts         # PRODUCTOS (datos simulados)
├── components/
│   └── producto-card/             # hijo: input() + output()
└── pages/
    ├── inicio/
    ├── productos/                 # padre: orquesta navegación y estado
    ├── producto-detalle/          # lee :id con ActivatedRoute
    ├── categorias/                # Reto A
    └── no-encontrado/             # ruta **
```

## Rutas

| URL | Componente |
|-----|------------|
| `/` | `Inicio` |
| `/productos` | `Productos` |
| `/productos/:id` | `ProductoDetalle` |
| `/categorias` | `Categorias` |
| `**` | `NoEncontrado` (siempre al final) |

## Comunicación entre componentes

```
Productos (padre)
  │  [producto]="producto"              ──► input.required<Producto>()
  │  [destacado]="producto.precio > 1000" ──► input(false)
  ▼
ProductoCard (hijo)
  │  (verDetalle)          emite id ──► irDetalle(id) → router.navigate(['/productos', id])
  │  (favorito)            emite id ──► registrarFavorito(id) → favoritos + 1
  │  (agregarComparacion)  emite id ──► registrarComparacion(id) → guarda el id sin repetir
```

El hijo solo presenta datos y emite eventos; toda la navegación y el estado viven en el padre.

## Actividad de consolidación

- [x] **Reto A — Categorías:** componente `pages/categorias`, ruta `/categorias`, enlace en el menú
  y lista de categorías únicas (`new Set(...)`) con la cantidad de productos de cada una.
- [x] **Reto B — Tarjeta reutilizable:** `destacado = input(false)`; con `true` se aplica la clase
  `.destacado` (borde dorado y etiqueta "★ Destacado"). Productos marca los de precio > S/ 1,000
  (Laptop Pro y Monitor 27").
- [x] **Reto C — Evento adicional:** `agregarComparacion = output<number>()` emitido por el botón
  "Comparar". El padre guarda los ids en una señal (sin duplicados) y muestra "En comparación: N".
- [x] **Reto D — Consulta directa:** ver respuesta abajo.

### Reto D: `/productos/1` vs `/productos/999`

Ambas URLs coinciden con el mismo patrón `productos/:id`, así que el router carga el mismo componente
`ProductoDetalle`; solo cambia el valor del parámetro. El componente lee `id` con
`ActivatedRoute.snapshot.paramMap`, lo convierte a `number` y lo busca en `PRODUCTOS`. Con `1`
encuentra "Laptop Pro" y entra en la rama `@if`; con `999` `find()` devuelve `undefined` y se
muestra la rama `@else` ("Producto no encontrado"). No llega a la ruta `**` porque la URL sí tiene
una ruta válida: lo que no existe es el recurso, no la ruta.

## Verificación (matriz 11.2)

| Prueba | Resultado |
|--------|-----------|
| `/` muestra Inicio dentro del `router-outlet` | Correcto |
| Pulsar Productos cambia a `/productos` sin recargar | Correcto |
| El enlace activo recibe la clase `active` | Correcto |
| Cada `ProductoCard` muestra el producto recibido del padre | Correcto |
| Favorito dos veces → contador en 2 | Correcto |
| Ver detalle del id 2 → `/productos/2` | Correcto |
| `/productos/3` → Teclado mecánico | Correcto |
| `/productos/999` → Producto no encontrado | Correcto |
| `/xyz` → página 404 | Correcto |
| Atrás / adelante del navegador | Correcto |
| `ng test --watch=false` | **10 pruebas aprobadas** |
| `ng build` | Sin errores ni advertencias |

Las pruebas de `app.spec.ts` usan `RouterTestingHarness` y cubren el menú, cada ruta, el input
`destacado`, los outputs `favorito` y `agregarComparacion`, la navegación programática a
`/productos/2`, las categorías únicas y la ruta comodín.

## Preguntas de reflexión

1. **`routerLink` vs. `href`:** `routerLink` le pide al router de Angular que cambie la vista dentro
   de la misma página (usa la History API), sin volver a descargar `index.html` ni reiniciar la
   aplicación, por lo que se conserva el estado. Un `href` provoca una navegación completa del
   navegador: recarga todo y se pierde el estado en memoria.
2. **Responsabilidad de `RouterOutlet`:** es el punto del layout donde Angular renderiza el
   componente de la ruta activa. El header y el menú quedan fijos y solo cambia el contenido del
   outlet.
3. **¿Por qué `**` al final?** El router recorre las rutas en orden y usa la primera que coincide.
   `**` coincide con cualquier URL, así que si estuviera antes "taparía" a las demás y todo
   terminaría en la página 404.
4. **Ventaja de `input.required()`:** convierte el dato en un contrato obligatorio que Angular
   verifica en compilación: si el padre olvida pasar `[producto]`, el build falla. Además el tipo es
   `Producto` y no `Producto | undefined`, por lo que no hay que comprobar `undefined` en el hijo.
5. **¿Por qué `output()` reduce el acoplamiento?** El hijo solo anuncia "ocurrió esto, con este id";
   no conoce al padre ni decide qué hacer. Cada padre puede reaccionar como quiera (navegar, contar,
   abrir un modal), así que la misma tarjeta se reutiliza en contextos distintos.
6. **¿Parámetro de ruta o input?** El parámetro de ruta conviene cuando el dato identifica qué vista
   o recurso mostrar y debe vivir en la URL: se puede compartir, guardar como favorito, recargar y
   usar con atrás/adelante (`/productos/3`). El `input()` es para pasar datos entre un padre y un
   hijo que están en la misma vista.
7. **¿Qué se reemplazará en la semana 14?** El archivo `productos.mock.ts` (la constante
   `PRODUCTOS`). En su lugar, un servicio con `HttpClient` pedirá la lista y el detalle al API Spring
   Boot (`GET /api/productos` y `GET /api/productos/{id}`). Las rutas, `ProductoCard` y el flujo
   `input()` / `output()` se mantienen.
