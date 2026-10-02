# Laboratorio N.° 09: Autenticación con JWT en Spring Boot

- **Curso:** Desarrollo Web Integrado
- **Semana:** 9
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java 17/21, Spring Boot 3.4.1, Spring Security 6.4.2, JJWT 0.11.5, Spring Data JPA, Hibernate, MySQL, BCrypt, RESTful APIs

---

## 📌 1. Descripción del Proyecto

En este laboratorio se realiza la transición completa de la seguridad de la API REST desde la autenticación básica HTTP Basic (implementada en el Laboratorio 8) hacia un esquema moderno de **Tokens Web JSON (JWT)** totalmente sin estado (**Stateless**).

Principales características implementadas:
1. **Emisión de Tokens JWT (`POST /api/auth/login`):** Valida las credenciales (`username` y `password`) contra la base de datos MySQL usando `AuthenticationManager` y genera un token compacto firmado digitalmente con algoritmo HMAC-SHA256 (HS256).
2. **Estructura Estándar de JWT:**
   - **Header:** Algoritmo de firma (`HS256`) y tipo de token (`JWT`).
   - **Payload / Claims:** Emisor (`iss`), sujeto/username (`sub`), fecha de emisión (`iat`), tiempo de expiración configurable (`exp`) y lista de autoridades asignadas (`roles: ["ROLE_ADMIN", "ROLE_USER"]`).
   - **Signature:** Firma criptográfica calculada mediante clave secreta codificada en Base64 de 256 bits (`app.jwt.secret-base64`).
3. **Filtro de Intercepción de Peticiones (`JwtAuthenticationFilter`):**
   - Intercepta cada solicitud HTTP entrante heredando de `OncePerRequestFilter`.
   - Extrae el encabezado `Authorization: Bearer <token>`.
   - Verifica la firma y vigencia del token contra la clave y emisor autorizados.
   - Reconstruye la sesión de seguridad en el `SecurityContextHolder` con un `UsernamePasswordAuthenticationToken` conteniendo los roles y detalles del usuario.
4. **Endpoint de Perfil (`GET /api/auth/me`):** Expone los datos de la identidad actual autenticada a partir del contexto de seguridad (`@AuthenticationPrincipal`).
5. **Reto de Aplicación - Módulo Administrativo (`GET /api/admin/reporte`):** Endpoint protegido exclusivamente para usuarios con autoridad `ROLE_ADMIN` (Sección 15.1).
6. **Endpoints Públicos y de Recursos:**
   - `GET /api/publico/estado` accesible sin token (público).
   - `GET /api/productos/**` disponible para `ROLE_USER` y `ROLE_ADMIN`.
   - Operaciones de mutación (`POST`, `PUT`, `DELETE` en `/api/productos/**`) exclusivas para `ROLE_ADMIN`.
7. **Suite de Pruebas Unitarias (`TokenServiceTest`):** Valida la emisión, extracción de claims y rechazo automático de tokens manipulados o con firma inválida.

---

## 📋 2. Matriz de Endpoints y Control de Acceso

| Método | Endpoint | Acceso Requerido | Descripción | Código HTTP Esperado |
| :--- | :--- | :--- | :--- | :---: |
| `POST` | `/api/auth/login` | Público | Autenticación con credenciales y entrega de JWT | `200 OK` / `401 Unauthorized` |
| `GET` | `/api/publico/estado` | Público | Verificación de salud y estado de la API | `200 OK` |
| `GET` | `/api/auth/me` | Autenticado (`USER` o `ADMIN`) | Consulta de datos del perfil y roles del token | `200 OK` (401 si no hay token) |
| `GET` | `/api/productos` | Autenticado (`USER` o `ADMIN`) | Listado general de productos | `200 OK` (401 si no hay token) |
| `GET` | `/api/productos/activos` | Autenticado (`USER` o `ADMIN`) | Listado de productos habilitados | `200 OK` (401 si no hay token) |
| `GET` | `/api/productos/{id}` | Autenticado (`USER` o `ADMIN`) | Consulta de producto por identificador | `200 OK` / `404 Not Found` |
| `POST` | `/api/productos` | `ROLE_ADMIN` | Registro de nuevo producto | `201 Created` (403 si es USER) |
| `PUT` | `/api/productos/{id}` | `ROLE_ADMIN` | Actualización de datos de producto | `200 OK` (403 si es USER) |
| `DELETE` | `/api/productos/{id}` | `ROLE_ADMIN` | Eliminación de producto | `204 No Content` (403 si es USER) |
| `GET` | `/api/admin/reporte` | `ROLE_ADMIN` | Reporte administrativo protegido (Reto 15.1) | `200 OK` (403 si es USER) |

---

## 👥 3. Usuarios de Prueba y Roles Precargados

Los datos de seguridad se cargan automáticamente al iniciar la aplicación mediante `DatosSeguridadIniciales`:

| Usuario | Contraseña | Roles Asignados |
| :--- | :--- | :--- |
| `usuario` | `user123` | `ROLE_USER` |
| `admin` | `admin123` | `ROLE_ADMIN`, `ROLE_USER` |

> Todas las contraseñas se almacenan con hash unidireccional y sal aleatoria usando `BCryptPasswordEncoder`.

---

## ⚙️ 4. Configuración del Token JWT (`application.properties`)

```properties
app.jwt.issuer=tienda-api
app.jwt.ttl-minutes=30
app.jwt.secret-base64=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
```

- **`issuer`**: Identificador de la entidad emisora del token.
- **`ttl-minutes`**: Tiempo de vida útil del token (30 minutos para mitigar riesgos por exposición).
- **`secret-base64`**: Clave simétrica de 256 bits codificada en Base64 utilizada para firmar y validar con `HS256`.

---

## 🧪 5. Matriz de Pruebas Manuales (Sección 11 de la Guía)

| N.° | Caso de Prueba | Método y URL | Encabezados / Body | Resultado Esperado |
| :---: | :--- | :--- | :--- | :---: |
| 1 | Verificación pública | `GET /api/publico/estado` | Ninguno | `200 OK` |
| 2 | Intento a recurso protegido sin token | `GET /api/productos` | Ninguno | `401 Unauthorized` |
| 3 | Login con credenciales inválidas | `POST /api/auth/login` | `{"username":"admin","password":"err"}` | `401 Unauthorized` |
| 4 | Login exitoso con usuario regular | `POST /api/auth/login` | `{"username":"usuario","password":"user123"}` | `200 OK` + JSON con token JWT |
| 5 | Acceso con token a perfil propio | `GET /api/auth/me` | `Authorization: Bearer <TOKEN_USER>` | `200 OK` con datos de `usuario` |
| 6 | Acceso con token USER a productos | `GET /api/productos` | `Authorization: Bearer <TOKEN_USER>` | `200 OK` con lista de productos |
| 7 | Intento de alta con token USER | `POST /api/productos` | `Authorization: Bearer <TOKEN_USER>` | `403 Forbidden` |
| 8 | Login exitoso como administrador | `POST /api/auth/login` | `{"username":"admin","password":"admin123"}` | `200 OK` + JSON con token JWT |
| 9 | Acceso a reporte admin con token ADMIN | `GET /api/admin/reporte` | `Authorization: Bearer <TOKEN_ADMIN>` | `200 OK` |
| 10 | Creación de producto con token ADMIN | `POST /api/productos` | `Authorization: Bearer <TOKEN_ADMIN>` + JSON Producto | `201 Created` |
| 11 | Petición con token alterado o inválido | `GET /api/productos` | `Authorization: Bearer token_falso` | `401 Unauthorized` |

---

## 🚀 6. Extensión Opcional (Sección 14 de la Guía)

### Expiración y Refresco de Tokens (*Refresh Tokens*)
Para entornos de producción reales, un tiempo de vida (TTL) corto (por ejemplo 15 a 30 minutos) mejora la seguridad en caso de interceptación del token de acceso (*Access Token*). Para no obligar al usuario a iniciar sesión repetidamente cuando el token expira, se implementa una arquitectura con **Refresh Tokens**:
1. **Access Token (corta duración):** Se almacena en memoria volátil de la aplicación cliente y se adjunta en cada petición HTTP en el encabezado `Authorization: Bearer <token>`.
2. **Refresh Token (larga duración, ej. 7 a 30 días):** Se almacena en la base de datos (con posibilidad de revocación inmediata) y se envía al cliente en una cookie con atributos `HttpOnly`, `Secure` y `SameSite=Strict` para prevenir robos mediante scripts maliciosos (XSS).
3. **Endpoint de rotación (`POST /api/auth/refresh`):** Cuando el Access Token expira y retorna `401`, el cliente envía el Refresh Token al endpoint de renovación para emitir un nuevo par de tokens sin requerir las credenciales del usuario nuevamente.

---

## 💡 7. Respuestas a las Preguntas de Reflexión (Sección 16 de la Guía)

### 1. ¿Qué ventajas ofrece JWT frente a la autenticación básica HTTP Basic?
- **No expone las credenciales del usuario en cada petición:** En HTTP Basic, el usuario y la contraseña codificados en Base64 viajan en la cabecera `Authorization` de todas y cada una de las solicitudes. Si una petición es interceptada, la contraseña queda comprometida. Con JWT, las credenciales viajan una sola vez durante el login; las peticiones subsecuentes solo transportan un token temporal de vida limitada.
- **Autosuficiencia (*Self-contained*):** El token transporta la identidad, tiempo de expiración y roles concedidos dentro de su payload, permitiendo que el servidor autorice peticiones de inmediato sin necesidad de consultar la base de datos en cada invocación.
- **Arquitectura Stateless y Escalabilidad Horizontal:** No requiere almacenar sesiones en memoria del servidor (`HttpSession`), permitiendo balancear la carga entre múltiples instancias de servidores API sin necesidad de sesiones fijas (*sticky sessions*) o almacenamiento distribuido de sesiones.

### 2. ¿Por qué una API stateless no debe mantener sesiones en memoria del servidor?
Porque el principio REST de "sin estado" (*Statelessness*) dicta que cada solicitud debe contener toda la información requerida para ser procesada y entendida de manera independiente. Si el servidor mantiene sesiones en memoria:
- Complica el escalado horizontal (si una petición cae en una réplica diferente a donde se inició sesión, fallaría).
- Consume memoria RAM proporcional al número de usuarios concurrentes.
- Aumenta el acoplamiento y la complejidad ante fallos y reinicios de instancias del backend.

### 3. ¿Qué implicancias de seguridad tiene la duración (TTL) de un token de acceso?
- **TTL muy prolongado (días o semanas):** Si el token es interceptado (por malware, logs no seguros o un ataque de red), el atacante puede hacerse pasar por el usuario legítimo durante todo ese periodo de tiempo, ya que un token JWT firmado es válido hasta su expiración a menos que se mantenga una lista negra (*blacklist*).
- **TTL corto (15 a 30 minutos):** Minimiza la ventana de exposición y riesgo ante robo o filtración. Para evitar degradar la experiencia de usuario, este enfoque se acompaña típicamente de un mecanismo de rotación con *Refresh Tokens*.

### 4. ¿Por qué no deben almacenarse contraseñas u otros datos sensibles dentro de los claims de un JWT?
Porque los claims del payload en un JWT estándar están únicamente codificados en Base64URL, **no están cifrados** (a menos que se use cifrado JWE). Cualquier persona o cliente que tenga acceso a la cadena del token puede decodificarlo de inmediato en texto plano leyendo su contenido en herramientas públicas como `jwt.io`. Por tanto, solo debe contener datos de contexto público o de autorización (ej. `sub`, `roles`, `exp`), nunca contraseñas, secretos, números de tarjetas de crédito ni datos personales confidenciales.

### 5. ¿Qué rol cumple la firma del token y qué sucedería si el secreto se ve comprometido?
- **Rol de la firma:** Garantiza la **integridad** y la **autenticidad** del token. Permite verificar que el token fue emitido por una entidad autorizada y que ninguna parte de su contenido (como el nombre de usuario o los roles asignados) fue alterada durante el tránsito.
- **Consecuencias si el secreto se ve comprometido:** Cualquier atacante con conocimiento del secreto puede forjar tokens JWT válidos a su antojo (*token forgery*), asignándose privilegios de `ROLE_ADMIN` o suplantando la identidad de cualquier usuario del sistema, comprometiendo de forma irreversible toda la seguridad de la API.
