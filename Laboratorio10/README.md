# Laboratorio 10: Integración del Back-End Seguro con Spring Boot (APF2)

Este laboratorio consolida la **Semana 10 y el Segundo Avance del Proyecto Final (APF2) de Desarrollo Web Integrado (Unidad 2)**:
- Persistencia JPA/Hibernate con MySQL y H2.
- Consultas personalizadas JPQL (`buscarPorRangoPrecio`, conteo de stock crítico).
- Control de concurrencia y transacciones atómicas con `@Transactional` (Commit y Rollback).
- Seguridad sin estado (*stateless*) mediante Spring Security y **JWT (JSON Web Tokens)**.
- Control de acceso por roles (`ROLE_USER` y `ROLE_ADMIN`).
- Matriz completa de respuestas HTTP (200, 201, 400, 401, 403, 404, 409).

## Endpoints Principales

| Método | Ruta | Acceso |
| --- | --- | --- |
| POST | `/api/auth/login` | Público |
| GET | `/api/auth/me` | JWT válido |
| GET | `/api/productos` | USER o ADMIN |
| GET | `/api/productos/rango?min=100&max=1000` | USER o ADMIN |
| POST | `/api/productos` | ADMIN |
| PUT | `/api/productos/{id}` | ADMIN |
| DELETE | `/api/productos/{id}` | ADMIN |
| POST | `/api/inventario/{id}/salida?cantidad=2` | ADMIN |
| GET | `/api/admin/resumen` | ADMIN |

## Integrantes
- [Antonela_Jaimes](./Antonela_Jaimes/): Proyecto Spring Boot completo con endpoints REST, seguridad JWT, suite de pruebas automatizadas, evidencias de rollback transaccional y respuestas completas de reflexión.
- [Adrian_Salcedo](./Adrian_Salcedo/): Implementación integradora de inventario seguro con Spring Boot.
