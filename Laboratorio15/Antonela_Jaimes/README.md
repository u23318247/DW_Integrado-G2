# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## GUÍA DE LABORATORIO N.° 15: SEGURIDAD, JWT E INTERCEPTORES EN ANGULAR
### Autenticación Stateless, Guards Funcionales, Inyección de Bearer Tokens y Control de Acceso

* **Estudiante:** Antonela Jaimes
* **Curso:** Desarrollo Web Integrado — Semana 15
* **Tecnologías:** Angular 21/22, TypeScript, RxJS, Spring Security, JWT

---

## 1. Resumen y Logro de Aprendizaje
Se implementó un módulo de seguridad completo (`modulo-seguridad-jwt`) en Angular para gestionar el ciclo de vida de la autenticación de usuarios:
1. **Interceptor HTTP Funcional (`jwtInterceptor`):** Clona automáticamente las solicitudes dirigidas a endpoints `/api/**` para inyectar la cabecera `Authorization: Bearer <token>`.
2. **Guardian de Rutas (`authGuard`):** Protege la vista del `/panel` administrativo impidiendo el acceso a usuarios anónimos y redirigiendo al `/login`.
3. **Servicio Reactivo (`AuthService`):** Administra el estado de la sesión mediante **Signals** (`usuarioActual`, `token`, `estaAutenticado`, `esAdmin`).

---

## 2. Estructura del Módulo

```text
Laboratorio15/Antonela_Jaimes/
├── README.md
└── modulo-seguridad-jwt/
    └── src/app/
        ├── core/
        │   ├── auth.guard.ts           <- Protección canActivate
        │   └── jwt.interceptor.ts      <- Inyección de cabecera Bearer
        ├── models/
        │   └── auth.model.ts           <- Interfaces UsuarioSesion y AuthResponse
        ├── services/
        │   └── auth.service.ts         <- Métodos login, logout y Signals
        └── pages/
            ├── login/                  <- Formulario de acceso reactivo
            └── panel/                  <- Vista protegida con datos de sesión
```

---

## 3. Conclusiones
* Los interceptores funcionales de Angular reducen la duplicación de código evitando agregar manualmente tokens de autorización en cada petición HTTP.
* La combinación de `AuthGuard` en el frontend y Spring Security en el backend garantiza una defensa en profundidad robusta para aplicaciones corporativas.

---
**Autor:** Antonela Jaimes | Desarrollo Web Integrado (UTP)
