# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## FACULTAD DE INGENIERÍA DE SISTEMAS E INFORMÁTICA
### DESARROLLO WEB INTEGRADO — UNIDAD 3: SEMANA 11

---

# GUÍA DE LABORATORIO 11
## Fundamentos de Angular, Componentes y SASS/SCSS
**Estudiante:** Antonela Jaimes  
**Tema:** Node.js + npm + Angular CLI + Componentes Standalone + Control de Flujo (@if, @for) + Estilos SCSS  
**Tecnologías:** Angular 21/22, TypeScript 5.9, SASS/SCSS, Node.js 24 LTS, npm 11  

---

## 1. Propósito y Logro de Aprendizaje

Esta práctica da inicio a la **Unidad 3 del curso**. Habiendo construido y asegurado previamente el backend con **Spring Boot, Spring Security y PostgreSQL con JWT** (Semanas 1 a 10), iniciamos la capa de presentación que posteriormente consumirá dichos servicios REST.

> **Logro de la sesión:**  
> Configurar el entorno Angular moderno, desarrollar una aplicación web modular basada en componentes standalone, aplicar la sintaxis moderna de plantillas (`@if`, `@for`, signals) y organizar estilos profesionales con preprocesadores **SASS/SCSS** (variables, anidamiento y mixins).

---

## 2. Estructura del Proyecto

El proyecto sigue estrictamente la convención moderna recomendada por Angular (guía de estilo 2025/2026), dividiendo responsabilidades en componentes modulares y reutilizables:

```text
Laboratorio11/Antonela_Jaimes/
├── .gitignore
├── angular.json                      # Configuración de build, serve y schematics SCSS
├── package.json                      # Dependencias Angular y scripts npm
├── tsconfig.json                     # Configuración base de TypeScript
├── tsconfig.app.json                 # Configuración de compilación para la aplicación
├── README.md                         # Documentación técnica y respuestas de reflexión
└── src/
    ├── index.html                    # Contenedor HTML raíz (<app-root>)
    ├── main.ts                       # Punto de arranque standalone con bootstrapApplication
    ├── styles.scss                   # Estilos globales y reset
    ├── styles/
    │   └── _variables.scss           # Tokens de diseño: paleta de colores, mixins y sombras
    └── app/
        ├── app.config.ts             # Proveedores globales de Angular
        ├── app.ts                    # Componente raíz: estado reactivo, signals, productos
        ├── app.html                  # Plantilla raíz: composición de componentes y directivas
        ├── app.scss                  # Estilos encapsulados con anidamiento y mixins
        ├── shared/
        │   ├── app-header/           # Componente 1: Barra superior de navegación y branding
        │   │   ├── app-header.ts
        │   │   ├── app-header.html
        │   │   └── app-header.scss
        │   └── app-footer/           # Componente 2: Pie de página con metadatos del curso
        │       ├── app-footer.ts
        │       ├── app-footer.html
        │       └── app-footer.scss
        └── features/
            └── summary-panel/        # Componente 3: Métricas de catálogo (totales y stock)
                ├── summary-panel.ts
                ├── summary-panel.html
                └── summary-panel.scss
```

---

## 3. Componentes Implementados

### 3.1 Componente Raíz (`App`)
* **Archivo:** `src/app/app.ts`, `app.html`, `app.scss`.
* **Responsabilidad:** Gestiona la lista de productos tecnológicos, el estado reactivo con `signal(true)` para alternar la visualización del stock, la búsqueda interactiva mediante `[(ngModel)]`, y el cálculo de métricas (disponibles vs. agotados).

### 3.2 Componente Cabecera (`AppHeader`)
* **Selector:** `<app-header></app-header>`
* **Archivo:** `src/app/shared/app-header/`
* **Responsabilidad:** Presenta la identidad visual del catálogo, el curso "Desarrollo Web Integrado · Semana 11" y el autor "Antonela Jaimes".

### 3.3 Componente Métricas (`SummaryPanel`)
* **Selector:** `<app-summary-panel></app-summary-panel>`
* **Archivo:** `src/app/features/summary-panel/`
* **Responsabilidad:** Muestra tres tarjetas visuales con indicadores clave del catálogo:
  1. **Total de productos registrados.**
  2. **Total de productos disponibles con stock.**
  3. **Total de productos agotados / sin stock.**

### 3.4 Componente Pie de Página (`AppFooter`)
* **Selector:** `<app-footer></app-footer>`
* **Archivo:** `src/app/shared/app-footer/`
* **Responsabilidad:** Renderiza el pie de página institucional con el año y la autoría del estudiante.

---

## 4. Conceptos Técnicos Demostrados

| Concepto Angular | Ejemplo en Código | Función |
| :--- | :--- | :--- |
| **Componentes Standalone** | `standalone: true, imports: [AppHeader, ...]` | No requiere `NgModule`. Los componentes gestionan directamente sus dependencias. |
| **Interpolación** | `{{ titulo }}`, `{{ producto.nombre }}` | Muestra valores de TypeScript dinámicamente en el HTML. |
| **Property Binding** | `[class.out-of-stock]="producto.stock === 0"` | Aplica clases CSS condicionalmente según el valor de una expresión booleana. |
| **Event Binding** | `(click)="alternarStock()"` | Captura eventos del usuario y ejecuta métodos del componente. |
| **Two-way Data Binding** | `[(ngModel)]="busqueda"` | Vinculación bidireccional entre el input de búsqueda y la variable de TypeScript. |
| **Signals Reactivas** | `mostrarStock = signal(true); this.mostrarStock.update(...)` | Estado reactivo primitivo de Angular de alto rendimiento y detección fina de cambios. |
| **Control de Flujo `@for`** | `@for (p of productosFiltrados; track p.id)` | Renderizado declarativo y eficiente de listas con bloque `@empty` de respaldo. |
| **Control de Flujo `@if`** | `@if (mostrarStock())`, `@if (p.stock > 0)` | Renderizado condicional en el DOM sin necesidad de directivas estructurales antiguas. |

---

## 5. Arquitectura SASS/SCSS Aplicada

1. **Tokens Centralizados (`_variables.scss`):**
   * Variables de color: `$navy: #0d2042;`, `$blue: #14569b;`, `$teal: #008491;`, `$danger: #b23a3a;`, `$success: #198754;`.
2. **Mixins Reutilizables:**
   * `@mixin card-shadow`: Aplica elevación visual suave (`box-shadow: 0 10px 26px rgba(...)`).
   * `@mixin card-shadow-hover`: Eleva visualmente las tarjetas en `:hover`.
   * `@mixin smooth-transition`: Suaviza animaciones y microinteracciones.
3. **Anidamiento y Selector Padre (`&`):**
   * Anidamiento limpio de elementos: `.product-card { &__title { ... } &.out-of-stock { ... } }`.
4. **Diseño Responsive:**
   * Cuadrícula fluida con CSS Grid: `grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));`.

---

## 6. Respuestas a Preguntas de Cierre y Reflexión (Sección 14 de la Guía)

### 1. ¿Por qué Angular necesita Node.js si la aplicación finalmente se ejecuta en el navegador?
> **Respuesta:**  
> Aunque el navegador web solo interpreta JavaScript, HTML y CSS estándar, el código fuente de Angular se escribe en **TypeScript**, utiliza archivos **SCSS** y emplea módulos ES modernos.  
> **Node.js** proporciona el entorno de ejecución del lado del servidor/herramientas necesario para que corran el compilador de Angular (`@angular/compiler-cli`), el bundler/transpilador (Vite / esbuild), el gestor de paquetes (`npm`) y el servidor de desarrollo en caliente (`ng serve`). Tras el proceso de construcción (`ng build`), el resultado final son archivos estáticos que ya no dependen de Node.js en el cliente.

### 2. ¿Qué diferencia existe entre Angular y Angular CLI?
> **Respuesta:**  
> * **Angular:** Es el **framework** en sí mismo; el conjunto de bibliotecas, APIs y herramientas arquitectónicas (`@angular/core`, `@angular/common`, `@angular/forms`) con las que programamos componentes, directivas, pipes y servicios.
> * **Angular CLI:** Es la **interfaz de línea de comandos** (`ng`) que automatiza el ciclo de vida del proyecto: inicialización (`ng new`), generación de artefactos (`ng generate component`), ejecución local (`ng serve`), pruebas (`ng test`) y compilación para producción (`ng build`).

### 3. ¿Qué ventaja ofrece dividir una interfaz en componentes?
> **Respuesta:**  
> La componentización aporta **modularidad**, **reutilización de código**, **encapsulamiento de estilos y lógica** y **mantenibilidad**. Cada componente tiene una única responsabilidad bien definida (principio SRP). Si se debe modificar la cabecera (`app-header`) o el resumen de métricas (`summary-panel`), los cambios quedan aislados sin riesgo de romper el resto de la interfaz.

### 4. ¿Qué significa que un componente sea standalone?
> **Respuesta:**  
> Significa que el componente es **autónomo** y no necesita pertenecer a un `@NgModule` para ser declarado o utilizado. A partir de Angular 15+ y consolidado en Angular 19-22, los componentes standalone declaran sus propias dependencias en su metadato `imports: [...]`, simplificando la arquitectura, mejorando el *tree-shaking* y reduciendo drásticamente el código boilerplate.

### 5. ¿Qué diferencia existe entre interpolación, property binding y event binding?
> **Respuesta:**  
> * **Interpolación (`{{ exp }}`):** Flujo unidireccional de datos desde el componente hacia la plantilla en formato de texto plano/string.
> * **Property Binding (`[propiedad]="valor"`):** Flujo unidireccional desde el componente hacia una propiedad del elemento DOM o `@Input()` de un componente hijo (e.g. `[class.out-of-stock]="producto.stock === 0"`).
> * **Event Binding (`(evento)="metodo()"`):** Flujo inverso desde la plantilla hacia el componente; escucha eventos de usuario (clicks, teclas) y ejecuta lógica en TypeScript (e.g. `(click)="alternarStock()"`).

### 6. ¿Cuándo usarías `@if` y cuándo `@for`?
> **Respuesta:**  
> * **`@if`:** Se utiliza para **renderizado condicional**; añade o remueve nodos del DOM dependiendo de si una expresión booleana se evalúa como verdadera o falsa (e.g., verificar si hay stock antes de mostrar la etiqueta de unidades).
> * **`@for`:** Se utiliza para **iteración de colecciones** (arrays, listas); genera una plantilla repetida por cada elemento de una lista, exigiendo un identificador único mediante `track` (e.g., `track producto.id`) para optimizar al máximo las actualizaciones del DOM, y permitiendo un bloque `@empty` cuando no hay datos.

### 7. ¿Qué ventajas aporta SCSS frente a una hoja CSS extensa?
> **Respuesta:**  
> SCSS introduce capacidades de lenguajes de programación que CSS plano no posee:
> 1. **Variables reutilizables:** Permite cambiar paletas de colores en un solo punto.
> 2. **Anidamiento (Nesting):** Refleja la jerarquía visual del HTML, evitando la repetición constante de selectores padre.
> 3. **Mixins:** Permite empaquetar bloques de reglas CSS (como sombras o flexbox) y reutilizarlos con `@include`.
> 4. **Partials y `@use`:** Divide los estilos en múltiples archivos modulares (`_variables.scss`, `_mixins.scss`) que se compilan en un solo bundle optimizado.

### 8. ¿Por qué todavía no consumimos el API Spring Boot en esta semana?
> **Respuesta:**  
> Por delimitación didáctica y madurez arquitectónica: la meta de la **Semana 11** es dominar los **fundamentos de la vista en Angular**, la jerarquía de componentes, las directivas de control de flujo nativas y el preprocesamiento con SCSS sin la complejidad añadida de peticiones asíncronas, interceptores o manejo de errores de red.  
> En las semanas siguientes (Semana 12 a 14) se introducirán el enrutamiento (`RouterModule`), la comunicación avanzada `@Input`/`@Output`, los formularios reactivos y el cliente HTTP (`HttpClient`) para integrarse con la API REST de Spring Boot construida en las semanas 1 a 10.

---

## 7. Instrucciones de Ejecución

1. Ubicarse en el directorio del laboratorio:
   ```bash
   cd "Laboratorio11/Antonela_Jaimes"
   ```
2. Instalar dependencias (si aún no están instaladas):
   ```bash
   npm install
   ```
3. Iniciar el servidor de desarrollo local:
   ```bash
   npm start
   ```
   Abrir en el navegador: [http://localhost:4200/](http://localhost:4200/)

4. Compilar para producción (Build):
   ```bash
   npm run build
   ```
   Genera la carpeta optimizada `dist/catalogo-angular` lista para desplegarse.
