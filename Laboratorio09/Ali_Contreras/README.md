# Ali_Contreras — Guía de Laboratorio 09

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 9
**Proyecto:** API Tienda — Autenticación con JWT en Spring Boot

Evolución del Lab 8: se reemplaza **HTTP Basic** por **JWT**. El usuario inicia sesión una vez en
`POST /api/auth/login`, recibe un token firmado (HS256) y lo envía en cada petición como
`Authorization: Bearer <token>`. Se conservan los usuarios y roles de MySQL, BCrypt y la misma
política por roles; el dominio de productos no se modificó.

## Flujo

```
POST /api/auth/login ─► AuthenticationManager ─► UserDetailsService (MySQL) + BCrypt
        │                                              │
        │                       credenciales válidas ◄─┘
        ▼
TokenService ─► JWT firmado HS256 { iss, sub, iat, exp, roles }
        │
        ▼
GET /api/productos  +  Authorization: Bearer <token>
        │
        ▼
OAuth2 Resource Server ─► verifica firma, exp e iss ─► roles → GrantedAuthority
        │                          (inválido → 401)
        ▼
SecurityFilterChain ─► ¿rol suficiente? (no → 403) ─► Controller → Service → Repository
```

## Qué cambió respecto al Lab 8

| Archivo | Cambio |
|---------|--------|
| `pom.xml` | + `spring-boot-starter-oauth2-resource-server`, + `spring-boot-starter-validation` |
| `config/JwtConfig.java` | **Nuevo**: `SecretKey`, `JwtEncoder` y `JwtDecoder` (HS256 + validación de `iss`) |
| `security/TokenService.java` | **Nuevo**: emite el JWT a partir de la `Authentication` validada |
| `dto/LoginRequest.java`, `dto/TokenResponse.java` | **Nuevos**: contrato JSON del login (records) |
| `controller/AuthController.java` | **Nuevo**: `POST /api/auth/login` y `GET /api/auth/me` |
| `config/SecurityConfig.java` | Sin HTTP Basic ni formLogin; `AuthenticationManager`, `JwtAuthenticationConverter` y `oauth2ResourceServer().jwt()` |
| `controller/AdminController.java`, `service/ReporteService.java` | **Nuevos**: reto `GET /api/admin/reporte` |
| `Usuario`, `Rol`, repositorios, `CustomUserDetailsService`, `DatosSeguridadIniciales` | Sin cambios |

## Cómo ejecutar

Requisitos: Java 21+, Maven y MySQL en `localhost:3306` (usuario `root`, contraseña `root`),
con la base `tienda_db` de las semanas 7 y 8.

La clave de firma **no está en el repositorio**: se lee de la variable de entorno
`JWT_SECRET_BASE64`. Debe crearse en la **misma terminal** donde se arranca la aplicación.

**Windows PowerShell**
```powershell
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
mvn spring-boot:run
```

**Git Bash / Linux / macOS**
```bash
export JWT_SECRET_BASE64="$(openssl rand -base64 32)"
mvn spring-boot:run
```

Si falta la variable, la aplicación **no arranca** (comprobado):
`Could not resolve placeholder 'JWT_SECRET_BASE64'`. Cada arranque con una clave nueva invalida
los tokens anteriores; basta con volver a iniciar sesión.

Usuarios de laboratorio: `usuario` / `Usuario123*` (ROLE_USER) y `admin` / `Admin123*`
(ROLE_ADMIN, ROLE_USER).

### En Postman

1. `POST http://localhost:8080/api/auth/login` → Body → raw → JSON:
   `{"username":"usuario","password":"Usuario123*"}`
2. Copiar el valor de `accessToken`.
3. En la siguiente petición: **Authorization → Type: Bearer Token** → pegar el token (sin escribir
   la palabra *Bearer*).

## Política de acceso

| Recurso | Método | Acceso |
|---------|--------|--------|
| `/api/auth/login` | POST | Público |
| `/api/publico/**` | Cualquiera | Público |
| `/api/productos/**` | GET | ROLE_USER o ROLE_ADMIN |
| `/api/productos/**` | POST / PUT / PATCH / DELETE | ROLE_ADMIN |
| `/api/inventario/**` | GET | ROLE_USER o ROLE_ADMIN |
| `/api/inventario/ajustes` | POST | ROLE_ADMIN |
| `/api/admin/**` | Cualquiera | ROLE_ADMIN *(reto)* |
| Cualquier otro (p. ej. `/api/auth/me`) | Cualquiera | Token válido |

## Matriz de pruebas (sección 11) — resultados obtenidos contra MySQL

| N° | Solicitud | Token | Esperado | Obtenido |
|----|-----------|-------|----------|----------|
| 01 | GET /api/publico/estado | Ninguno | 200 | **200** `{"estado":"API disponible"}` |
| 02 | POST /api/auth/login (válido) | Ninguno | 200 + JWT | **200** `{"tokenType":"Bearer","accessToken":"eyJ...","expiresInSeconds":1800}` |
| 03 | POST /api/auth/login (contraseña errónea) | Ninguno | 401 | **401** `{"error":"Credenciales inválidas"}` |
| 04 | GET /api/productos | Ninguno | 401 | **401** |
| 05 | GET /api/productos | USER | 200 | **200** |
| 06 | POST /api/productos | USER | 403 | **403** |
| 07 | POST /api/productos | ADMIN | 200 o 201 | **201** |
| 08 | GET /api/auth/me | ADMIN | 200 + roles | **200** `{"roles":["ROLE_ADMIN","ROLE_USER"],"username":"admin"}` |
| 09 | GET /api/productos | JWT modificado | 401 | **401** |
| 10 | GET /api/productos | JWT vencido | 401 | **401** |

- **Caso 09:** se cambió un carácter del tercer segmento (la firma) de un token válido.
- **Caso 10:** se firmó con la clave correcta un token con `exp` de hace 10 minutos (supera la
  tolerancia de reloj de 60 s del validador). La respuesta incluyó
  `WWW-Authenticate: Bearer error="invalid_token", error_description="... Jwt expired at ..."`.

### Reto de aplicación: `GET /api/admin/reporte`

| Token | Resultado |
|-------|-----------|
| Ninguno | **401** |
| USER | **403** |
| ADMIN | **200** `{"fechaServidor":"2026-10-01T21:22:22.554989","totalProductos":6}` |

### 11.3. Contenido del JWT (decodificado localmente)

```
header : {"alg": "HS256"}
payload: {"iss": "tienda-api", "sub": "usuario", "exp": 1790909538, "iat": 1790907738, "roles": ["ROLE_USER"]}
```

El payload solo se codifica en Base64URL, **no se cifra**: cualquiera puede leerlo. Por eso no
contiene la contraseña ni el hash (verificado también en una prueba automatizada). La firma impide
modificarlo sin la clave.

## Pruebas automatizadas

```bash
mvn test
```

**43 pruebas, 0 fallos.** Corren sobre H2 con una clave exclusiva de prueba definida en
`src/test/resources/application.properties` (no se usa fuera de los tests):

- `TokenServiceTest` (1, la de la guía): emite y decodifica un JWT y verifica `sub`, `iss`,
  `roles`, `exp` y `alg=HS256`, sin arrancar Spring ni MySQL.
- `JwtIntegracionTest` (18): la **matriz completa 01–10** con login real contra los usuarios de la
  base de datos, más emisor distinto → 401, login con campos vacíos → 400, que el JWT no contenga
  la contraseña, que las contraseñas sigan en BCrypt, el reto `/api/admin/reporte` (401/403/200) y
  que `@PreAuthorize` del Lab 8 siga activo.
- `ProductoControllerSecurityTest` (14): reglas por rol con `@WebMvcTest` + `@WithMockUser`,
  incluido el reporte de administrador.
- Las 10 pruebas de JPQL y transacciones de la semana 7 siguen pasando.

> **Nota de versiones:** la guía usa Spring Boot 4.1 / Spring Security 7.1. Este proyecto usa
> **Spring Boot 3.5.5** (Spring Security 6.5) con Java 21, como los laboratorios anteriores. Por
> eso el starter se llama `spring-boot-starter-oauth2-resource-server` (en Boot 4 es
> `spring-boot-starter-security-oauth2-resource-server`), y en `TokenServiceTest` el emisor se lee
> con `getClaimAsString("iss")`: en Security 6.x, `getIssuer()` exige que `iss` sea una URL y
> `tienda-api` no lo es. La validación del emisor la sigue haciendo `JwtDecoder`.

## Extensión opcional: ¿por qué un token de ADMIN sigue sirviendo si le quito el rol?

El JWT es **autocontenido**: el servidor confía en los `roles` que viajan dentro del token y no
vuelve a consultar MySQL en cada petición. Si se retira `ROLE_ADMIN` a un usuario en la base de
datos, un token emitido antes seguirá diciendo `ROLE_ADMIN` y será aceptado hasta su `exp`.

Mitigaciones posibles:

- **Expiración breve** del access token (por ejemplo 5–15 min) y un *refresh token* para renovar.
- **Revocación por `jti`**: dar a cada token un identificador único y mantener una lista de
  tokens revocados que el filtro consulte.
- **Versión de sesión**: guardar un número de versión en el usuario e incluirlo en el token; al
  cambiar roles o la contraseña se incrementa y los tokens con versión antigua se rechazan.
- **Consultar permisos sensibles en cada solicitud**: para operaciones críticas, volver a
  verificar el rol en la base de datos en lugar de confiar solo en el claim.

## Límites de seguridad de este laboratorio

- Con usuarios reales es obligatorio **HTTPS**: un JWT interceptado puede usarse hasta que expire.
- No poner tokens en la URL, logs, capturas ni en Git. No guardar contraseñas en el JWT.
- HS256 usa una sola clave para firmar y verificar; sirve para una sola aplicación. En sistemas
  distribuidos conviene RS256/ES256, JWK, rotación de claves y un proveedor de identidad (OIDC).
- En un frontend Angular, evitar tokens de larga duración en `localStorage` por el riesgo de XSS.
- Si en el futuro el token viaja en cookies, hay que volver a evaluar la protección CSRF.
