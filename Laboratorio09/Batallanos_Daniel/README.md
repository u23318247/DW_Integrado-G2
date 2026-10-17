# Laboratorio 09 - Spring Security: JWT (API REST Stateless)

Migracion de la arquitectura de seguridad de HTTP Basic (semana 8) a **JSON Web Tokens (JWT)**
firmados con **HMAC-SHA256 (HS256)**. La API es estrictamente *stateless*: el cliente se
autentica una vez, obtiene un token y lo envia en `Authorization: Bearer <token>`.

- **Autor:** Batallanos Daniel
- **Paquete base:** `com.utp.tienda`
- **Stack:** Java 21+ · Spring Boot 4.1.1 · Spring Security 7.1 · OAuth2 Resource Server (Nimbus JOSE JWT) · Spring Data JPA · MySQL 8 · Maven · JUnit 5 + MockMvc

---

## 1. Flujo de ejecucion

```
1. POST /api/auth/login  { "username": "admin", "password": "Admin123*" }
2. AuthenticationManager valida contra MySQL (CustomUserDetailsService + BCrypt)
3. TokenService emite un JWT firmado (HS256) con claims sub, iss, iat, exp y roles
4. El cliente envia Authorization: Bearer <token> en cada peticion
5. BearerTokenAuthenticationFilter verifica firma y vigencia
6. JwtAuthenticationConverter convierte el claim "roles" en autoridades (ROLE_USER / ROLE_ADMIN)
7. SecurityFilterChain valida el acceso por ruta y metodo HTTP
```

### Claims del token emitido

| Claim | Valor | Descripcion |
| :--- | :--- | :--- |
| `sub` | `admin` / `usuario` | Nombre de usuario |
| `iss` | `tienda-api` | Emisor (`app.jwt.issuer`), validado por el decoder |
| `iat` | instante actual | Momento de emision |
| `exp` | iat + 30 min | Vencimiento (`app.jwt.ttl-minutes`) |
| `roles` | `["ROLE_ADMIN","ROLE_USER"]` | Autoridades, ya con prefijo `ROLE_` |

> `JwtGrantedAuthoritiesConverter.setAuthorityPrefix("")` es indispensable: sin ella Spring
> antepondria `SCOPE_` y las expresiones `hasRole(...)` no coincidirian.

---

## 2. Matriz de autorizacion

| Endpoint / Patron | Metodo | Acceso | Resultado |
| :--- | :--- | :--- | :--- |
| `/api/auth/login` | `POST` | Publico | 200 + token · 401 credenciales invalidas |
| `/api/publico/**` | `GET` | Publico | 200 sin token |
| `/api/auth/me` | `GET` | Autenticado | 200 con token · 401 sin token |
| `/api/productos/**` | `GET` | `USER` o `ADMIN` | 200 · 401 sin token · 403 otro rol |
| `/api/productos/**` | `POST`, `PUT`, `PATCH`, `DELETE` | Solo `ADMIN` | 200/201 · 401 sin token · 403 si es `USER` |
| `/api/admin/reporte` | `GET` | Solo `ADMIN` | 200 · 403 si es `USER` |
| `/api/inventario/ajustes` | `POST` | Solo `ADMIN` | 201 · 403 si es `USER` |
| `/api/inventario/**` | `GET` | `USER` o `ADMIN` | 200 · 401 sin token |
| Cualquier otra ruta | `*` | `anyRequest().authenticated()` | 401 sin token |

El orden en `SecurityFilterChain` es critico: gana la **primera** coincidencia, por eso la regla
especifica `POST /api/inventario/ajustes` precede a la general `/api/inventario/**`.

---

## 3. Estructura de archivos

```text
src/main/java/com/utp/tienda/
├── TiendaApplication.java
├── config/
│   ├── JwtConfig.java                  <-- NUEVO: SecretKey, JwtEncoder, JwtDecoder + validacion
│   ├── SecurityConfig.java             <-- MODIFICADO: OAuth2 Resource Server
│   ├── MethodSecurityConfig.java       @EnableMethodSecurity (conservado)
│   └── DatosSeguridadIniciales.java    <-- PRESERVADO de la semana 8
├── controller/
│   ├── AuthController.java             <-- NUEVO: POST /api/auth/login, GET /api/auth/me
│   ├── AdminReporteController.java     <-- NUEVO: GET /api/admin/reporte
│   ├── ProductoController.java         <-- PRESERVADO
│   ├── PublicoController.java          <-- PRESERVADO
│   └── InventarioController.java       <-- PRESERVADO
├── dto/
│   ├── LoginRequest.java               <-- NUEVO: record de entrada
│   ├── TokenResponse.java              <-- NUEVO: record de respuesta
│   ├── ProductoRequest.java  ProductoResponse.java
│   └── AjusteInventarioRequest.java  MovimientoStockResponse.java  ErrorResponse.java
├── model/
│   ├── Usuario.java  Rol.java  Producto.java
│   └── MovimientoStock.java  TipoMovimiento.java
├── repository/
│   ├── UsuarioRepository.java  RolRepository.java
│   └── ProductoRepository.java  MovimientoStockRepository.java
├── security/
│   ├── CustomUserDetailsService.java   <-- PRESERVADO
│   └── TokenService.java               <-- NUEVO: emision del JWT
└── exception/
    ├── RecursoNoEncontradoException.java  ReglaNegocioException.java
    └── ApiExceptionHandler.java

src/test/java/com/utp/tienda/
├── TiendaApplicationTests.java
├── controller/
│   ├── ProductoControllerSecurityTest.java    (14 pruebas)
│   ├── InventarioControllerSecurityTest.java  (7 pruebas)
│   ├── PublicoControllerSecurityTest.java     (5 pruebas)
│   └── AuthControllerSecurityTest.java        <-- NUEVO (5 pruebas)
└── security/
    ├── TokenServiceTest.java                  <-- NUEVO: firma y decodificacion (5 pruebas)
    ├── SeguridadIntegracionTest.java          (18 pruebas end-to-end con JWT real)
    └── CustomUserDetailsServiceTest.java      (4 pruebas unitarias)
```

**Conservado de la semana 8** (no se reescribio): base de datos MySQL `tienda_db`, entidades
`Usuario`/`Rol`, `UsuarioRepository`, `RolRepository`, `Producto`, `CustomUserDetailsService`,
`BCryptPasswordEncoder`, logica de dominio de productos e inventario.

---

## 4. Modelo de datos (sin cambios)

**`roles`** · `id` (PK, auto) · `nombre` (VARCHAR 50, UNIQUE, con prefijo `ROLE_`)

**`usuarios`** · `id` (PK, auto) · `username` (VARCHAR 80, UNIQUE) · `password` (VARCHAR 100,
hash BCrypt) · `activo` (BOOLEAN, `true`)

**`usuarios_roles`** · `usuario_id` (FK) · `rol_id` (FK)

---

## 5. Datos semilla (conservados)

| Usuario | Contrasena | Roles | Puede hacer |
| :--- | :--- | :--- | :--- |
| `usuario` | `Usuario123*` | `ROLE_USER` | Leer productos e inventario, `GET /api/auth/me` |
| `admin` | `Admin123*` | `ROLE_ADMIN`, `ROLE_USER` | Todo, incluido `GET /api/admin/reporte` |

Las contrasenas se almacenan como hash BCrypt, nunca en texto plano.

---

## 6. Endpoints

| Metodo | Ruta | Acceso | Descripcion |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Publico | Devuelve `{tokenType, accessToken, expiresInSeconds}` |
| `GET` | `/api/auth/me` | Autenticado | `{username, roles}` del portador del token |
| `GET` | `/api/publico/estado` | Publico | `{"estado":"API disponible"}` |
| `GET` | `/api/productos` | USER, ADMIN | Lista productos activos |
| `GET` | `/api/productos/{id}` | USER, ADMIN | Detalle (404 si no existe) |
| `POST` | `/api/productos` | ADMIN | Crea producto (201) |
| `PUT` | `/api/productos/{id}` | ADMIN | Reemplazo completo (200) |
| `PATCH` | `/api/productos/{id}` | ADMIN | Actualizacion parcial (200) |
| `DELETE` | `/api/productos/{id}` | ADMIN | Elimina (204) |
| `GET` | `/api/inventario/movimientos` | USER, ADMIN | Bitacora de movimientos |
| `GET` | `/api/inventario/stock/{id}` | USER, ADMIN | Stock actual |
| `POST` | `/api/inventario/ajustes` | ADMIN | Ajusta stock y audita el movimiento |
| `GET` | `/api/admin/reporte` | ADMIN | `{fecha, totalProductos}` |

---

## 7. Como ejecutar

### Requisitos
- JDK 17 o superior (probado con Java 25) · Maven 3.9+ · MySQL 8.x

### Base de datos

```sql
CREATE DATABASE tienda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Las tablas se crean solas con `spring.jpa.hibernate.ddl-auto=update`. Ajusta credenciales en
`src/main/resources/application.properties`.

### Clave criptografica (obligatoria)

La propiedad `app.jwt.secret-base64` lee la variable de entorno `JWT_SECRET_BASE64`, que debe
supplyar un Base64 de **al menos 32 bytes (256 bits)**. Si la clave falta o es corta, la
aplicacion falla al arrancar con `IllegalArgumentException`.

Generar una clave:

```powershell
$b = New-Object byte[] 48
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b)
[Convert]::ToBase64String($b)
```

Arrancar la API:

```bash
mvn spring-boot:run
```

### Probar con cURL

```bash
# 1) Obtener token (200 OK)
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123*"}' | jq -r .accessToken)

# Credenciales invalidas -> 401
curl -i -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"malaclave"}'

# Sin token -> 401 con WWW-Authenticate: Bearer
curl -i http://localhost:8080/api/productos

# Token invalido o vencido -> 401
curl -i -H "Authorization: Bearer token.invalido" http://localhost:8080/api/productos

# USER: 200 en lectura
USER_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"usuario","password":"Usuario123*"}' | jq -r .accessToken)

curl -i -H "Authorization: Bearer $USER_TOKEN" http://localhost:8080/api/productos

# USER: 403 al escribir y 403 en el reporte de admin
curl -i -H "Authorization: Bearer $USER_TOKEN" -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" -d '{"nombre":"Teclado","precio":249.90,"stock":10}'
curl -i -H "Authorization: Bearer $USER_TOKEN" http://localhost:8080/api/admin/reporte

# ADMIN: 201 al crear y 200 en el reporte
curl -i -H "Authorization: Bearer $TOKEN" -X POST http://localhost:8080/api/productos \
  -H "Content-Type: application/json" -d '{"nombre":"Teclado","precio":249.90,"stock":10}'
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/admin/reporte

# Perfil del token
curl -i -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/auth/me
```

### Ejecutar las pruebas

```bash
mvn clean test
```

Las pruebas usan **H2 en memoria** y una clave JWT de prueba definida en
`src/test/resources/application.properties`, por lo que **no requieren MySQL ni la variable de
entorno `JWT_SECRET_BASE64`**.

---

## 8. Pruebas

**`TokenServiceTest`** (Tarea 9): construye encoder, decoder y `TokenService` a mano con una clave
HMAC de 32 bytes reservada para test, y valida `sub`, `iss`, `exp`, `iat`, el claim `roles`, el
algoritmo `HS256` del encabezado y el calculo de `expiresInSeconds`.

**`SeguridadIntegracionTest`** (18 pruebas, contexto completo con JWT real): emision de token,
credenciales invalidas (401), ausencia de cookie `JSESSIONID`, HTTP Basic desactivado (reto
`Bearer`, nunca `Basic`), matriz USER vs ADMIN, `/api/auth/me`, token con firma alterada (401),
token vencido (401), token malformado (401), ajustes de inventario por rol, hash BCrypt y no
exposicion de credenciales.

**`AuthControllerSecurityTest`**: slice del controlador con `AuthenticationManager` y `TokenService`
simulados (login 200/401/400, `/api/auth/me` con y sin token).

**`ProductoControllerSecurityTest`**, **`InventarioControllerSecurityTest`**,
**`PublicoControllerSecurityTest`**: slices `@WebMvcTest` con `@Import(SecurityConfig.class)`, de
modo que se ejercitan las reglas reales del `SecurityFilterChain`.

---

## 9. Criterios de aceptacion

- [x] **Sin dependencias de sesion**: `SessionCreationPolicy.STATELESS`; ninguna peticion emite `JSESSIONID`.
- [x] **HTTP Basic desactivado**: `.httpBasic(AbstractHttpConfigurer::disable).formLogin(...)`; las rutas protegidas responden 401 con `WWW-Authenticate: Bearer`.
- [x] **Emision de token**: `POST /api/auth/login` con `usuario`/`Usuario123*` o `admin`/`Admin123*` responde 200 con Bearer token.
- [x] **Credenciales invalidas**: `POST /api/auth/login` con contrasena erronea responde 401.
- [x] **Roles por token**: `ROLE_USER` -> 200 en `GET /api/productos`, 403 en `POST /api/productos` y `GET /api/admin/reporte`; `ROLE_ADMIN` -> acceso total.
- [x] **Tokens invalidos**: firma alterada o token vencido se rechazan con 401.
- [x] **`TokenServiceTest` pasa** con `mvn test`.
- [x] **Conservado de la semana 8**: MySQL `tienda_db`, entidades `Usuario`/`Rol`, `UsuarioRepository`, `BCryptPasswordEncoder`, logica de productos.
- [x] **Sin fuga de credenciales**: los endpoints usan DTOs y `Usuario.password` esta marcado con `@JsonIgnore`.

---

## 10. Notas de seguridad

- La clave HMAC **nunca** se versiona: viaja por variable de entorno. La clave de
  `src/test/resources/application.properties` es solo para pruebas.
- El TTL por defecto es de 30 minutos; un token expirado o firmado con otra clave se rechaza en
  `JwtDecoder`, antes de llegar al controlador.
- `JwtConfig` valida la longitud de la clave al arrancar (fail-fast) en lugar de dejar que
  HMAC-SHA256 rechace la peticion en tiempo de ejecucion.
- La API no usa cookies ni estado en servidor: horizontalmente escalable.
- `DatosSeguridadIniciales` sigue siendo idempotente: los datos de la semana 8 no se duplican
  en cada arranque.
- En un entorno real conviene rotar la clave, guardar las contrasenas de los seeds fuera del
  repositorio y registrar los accesos a `/api/auth/login`.