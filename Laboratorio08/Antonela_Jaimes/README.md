# Laboratorio N.° 08: Seguridad con Spring Security, Roles y Permisos

- **Curso:** Desarrollo Web Integrado
- **Semana:** 8
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java, Spring Boot 3, Spring Security 6, Spring Data JPA, Hibernate, MySQL, BCrypt, MockMvc

---

## 📌 1. Descripción del Proyecto

En este laboratorio se incorpora una robusta capa de seguridad a la API REST desarrollada en las semanas 6 y 7 utilizando **Spring Security**.
Se implementa:
1. **Autenticación HTTP Basic** con usuarios y roles persistidos en base de datos MySQL (`usuarios`, `roles`, `usuarios_roles`).
2. **Cifrado seguro de contraseñas** mediante `BCryptPasswordEncoder` (nunca contraseñas en texto plano).
3. **Carga dinámica de credenciales y roles** mediante la implementación de `UserDetailsService` (`CustomUserDetailsService`).
4. **Autorización granular por método HTTP y rol** utilizando `SecurityFilterChain` con `authorizeHttpRequests`, distinguiendo entre acceso público, roles `ROLE_USER` y privilegios administrativos `ROLE_ADMIN`.
5. **Arquitectura sin estado (`SessionCreationPolicy.STATELESS`)** y CSRF deshabilitado para clientes API REST.
6. **Seguridad a nivel de método con `@PreAuthorize`** (Reto avanzado).
7. **Pruebas automatizadas de seguridad con `MockMvc` y `@WithMockUser`**, validando respuestas `401 Unauthorized`, `403 Forbidden` y `200 OK`.

---

## 📋 2. Política de Acceso y Endpoints

| Recurso | Método | Acceso Requerido | Código HTTP Esperado |
| :--- | :--- | :--- | :---: |
| `/api/publico/**` | GET | Público (sin autenticación) | `200 OK` |
| `/api/productos/**` | GET | `ROLE_USER` o `ROLE_ADMIN` | `200 OK` (401 si no autenticado) |
| `/api/productos/**` | POST | `ROLE_ADMIN` | `201 Created` (403 si es USER) |
| `/api/productos/**` | PUT / PATCH | `ROLE_ADMIN` | `200 OK` (403 si es USER) |
| `/api/productos/**` | DELETE | `ROLE_ADMIN` | `204 No Content` (403 si es USER) |
| `/api/inventario/**` | GET | `ROLE_USER` o `ROLE_ADMIN` | `200 OK` (401 si no autenticado) |
| `/api/inventario/ajustes` | POST | `ROLE_ADMIN` | `200 OK` (403 si es USER) |
| Cualquier otro endpoint | Cualquiera | Usuario autenticado | `401 Unauthorized` si anónimo |

---

## 👥 3. Usuarios de Prueba Precargados (BCrypt)

| Usuario | Contraseña | Roles Asignados |
| :--- | :--- | :--- |
| `usuario` | `Usuario123*` | `ROLE_USER` |
| `admin` | `Admin123*` | `ROLE_ADMIN`, `ROLE_USER` |

> Los usuarios y roles se inicializan automáticamente mediante `DatosSeguridadIniciales` implementando `CommandLineRunner`, guardando exclusivamente hashes BCrypt en la tabla `usuarios`.

---

## 🎯 4. Actividad de Consolidación (Sección 17) y Reto Avanzado (Sección 18)

1. **Módulo de Inventario (`InventarioController`):**
   - Acceso a `GET /api/inventario/**` para `ROLE_USER` y `ROLE_ADMIN`.
   - Modificación con `POST /api/inventario/ajustes` restringido exclusivamente a `ROLE_ADMIN`.
2. **Pruebas Automatizadas de Consolidación:**
   - En `InventarioControllerSecurityTest`, se comprueba el rechazo con `401 Unauthorized` para peticiones anónimas y `403 Forbidden` cuando un `ROLE_USER` intenta ajustar inventario.
3. **Reto Avanzado - Seguridad a nivel de método:**
   - Habilitado `@EnableMethodSecurity` en `SecurityConfig`.
   - Protección en la capa Service en `ProductoService.eliminar(Long id)` anotado con `@PreAuthorize("hasRole('ADMIN')")`.

---

## 💡 5. Respuestas a las Preguntas de Reflexión (Sección 20)

### 1. ¿Cuál es la diferencia entre autenticación y autorización?
- **Autenticación:** Responde a la pregunta *¿Quién eres?*. Es el proceso de verificar la identidad del cliente (validando credenciales, por ejemplo usuario y contraseña o un token).
- **Autorización:** Responde a la pregunta *¿Qué tienes permitido hacer?*. Ocurre después de una autenticación exitosa y consiste en evaluar si el usuario autenticado posee los roles o privilegios necesarios (`ROLE_USER`, `ROLE_ADMIN`) para acceder a un recurso o ejecutar una acción específica (ej. un `DELETE`).

### 2. ¿Por qué `hasRole("ADMIN")` busca una autoridad `ROLE_ADMIN`?
Por convención histórica y de diseño en Spring Security, el método `hasRole("X")` antepone automáticamente el prefijo `ROLE_` al evaluar las autoridades asignadas al usuario (`GrantedAuthority`). Por ende, internamente comprueba si el usuario cuenta con la autoridad concedida `ROLE_ADMIN`.

### 3. ¿Por qué una contraseña debe almacenarse con un `PasswordEncoder`?
Porque almacenar contraseñas en texto plano representa una vulnerabilidad de seguridad crítica en caso de fugas o accesos no autorizados a la base de datos. Un componente como `BCryptPasswordEncoder` aplica una función hash criptográfica unidireccional (*one-way*) que incorpora una sal aleatoria (*salt*) y un costo de cómputo ajustable (*work factor*), haciendo que sea inviable computacionalmente revertir el hash para obtener la contraseña original, incluso ante ataques por diccionario o tablas arcoíris (*rainbow tables*).

### 4. ¿Qué diferencia existe entre un 401 y un 403?
- **`401 Unauthorized`:** Falta autenticación o las credenciales suministradas son inválidas o erróneas. El cliente debe autenticarse para continuar.
- **`403 Forbidden`:** El cliente ya se encuentra correctamente autenticado y su identidad es conocida por el servidor, pero no posee los permisos o roles requeridos para acceder al recurso solicitado.

### 5. ¿Qué responsabilidad cumple `UserDetailsService`?
Actúa como una interfaz adaptadora y puente entre la fuente de datos de la aplicación (en este caso, las entidades JPA `Usuario` y `Rol` en MySQL) y el núcleo de seguridad de Spring Security. Su único método `loadUserByUsername(String username)` se encarga de buscar al usuario y transformarlo en un objeto estándar `UserDetails` con su nombre, contraseña cifrada y lista de autoridades.

### 6. ¿Por qué las reglas más específicas deben declararse antes que `anyRequest()`?
Porque Spring Security evalúa las reglas de `authorizeHttpRequests` de arriba hacia abajo secuencialmente y aplica la **primera regla coincidente** (*first-match wins*). Si se coloca `anyRequest().authenticated()` al inicio, todas las solicitudes coincidirían con esa regla y se ignorarían por completo las reglas más específicas como las excepciones públicas (`permitAll()`) o los filtros por rol.

### 7. ¿Por qué en una API REST stateless se puede tomar una decisión diferente sobre CSRF que en una aplicación web con sesión?
El ataque CSRF (*Cross-Site Request Forgery*) se aprovecha de que los navegadores web envían automáticamente las cookies de sesión (como `JSESSIONID`) en solicitudes entre sitios. En una API REST sin estado (*stateless*) que no utiliza cookies ni sesiones para la autenticación (sino cabeceras explícitas como `Authorization: Basic ...` o `Bearer JWT`), el navegador no adjunta credenciales de forma implícita o automática ante peticiones de sitios de terceros, eliminando el vector de ataque CSRF habitual y permitiendo desactivarlo con seguridad.

### 8. ¿Qué parte de esta solución podremos reutilizar cuando incorporemos JWT en la Semana 9?
Podremos reutilizar prácticamente toda la infraestructura construida:
- Las entidades persistentes `Usuario` y `Rol` en MySQL.
- Los repositorios `UsuarioRepository` y `RolRepository`.
- El servicio `CustomUserDetailsService` para la validación y carga del usuario.
- El bean `PasswordEncoder` (`BCryptPasswordEncoder`).
- Toda la política y reglas de autorización declaradas en `authorizeHttpRequests` (`hasRole`, `hasAnyRole`, `permitAll`).
Lo único que cambiará en la Semana 9 será el mecanismo de transporte de credenciales: se reemplazará `httpBasic()` por un filtro interceptor de JWT (`JwtAuthenticationFilter`) y un endpoint para generar y firmar tokens.
