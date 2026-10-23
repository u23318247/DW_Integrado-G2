# Laboratorio 10 - API segura de inventario

Implementación integradora de Spring Boot con JPA/MySQL, JPQL, transacciones,
Spring Security y JWT.

## Requisitos

- Java 17 o superior
- Maven 3.9 o superior
- MySQL 8 con una base de datos llamada `tienda_db`

## Configuración

No se versionan contraseñas ni claves JWT. Antes de iniciar la aplicación,
define las variables de entorno en PowerShell:

```powershell
$env:DB_PASSWORD="tu_password_mysql"
$env:JWT_SECRET_BASE64="clave_base64_de_al_menos_32_bytes"
```

La clave debe decodificar al menos 32 bytes para HS256. El esquema se actualiza
con `spring.jpa.hibernate.ddl-auto=update`.

## Ejecución y pruebas

Desde `Laboratorio10\Adrian_Salcedo`:

```powershell
.\mvnw.cmd test
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

## Usuarios de laboratorio

Los usuarios se crean automáticamente si no existen:

- `usuario`: `ROLE_USER`
- `admin`: `ROLE_ADMIN` y `ROLE_USER`

Usa las credenciales entregadas para el laboratorio y no las reutilices en
entornos reales.

## Endpoints principales

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

El token de login se envía como `Authorization: Bearer <accessToken>`.

## Integración demostrada

- `ProductoRepository.buscarPorRangoPrecio` usa una consulta JPQL parametrizada.
- `InventarioService.registrarSalida` actualiza el stock e inserta
  `MovimientoStock` dentro de una única transacción.
- Un producto inexistente responde `404`; una cantidad inválida o un stock
  insuficiente responde `409`, sin cambios parciales.
- `/api/admin/resumen` combina conteos persistidos y está restringido a
  `ROLE_ADMIN`.
- Las pruebas automatizadas verifican la autorización sin depender de un JWT
  real.
