# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## GUÍA DE LABORATORIO N.° 16: COMUNICACIÓN ENTRE FRONTEND Y BACKEND
### Angular 22 + Spring Boot REST API (Gestión de Productos con HttpClient y CORS)

* **Estudiante:** Antonela Jaimes
* **Curso:** Desarrollo Web Integrado — Semana 16
* **Tecnologías:** Angular 21/22, Spring Boot 3, Java 17/25, Maven, REST JSON

---

## 1. Resumen y Logro de Aprendizaje
En este laboratorio se construyó una arquitectura desacoplada full-stack completa:
1. **Backend REST (`backend-productos`):** Controlador REST Spring Boot con operaciones CRUD completas (`/api/productos`), validación con `@Valid`, DTOs tipados, excepciones personalizadas (`RecursoNoEncontradoException`), manejador global (`GlobalExceptionHandler`) y política CORS configurada en `CorsConfig.java`.
2. **Frontend Reactivo (`frontend-productos`):** Aplicación Angular standalone consumiendo la API de Spring Boot con `HttpClient`, `ProductoService`, `HttpErrorInterceptor` y formularios reactivos.

---

## 2. Estructura del Laboratorio

```text
Laboratorio16/Antonela_Jaimes/
├── README.md
├── backend-productos/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/utp/backend_productos/
│       │   ├── config/CorsConfig.java
│       │   ├── controller/ProductoController.java
│       │   ├── dto/ProductoRequest.java & ErrorResponse.java
│       │   ├── exception/GlobalExceptionHandler.java & RecursoNoEncontradoException.java
│       │   ├── model/Producto.java
│       │   └── service/ProductoService.java
│       └── resources/application.properties
└── frontend-productos/
    └── src/app/
        ├── core/http-error-interceptor.ts
        ├── models/producto.ts
        ├── services/producto.service.ts
        └── productos/
            ├── productos.component.ts
            ├── productos.component.html
            └── productos.component.css
```

---

## 3. Resolución de Problemas Frecuentes
* **CORS:** Resuelto permitiendo el origen `http://localhost:4200` y métodos `GET, POST, PUT, DELETE, OPTIONS` en `CorsConfig.java`.
* **Inconsistencia de Fechas:** Se normalizó el formato en `application.properties` con `spring.jackson.date-format=yyyy-MM-dd` compatible con el input `type="date"` de Angular.
* **Manejo de Errores 400 y 404:** Interceptados mediante `GlobalExceptionHandler` retornando payloads estructurados `ErrorResponse`.

---
**Autor:** Antonela Jaimes | Desarrollo Web Integrado (UTP)
