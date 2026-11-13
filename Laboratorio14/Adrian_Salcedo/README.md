# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## GUÍA DE LABORATORIO N.° 14: SERVICIOS Y CONSUMO DE APIS EN ANGULAR
### HttpClient, Servicios, Inyección de Dependencias, Observables y Operaciones CRUD

* **Estudiante:** Antonela Jaimes
* **Curso:** Desarrollo Web Integrado — Semana 14
* **Tecnologías:** Angular 21/22, TypeScript 5.9/6, RxJS, HTML5, CSS3
* **API de Práctica:** JSONPlaceholder (REST API)

---

## 1. Logro de Aprendizaje y Resumen
Al finalizar esta práctica, se implementó una aplicación Angular standalone (`laboratorio-servicios-api`) desacoplada, donde la lógica de acceso a datos y consumo HTTP reside en `UsuarioService`, y el componente `Usuarios` administra el estado reactivo mediante **Signals** (`usuarios`, `cargando`, `guardando`, `error`, `mensaje`), formularios reactivos para creación y edición, y una tabla interactiva con operaciones CRUD completas.

---

## 2. Estructura del Proyecto

```text
Laboratorio14/Antonela_Jaimes/
├── README.md                                  <- Informe completo de Laboratorio 14
└── laboratorio-servicios-api/                 <- Proyecto Angular standalone
    ├── angular.json
    ├── package.json
    ├── tsconfig.json
    └── src/
        ├── index.html
        ├── main.ts
        ├── styles.css
        └── app/
            ├── app.config.ts                  <- provideHttpClient()
            ├── app.routes.ts                  <- Ruta /usuarios
            ├── app.ts / app.html / app.css
            └── usuarios/
                ├── usuario.model.ts           <- Interfaz Usuario y tipo UsuarioEntrada
                ├── usuario.service.ts         <- Métodos GET, POST, PUT, PATCH, DELETE
                ├── usuarios.ts                <- Lógica con Signals y FormBuilder
                ├── usuarios.html              <- Formulario y tabla reactiva
                └── usuarios.css               <- Estilos responsivos
```

---

## 3. Fundamentos y Métodos HTTP Implementados

| Método | Endpoint | Implementación en `UsuarioService` | Resultado en la Aplicación |
| :---: | :---: | :--- | :--- |
| **GET** | `/users` | `obtenerTodos(): Observable<Usuario[]>` | Carga inicial de 10 usuarios de la API pública. |
| **GET** | `/users/:id` | `obtenerPorId(id): Observable<Usuario>` | Consulta unívoca de usuario por su clave primaria. |
| **POST** | `/users` | `crear(usuario): Observable<Usuario>` | Envía el payload del formulario y agrega el registro al inicio de la tabla. |
| **PUT** | `/users/:id` | `actualizar(id, usuario): Observable<Usuario>` | Reemplaza la totalidad de los campos editables del usuario seleccionado. |
| **PATCH** | `/users/:id` | `actualizarParcial(id, cambios)` | Modificación granular de campos específicos (ej. email). |
| **DELETE**| `/users/:id` | `eliminar(id): Observable<unknown>` | Solicita la baja del registro y lo remueve de la señal reactiva local. |

---

## 4. Respuestas Técnicas y Control de Errores

* **Inyección de Dependencias moderna:** Se utilizó `inject(HttpClient)` e `inject(UsuarioService)`, eliminando la necesidad de constructores con inyecciones prolijas.
* **Manejo del ciclo de vida asíncrono con `finalize()`:** Se empleó el operador `finalize(() => this.cargando.set(false))` dentro del pipeline de RxJS para garantizar que el spinner de carga se oculte tanto en respuestas exitosas como ante errores de red (404, 500 o fallas CORS).
* **Control de errores controlado:** Ante una URL incorrecta (`/usuarios-inexistentes`), el suscriptor intercepta el código HTTP 404 y actualiza la señal `error`, renderizando un mensaje amigable al usuario sin congelar la interfaz.

---

## 5. Actividades Complementarias Implementadas

1. **Cuadro de búsqueda y filtro local:** Filtrado reactivo de usuarios en tiempo real por coincidencia de nombre o correo electrónico.
2. **Conexión con Spring Boot (Full Stack):** La arquitectura de `UsuarioService` está diseñada para conmutar `apiUrl` de `jsonplaceholder.typicode.com` a `http://localhost:8080/api/usuarios` sin requerir modificaciones en la plantilla HTML ni en la lógica del componente.

---
**Autor:** Antonela Jaimes | Desarrollo Web Integrado (UTP)
