# INSTRUCCIONES TÉCNICAS DE IMPLEMENTACIÓN PARA AGENTE IA

## Módulo: Autenticación y Autorización con JWT en Spring Boot (API REST Stateless)

---

### 1. Rol y Contexto Operativo
Actúa como un **Arquitecto de Software Backend y Especialista en Seguridad Spring**. Tu objetivo es migrar la arquitectura de seguridad implementada en la Semana 8 (HTTP Basic) a una arquitectura **stateless basada en JSON Web Tokens (JWT)** para la API REST de tienda/productos, siguiendo las directrices del Laboratorio 09.

* **Objetivo de Negocio:** Permitir a los clientes autenticarse una única vez mediante `POST /api/auth/login`, obtener un token firmado digitalmente con algoritmo simétrico **HMAC-SHA256 (HS256)** y utilizar dicho token en la cabecera `Authorization: Bearer <token>` para consumir endpoints protegidos según su rol.
* **Premisa de Continuidad:** Conservar intacta la base de datos MySQL (`tienda_db`), las entidades JPA (`Usuario`, `Rol`), el repositorio `UsuarioRepository` y el cifrado con `BCryptPasswordEncoder` construidos previamente. No reescribir la lógica de dominio de productos.

---

### 2. Stack Tecnológico Requerido
* **Lenguaje:** Java 17 o superior (compatible con Java 21/25).
* **Framework base:** Spring Boot 3.x / 4.x.
* **Seguridad & OAuth2:** `spring-boot-starter-security`, `spring-boot-starter-security-oauth2-resource-server` (Nimbus JOSE JWT).
* **Validación:** `spring-boot-starter-validation` (Jakarta Validation).
* **Persistencia:** Spring Data JPA / Hibernate con MySQL Driver.
* **Testing:** JUnit 5, MockMvc, `spring-security-test`.

---

### 3. Matriz de Autorización y Flujo de Seguridad

#### Flujo de Ejecución:
1. Cliente envía credenciales a `POST /api/auth/login`.
2. `AuthenticationManager` valida credenciales contra `CustomUserDetailsService` (MySQL) usando hash BCrypt.
3. `TokenService` emite un JWT firmado que encapsula: `sub` (username), `iss` (tienda-api), `iat`, `exp` (TTL) y el claim `roles` (`ROLE_USER`, `ROLE_ADMIN`).
4. Peticiones subsiguientes envían `Authorization: Bearer <token>`.
5. Spring Security OAuth2 Resource Server intercepta la petición, verifica la firma criptográfica y vigencia, y transforma el claim `roles` en autoridades de Spring Security.
6. `SecurityFilterChain` valida el acceso por ruta y verbo HTTP.

#### Matriz de Control de Acceso:
| Endpoint / Patrón | Método HTTP | Nivel de Acceso Requerido | Estado HTTP Esperado |
| :--- | :--- | :--- | :--- |
| `/api/publico/**` | `GET` | Público (`permitAll`) | 200 OK |
| `/api/auth/login` | `POST` | Público (`permitAll`) | 200 OK (JWT) / 401 Unauthorized |
| `/api/auth/me` | `GET` | Autenticado (`authenticated`) | 200 OK |
| `/api/productos/**` | `GET` | Roles `USER` o `ADMIN` | 200 OK / 401 si no hay token |
| `/api/productos/**` | `POST`, `PUT`, `PATCH`, `DELETE` | Exclusivo `ADMIN` | 200 o 201 si ADMIN / 403 si USER |
| `/api/admin/reporte` *(Reto)* | `GET` | Exclusivo `ADMIN` | 200 OK si ADMIN / 403 si USER |
| Cualquier otro recurso | Cualquier método | Autenticado (`anyRequest().authenticated()`) | 401 si no autentica |

---

### 4. Estructura de Paquetes y Archivos

Debes asegurar o generar los siguientes archivos dentro de `com.utp.tienda`:

```text
src/main/java/com/utp/tienda/
├── config/
│   ├── JwtConfig.java                     <-- NUEVO: Configuración de claves, encoder y decoder
│   ├── SecurityConfig.java                <-- MODIFICADO: Adaptado a OAuth2 Resource Server
│   └── DatosSeguridadIniciales.java       <-- PRESERVADO de semana 8
├── controller/
│   ├── AuthController.java                <-- NUEVO: Endpoints /api/auth/login y /api/auth/me
│   ├── AdminReporteController.java        <-- NUEVO: Reto de consolidación
│   ├── ProductoController.java            <-- PRESERVADO de semana 7/8
│   └── PublicoController.java             <-- PRESERVADO de semana 8
├── dto/
│   ├── LoginRequest.java                  <-- NUEVO: Record de entrada para login
│   └── TokenResponse.java                 <-- NUEVO: Record de respuesta con token
├── model/
│   ├── Producto.java                      <-- PRESERVADO
│   ├── Usuario.java                       <-- PRESERVADO
│   └── Rol.java                           <-- PRESERVADO
├── repository/
│   ├── ProductoRepository.java            <-- PRESERVADO
│   ├── UsuarioRepository.java             <-- PRESERVADO
│   └── RolRepository.java                 <-- PRESERVADO
└── security/
    ├── CustomUserDetailsService.java      <-- PRESERVADO
    └── TokenService.java                  <-- NUEVO: Generador de JWT con claims

src/test/java/com/utp/tienda/
└── security/
    └── TokenServiceTest.java              <-- NUEVO: Test unitario de firma y decodificación
```

---

### 5. Guía de Tareas Paso a Paso

#### Tarea 1: Actualización de Dependencias (`pom.xml`)
Incorpora al `pom.xml` las siguientes dependencias manteniendo las existentes de JPA, MySQL, Web y Security:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security-oauth2-resource-server</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

#### Tarea 2: Propiedades y Clave Criptográfica
1. **Archivo `src/main/resources/application.properties`**:
   Configura las siguientes propiedades (conservando la configuración de la BD):
   ```properties
   app.jwt.issuer=tienda-api
   app.jwt.ttl-minutes=30
   app.jwt.secret-base64=${JWT_SECRET_BASE64}
   ```
2. **Requisito Criptográfico:** La variable de entorno `JWT_SECRET_BASE64` debe suministrar una clave codificada en Base64 de al menos 32 bytes (256 bits) para HMAC-SHA256.

#### Tarea 3: DTOs para Autenticación (`com.utp.tienda.dto`)
Crea contratos inmutables utilizando Java Records:

1. **`LoginRequest.java`**:
   ```java
   package com.utp.tienda.dto;

   import jakarta.validation.constraints.NotBlank;

   public record LoginRequest(
       @NotBlank(message = "El nombre de usuario es obligatorio") String username,
       @NotBlank(message = "La contraseña es obligatoria") String password
   ) {}
   ```

2. **`TokenResponse.java`**:
   ```java
   package com.utp.tienda.dto;

   public record TokenResponse(
       String tokenType,
       String accessToken,
       Long expiresInSeconds
   ) {}
   ```

#### Tarea 4: Configuración Criptográfica JWT (`JwtConfig.java`)
Crea la clase `src/main/java/com/utp/tienda/config/JwtConfig.java`:
* Inyecta `${app.jwt.secret-base64}` y decodifica la clave. Lanza `IllegalArgumentException` si la longitud en bytes es menor a 32.
* Define el Bean `SecretKey` con algoritmo `HmacSHA256`.
* Define el Bean `JwtEncoder` usando `NimbusJwtEncoder` con un `ImmutableSecret`.
* Define el Bean `JwtDecoder` usando `NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build()`.
* Añade un validador al decoder: `decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));`.

#### Tarea 5: Servicio de Tokens (`TokenService.java`)
Crea la clase `src/main/java/com/utp/tienda/security/TokenService.java`:
* Inyecta `JwtEncoder`, `${app.jwt.issuer}` y `${app.jwt.ttl-minutes:30}`.
* Método `long expiresInSeconds()`: Retorna `ttlMinutes * 60`.
* Método `String crearToken(Authentication autenticacion)`:
  * Extrae los roles de `autenticacion.getAuthorities()` filtrando por aquellos que comiencen con `ROLE_`.
  * Construye un `JwtClaimsSet`:
    * `issuer`: valor configurado.
    * `subject`: `autenticacion.getName()`.
    * `issuedAt`: instante actual (`Instant.now()`).
    * `expiresAt`: instante actual más `ttlMinutes` minutos.
    * `claim("roles", roles)`: lista de roles.
  * Construye un `JwsHeader` especificando `MacAlgorithm.HS256`.
  * Codifica y retorna el token firmado en formato String: `encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue()`.

#### Tarea 6: Refactorización de Seguridad (`SecurityConfig.java`)
Actualiza completamente `src/main/java/com/utp/tienda/config/SecurityConfig.java`:
* Mantén el Bean `PasswordEncoder` (`BCryptPasswordEncoder`).
* Define el Bean `AuthenticationManager`:
  * Configura un `DaoAuthenticationProvider(userDetailsService)` y asígnale el `PasswordEncoder`.
  * Retorna una instancia de `ProviderManager(provider)`.
* Define el Bean `JwtAuthenticationConverter`:
  * Instancia `JwtGrantedAuthoritiesConverter`.
  * Asigna `authorities.setAuthoritiesClaimName("roles")`.
  * Asigna `authorities.setAuthorityPrefix("")` (dado que los roles ya están almacenados como `ROLE_USER` y `ROLE_ADMIN`).
  * Asigna este convertidor al `JwtAuthenticationConverter` y retórnalo.
* Configura el Bean `SecurityFilterChain`:
  * Deshabilita CSRF: `.csrf(AbstractHttpConfigurer::disable)`.
  * Deshabilita HTTP Basic y Form Login: `.httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable)`.
  * Define la política de sesiones como `SessionCreationPolicy.STATELESS`.
  * Reglas de autorización en `authorizeHttpRequests`:
    * `POST /api/auth/login` -> `permitAll()`
    * `/api/publico/**` -> `permitAll()`
    * `GET /api/productos/**` -> `hasAnyRole("USER", "ADMIN")`
    * `POST, PUT, PATCH, DELETE /api/productos/**` -> `hasRole("ADMIN")`
    * `GET /api/admin/**` -> `hasRole("ADMIN")`
    * `anyRequest().authenticated()`
  * Configura Resource Server:
    ```java
    .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> 
        jwt.jwtAuthenticationConverter(converter)
    ))
    ```

#### Tarea 7: Controlador de Autenticación (`AuthController.java`)
Crea `src/main/java/com/utp/tienda/controller/AuthController.java`:
* Ruta base: `/api/auth`.
* Inyecta `AuthenticationManager` y `TokenService`.
* Endpoint `POST /login`:
  * Recibe `@Valid @RequestBody LoginRequest request`.
  * Invoca `manager.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()))`.
  * Si la autenticación es exitosa, genera el token con `tokens.crearToken(auth)` y responde `200 OK` con un `TokenResponse("Bearer", token, tokens.expiresInSeconds())`.
  * Si captura `AuthenticationException`, responde `401 Unauthorized` con un cuerpo JSON de error: `{"error": "Credenciales inválidas"}`.
* Endpoint `GET /me`:
  * Recibe el usuario autenticado inyectando `JwtAuthenticationToken auth`.
  * Extrae los roles con prefijo `ROLE_` de sus autoridades.
  * Responde un mapa con: `username` y lista de `roles`.

#### Tarea 8: Reto de Aplicación (`AdminReporteController.java`)
Crea `src/main/java/com/utp/tienda/controller/AdminReporteController.java`:
* Ruta base: `/api/admin`.
* Inyecta `ProductoRepository` (o el servicio correspondiente).
* Endpoint `GET /reporte`:
  * Responde un payload con la fecha actual del servidor (`LocalDate.now()`) y el total de productos registrados (`productoRepository.count()`).
  * Queda protegido automáticamente por la regla `GET /api/admin/**` que exige `ROLE_ADMIN`.

#### Tarea 9: Pruebas Automatizadas Unitarias (`TokenServiceTest.java`)
Crea la suite de pruebas unitarias en `src/test/java/com/utp/tienda/security/TokenServiceTest.java`:
* Genera una clave HMAC válida para la prueba (ej. clave de 32 bytes hardcodeada exclusivamente para el scope de test).
* Inicializa manualmente `NimbusJwtEncoder`, `NimbusJwtDecoder` y el `TokenService`.
* Construye un `Authentication` simulado con el usuario `"admin"` y la autoridad `"ROLE_ADMIN"`.
* Invoca `service.crearToken(auth)`.
* Decodifica el token resultante con `decoder.decode(token)` y comprueba mediante aserciones JUnit:
  1. `getSubject()` equivale a `"admin"`.
  2. `getIssuer().toString()` coincide con `"tienda-api"`.
  3. El claim `"roles"` contiene `"ROLE_ADMIN"`.
  4. `getExpiresAt()` no es nulo.

---

### 6. Criterios de Aceptación y Definición de Terminado (DoD)

La IA ejecutora debe confirmar el cumplimiento estricto de los siguientes puntos:

- [ ] **Sin Dependencias de Sesión:** Ninguna petición genera `JSESSIONID`; el contexto es estrictamente `STATELESS`.
- [ ] **HTTP Basic Desactivado:** Peticiones sin token a rutas protegidas no devuelven el reto `WWW-Authenticate: Basic`, sino `401 Unauthorized` bajo el esquema Bearer.
- [ ] **Emisión Exitosa de Token:** `POST /api/auth/login` con credenciales válidas (`usuario`/`Usuario123*` o `admin`/`Admin123*`) responde `200 OK` con un Bearer token.
- [ ] **Manejo de Credenciales Inválidas:** `POST /api/auth/login` con contraseña errónea responde `401 Unauthorized`.
- [ ] **Validación de Roles por Token:**
  - Token de `usuario` (`ROLE_USER`) permite `GET /api/productos` (200 OK), pero produce `403 Forbidden` en `POST /api/productos` y en `GET /api/admin/reporte`.
  - Token de `admin` (`ROLE_ADMIN`) permite tanto `GET` como `POST` en `/api/productos` y acceso exitoso a `GET /api/admin/reporte`.
- [ ] **Rechazo de Tokens Inválidos:** Un token modificado (firma alterada) o vencido es rechazado por el Resource Server con estado `401 Unauthorized`.
- [ ] **Suites de Pruebas Verificadas:** La prueba `TokenServiceTest` pasa exitosamente con `./mvnw test` o `mvn test`.