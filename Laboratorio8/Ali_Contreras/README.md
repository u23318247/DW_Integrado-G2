# Ali_Contreras — Guía de Laboratorio 08

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 8
**Proyecto:** API Tienda — Seguridad con Spring Security, roles y permisos

Sobre la API de productos e inventario de las semanas 6 y 7 se agrega una capa de seguridad:
usuarios y roles persistidos en MySQL, contraseñas con **BCrypt**, autenticación **HTTP Basic**
y autorización por rol y método HTTP. En la semana 9 se reemplazará HTTP Basic por JWT
reutilizando los mismos usuarios y roles.

## Flujo de una petición

```
Cliente / Postman
   │
   ▼
SecurityFilterChain ── ¿credenciales válidas? ── no ──► 401 Unauthorized
   │  (UserDetailsService → MySQL, BCrypt)
   ├── ¿tiene el rol requerido? ──────────────── no ──► 403 Forbidden
   ▼
Controller → Service (@Transactional, @PreAuthorize) → Repository → MySQL
```

## Estructura (lo nuevo respecto a la semana 7)

```
src/main/java/com/utp/tienda/
├── config/
│   ├── SecurityConfig.java            # PasswordEncoder + SecurityFilterChain + @EnableMethodSecurity
│   └── DatosSeguridadIniciales.java   # crea roles y usuarios de prueba con BCrypt
├── model/
│   ├── Rol.java                       # tabla roles
│   └── Usuario.java                   # tabla usuarios + usuarios_roles (@ManyToMany)
├── repository/
│   ├── RolRepository.java
│   └── UsuarioRepository.java
├── security/
│   └── CustomUserDetailsService.java  # Usuario (MySQL) → UserDetails
└── controller/
    ├── PublicoController.java         # GET /api/publico/estado
    └── InventarioController.java      # actividad de consolidación
```

## Cómo ejecutar

Requisitos: Java 21+, Maven y MySQL en `localhost:3306` (usuario `root`, contraseña `root`;
cambiarla en `application.properties` si es distinta).

```bash
mvn spring-boot:run
```

Se usa la base `tienda_db` de la semana 7 (se crea sola si no existe). Al arrancar,
`DatosSeguridadIniciales` crea los roles y estos usuarios **solo si no existen**:

| Usuario | Contraseña | Roles |
|---------|------------|-------|
| `usuario` | `Usuario123*` | ROLE_USER |
| `admin` | `Admin123*` | ROLE_ADMIN, ROLE_USER |

> Credenciales didácticas, conocidas a propósito para el laboratorio. En un entorno real no se
> versionan: se usan variables de entorno o un gestor de secretos.

En Postman: pestaña **Authorization → Basic Auth** con el usuario y la contraseña.

## Política de acceso

| Recurso | Método | Acceso |
|---------|--------|--------|
| `/api/publico/**` | GET | Público |
| `/api/productos/**` | GET | ROLE_USER o ROLE_ADMIN |
| `/api/productos/**` | POST / PUT / PATCH / DELETE | ROLE_ADMIN |
| `/api/inventario/**` | GET | ROLE_USER o ROLE_ADMIN *(consolidación)* |
| `/api/inventario/ajustes` | POST | ROLE_ADMIN *(consolidación)* |
| Cualquier otro | Cualquiera | Usuario autenticado |

Las reglas se declaran de la más específica a la más general y terminan en
`anyRequest().authenticated()`. Las operaciones de inventario de la semana 7
(`POST /api/productos/{id}/salidas`, `/entradas`) quedan automáticamente reservadas a ADMIN
por ser `POST`.

## Evidencias en MySQL (paso 9)

```
mysql> SELECT id, username, activo, password FROM usuarios;
+----+----------+--------+--------------------------------------------------------------+
| id | username | activo | password                                                     |
+----+----------+--------+--------------------------------------------------------------+
|  1 | usuario  |      1 | $2a$10$3WfBXfVM.Xrj.sfuVNHdP.82y5elTLX0j2lmojGMAIVnjTqPUbxEy |
|  2 | admin    |      1 | $2a$10$EB6EBvW7ZjtmzYOcmGjo7u.wQhc6BBGgLmBSDRRmDEv1EOa7.MsVW |
+----+----------+--------+--------------------------------------------------------------+

mysql> SELECT * FROM roles;            mysql> (usuarios_roles con nombres)
+----+------------+                    +----------+------------+
| id | nombre     |                    | username | rol        |
+----+------------+                    +----------+------------+
|  1 | ROLE_USER  |                    | usuario  | ROLE_USER  |
|  2 | ROLE_ADMIN |                    | admin    | ROLE_USER  |
+----+------------+                    | admin    | ROLE_ADMIN |
                                       +----------+------------+
```

La columna `password` contiene solo hashes BCrypt (`$2a$10$...`), nunca la contraseña original.

## Pruebas de Postman (paso 10) — resultados obtenidos

| # | Credenciales | Solicitud | Esperado | Obtenido |
|---|--------------|-----------|----------|----------|
| 1 | Ninguna | GET /api/publico/estado | 200 | **200** `{"estado":"API disponible"}` |
| 2 | Ninguna | GET /api/productos | 401 | **401** |
| 3 | usuario / Usuario123* | GET /api/productos | 200 | **200** |
| 4 | usuario / Usuario123* | POST /api/productos | 403 | **403** |
| 5 | admin / Admin123* | POST /api/productos | 201 | **201** (id generado) |
| 6 | admin / Admin123* | DELETE /api/productos/{id} | 204 | **204** |
| 7 | admin / contraseña incorrecta | GET /api/productos | 401 | **401** |

Actividad de consolidación:

| Credenciales | Solicitud | Obtenido |
|--------------|-----------|----------|
| Ninguna | GET /api/inventario/stock-bajo | **401** |
| usuario | GET /api/inventario/stock-bajo | **200** |
| usuario | POST /api/inventario/ajustes | **403** |
| admin | POST /api/inventario/ajustes `{"productoId":2,"tipo":"ENTRADA","cantidad":3}` | **200**, stock 20 → 23 |
| Ninguna | GET /api/otro (no declarado) | **401** |
| usuario | GET /api/otro (no declarado) | **404** (autenticado; la ruta no existe) |

## Pruebas automatizadas

```bash
mvn test
```

**29 pruebas, 0 fallos** (corren sobre H2, sin necesitar MySQL):

- `ProductoControllerSecurityTest` (`@WebMvcTest` + `@WithMockUser`, 11): público 200,
  sin credenciales 401, USER lista 200, USER crea/elimina 403, ADMIN crea 201 y elimina 204, y
  los mismos escenarios 401/200/403/200 para `/api/inventario`.
- `SeguridadIntegracionTest` (`@SpringBootTest`, 8): usuarios **reales** con `httpBasic(...)`:
  hash BCrypt en la base, USER 200/403, ADMIN 201, contraseña incorrecta 401, usuario inexistente
  401, y el reto `@PreAuthorize` (USER bloqueado, ADMIN permitido).
- Las 10 pruebas de la semana 7 (JPQL y transacciones) siguen pasando.

> **Nota de versiones:** la guía usa Spring Boot 4.1 / Spring Security 7.1. Este proyecto usa
> **Spring Boot 3.5.5** (Spring Security 6.5) con Java 21, por eso `@WebMvcTest` se importa de
> `org.springframework.boot.test.autoconfigure.web.servlet` y la configuración de seguridad se
> carga en el slice con `@Import(SecurityConfig.class)`. Las reglas y el comportamiento son los
> mismos.

## Reto avanzado: seguridad a nivel de método

`SecurityConfig` tiene `@EnableMethodSecurity` y `ProductoService.eliminar` está anotado con
`@PreAuthorize("hasRole('ADMIN')")`. Es una segunda barrera: aunque alguien llamara al Service
desde otro punto que no pasara por la regla HTTP, un usuario sin ROLE_ADMIN recibiría
`AuthorizationDeniedException` y el producto no se eliminaría (comprobado en
`SeguridadIntegracionTest`). Las reglas por URL siguen en `SecurityFilterChain`; el refuerzo por
método se reserva para la operación crítica.

## Preguntas de reflexión

1. **¿Diferencia entre autenticación y autorización?** La autenticación responde *¿quién eres?*
   (validar usuario y contraseña). La autorización responde *¿qué puedes hacer?* (por ejemplo,
   permitir DELETE solo a ADMIN). Primero se autentica y luego se autoriza.

2. **¿Por qué `hasRole("ADMIN")` busca `ROLE_ADMIN`?** Por convención, Spring Security antepone el
   prefijo `ROLE_` a los roles para distinguirlos de otras autoridades. Por eso en la base de
   datos se guardan como `ROLE_USER` y `ROLE_ADMIN`.

3. **¿Por qué usar un `PasswordEncoder`?** Para no guardar contraseñas en texto plano ni de forma
   reversible. BCrypt aplica un hash con *salt* y factor de trabajo: se puede verificar una
   contraseña sin recuperarla, y si la base de datos se filtra, las contraseñas no quedan
   expuestas directamente.

4. **¿Diferencia entre 401 y 403?** 401: no hay una autenticación válida (sin credenciales o
   credenciales incorrectas). 403: el usuario sí está autenticado pero no tiene permiso para ese
   recurso.

5. **¿Qué responsabilidad cumple `UserDetailsService`?** Cargar el usuario por su *username*
   desde nuestra fuente de datos (MySQL) y entregarlo a Spring Security como `UserDetails`, con su
   hash y sus autoridades. Así Spring Security no depende de nuestra entidad `Usuario`.

6. **¿Por qué las reglas específicas van antes que `anyRequest()`?** Porque se evalúan en orden y
   gana la primera que coincide. Si `anyRequest().authenticated()` fuera primero, cualquier
   usuario autenticado podría hacer POST o DELETE y las reglas de ADMIN nunca se aplicarían.

7. **¿Por qué una API stateless puede decidir distinto sobre CSRF?** CSRF aprovecha que el
   navegador envía cookies de sesión automáticamente. Una API stateless sin cookies, que recibe
   las credenciales en el encabezado `Authorization` en cada petición, no tiene esa sesión que un
   sitio malicioso pueda reutilizar. En una aplicación web con sesión, CSRF debe mantenerse activo.

8. **¿Qué se reutilizará con JWT en la semana 9?** Las entidades `Usuario` y `Rol`, sus
   repositorios, `CustomUserDetailsService`, el `PasswordEncoder` BCrypt, los datos iniciales y
   las reglas de `authorizeHttpRequests`. Solo cambia el mecanismo de transporte de credenciales:
   en lugar de HTTP Basic, un endpoint de login emitirá un token y un filtro lo validará en cada
   petición.
