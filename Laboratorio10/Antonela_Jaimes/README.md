# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## FACULTAD DE INGENIERÍA DE SISTEMAS E INFORMÁTICA
### DESARROLLO WEB INTEGRADO — UNIDAD 2: SEMANA 10 / APF2

---

# GUÍA DE LABORATORIO 10
## Integración del Back-End Seguro con Spring Boot
**Estudiante:** Antonela Jaimes  
**Tema:** JPA/Hibernate + JPQL + Transacciones (@Transactional) + Spring Security + JWT + Entrega APF2  
**Tecnologías:** Java 17, Spring Boot 3.4.1, Spring Security 6.4, JJWT, MySQL 8 / H2 in-memory, Maven, Postman  

---

## 1. Propósito y Logro de Aprendizaje

Esta práctica culmina la **Unidad 2 del curso** y consolida el **Segundo Avance del Proyecto Final (APF2)**.
El objetivo es demostrar que todos los componentes desarrollados a lo largo de las semanas 6 a 9 funcionan como una **solución integral, robusta, persistente, transaccional y asegurada**:
- **Semana 6:** Persistencia JPA / Hibernate y operaciones CRUD.
- **Semana 7:** Consultas JPQL personalizadas y control transaccional (`@Transactional`).
- **Semana 8:** Control de acceso basado en roles (RBAC: `ROLE_USER`, `ROLE_ADMIN`).
- **Semana 9:** Autenticación sin estado (*stateless*) mediante tokens **JWT (JSON Web Tokens)**.
- **Semana 10:** Integración end-to-end, estabilización del backend, verificación de atomicidad (commit/rollback) y cierre de Unidad 2 antes de la capa frontend (Angular).

---

## 2. Arquitectura del Back-End Integrado

El flujo de una solicitud cliente se procesa a través de una arquitectura limpia en capas desacopladas:

```text
Cliente / Postman
    │
    │  Authorization: Bearer <JWT>
    ▼
SecurityFilterChain (Spring Security)
    │  1. Extrae y valida firma/vigencia del JWT (JwtAuthenticationFilter)
    │  2. Carga CustomUserDetails y autoridades (ROLE_USER / ROLE_ADMIN)
    ▼
@RestController (Controladores REST)
    │  - Valida contratos HTTP, DTOs y parámetros de entrada (@Valid)
    ▼
@Service (Capa de Negocio y Transacciones)
    │  - Aplica reglas de negocio y consistencia atómica (@Transactional)
    ▼
JpaRepository (Capa de Persistencia)
    │  - Consultas derivadas y JPQL optimizadas (@Query)
    ▼
Hibernate / JPA
    │  - Mapeo objeto-relacional y Dirty Checking
    ▼
MySQL / Base de Datos
```

---

## 3. Estructura de Directorios del Proyecto

```text
Laboratorio10/Antonela_Jaimes/
├── pom.xml                                   # Starters de Spring Boot, Security, JPA, JJWT y H2
├── mvnw / mvnw.cmd                           # Maven Wrapper portable
├── README.md                                 # Documentación técnica, matriz y preguntas de reflexión
├── src/
│   ├── main/
│   │   ├── java/com/utp/tienda/
│   │   │   ├── TiendaApplication.java        # Clase principal Spring Boot
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # Cadena de filtros y reglas de autorización HTTP
│   │   │   │   └── DatosSeguridadIniciales.java # Carga de datos base (roles, usuarios y productos)
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java       # POST /api/auth/login y GET /api/auth/me
│   │   │   │   ├── ProductoController.java   # CRUD de productos y búsqueda por rango JPQL
│   │   │   │   ├── InventarioController.java # Operación transaccional: POST /api/inventario/{id}/salida
│   │   │   │   ├── AdminController.java      # GET /api/admin/resumen (Dashboard administrativo)
│   │   │   │   └── PublicoController.java    # Endpoints de prueba pública
│   │   │   ├── dto/
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── TokenResponse.java
│   │   │   │   └── ResumenBackendResponse.java # Record DTO con métricas administrativas
│   │   │   ├── exception/
│   │   │   │   ├── ProductoNoEncontradoException.java # HTTP 404
│   │   │   │   └── StockInsuficienteException.java    # HTTP 409 Conflict
│   │   │   ├── model/
│   │   │   │   ├── Producto.java             # Entidad persistente de catálogo
│   │   │   │   ├── MovimientoStock.java      # Entidad de auditoría de salidas de inventario
│   │   │   │   ├── Usuario.java              # Entidad de usuario con credenciales BCrypt
│   │   │   │   └── Rol.java                  # Entidad de roles de seguridad
│   │   │   ├── repository/
│   │   │   │   ├── ProductoRepository.java   # JPQL buscarPorRangoPrecio y countByStockLessThan
│   │   │   │   ├── MovimientoStockRepository.java
│   │   │   │   ├── UsuarioRepository.java
│   │   │   │   └── RolRepository.java
│   │   │   ├── security/
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   ├── CustomUserDetailsService.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   └── TokenService.java         # Generación y validación de tokens JWT
│   │   │   └── service/
│   │   │       ├── ProductoService.java
│   │   │       ├── InventarioService.java    # Transacción atómica: descontar stock + registrar movimiento
│   │   │       └── AdminResumenService.java  # Resumen consolidado para administración
│   │   └── resources/
│   │       └── application.properties        # Configuración de base de datos y JWT
│   └── test/
│       ├── java/com/utp/tienda/
│       │   ├── controller/AdminControllerSecurityTest.java  # Pruebas MockMvc de roles (200 vs 403)
│       │   ├── service/InventarioTransaccionalTest.java    # Pruebas de commit y rollback transaccional
│       │   └── security/TokenServiceTest.java              # Pruebas de firma y expiración JWT
│       └── resources/
│           └── application-test.properties                 # Configuración H2 para pruebas automatizadas
```

---

## 4. Política de Seguridad y Endpoints REST

| Operación | Método y Ruta | Acceso Esperado | Descripción |
| :--- | :--- | :--- | :--- |
| **Iniciar Sesión** | `POST /api/auth/login` | **Público** | Valida credenciales con BCrypt y emite Token JWT. |
| **Perfil Usuario** | `GET /api/auth/me` | **Autenticado (JWT)** | Devuelve información y roles del usuario en sesión. |
| **Listar Productos** | `GET /api/productos` | `ROLE_USER` o `ROLE_ADMIN` | Obtiene el catálogo completo de productos. |
| **Buscar por Rango (JPQL)**| `GET /api/productos/rango?min=X&max=Y` | `ROLE_USER` o `ROLE_ADMIN` | Filtra por rango de precio ordenado ascendentemente. |
| **Crear Producto** | `POST /api/productos` | `ROLE_ADMIN` | Registra una nueva prenda/producto en base de datos. |
| **Actualizar Producto**| `PUT /api/productos/{id}` | `ROLE_ADMIN` | Modifica datos y stock de un producto existente. |
| **Eliminar Producto** | `DELETE /api/productos/{id}` | `ROLE_ADMIN` | Elimina producto por ID. |
| **Registrar Salida Stock**| `POST /api/inventario/{id}/salida?cantidad=N` | `ROLE_ADMIN` | Operación `@Transactional`: descuenta stock e inserta `MovimientoStock`. |
| **Resumen Administrativo**| `GET /api/admin/resumen` | `ROLE_ADMIN` | Retorna total de productos, stock crítico (<5) y total de movimientos. |

---

## 5. Demostración de Transaccionalidad y Rollback

El método `InventarioService.registrarSalida(Long id, int cantidad)` demuestra atomicidad empresarial:

```java
@Transactional
public Producto registrarSalida(Long productoId, int cantidad) {
    if (cantidad <= 0) {
        throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
    }
    Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ProductoNoEncontradoException(productoId));

    if (producto.getStock() < cantidad) {
        throw new StockInsuficienteException(
            "Stock actual: " + producto.getStock() + ", solicitado: " + cantidad);
    }

    // 1. Modificación de stock (Dirty Checking de Hibernate ejecuta UPDATE al hacer commit)
    producto.setStock(producto.getStock() - cantidad);

    // 2. Registro histórico del movimiento de inventario
    MovimientoStock movimiento = new MovimientoStock();
    movimiento.setProducto(producto);
    movimiento.setTipo("SALIDA");
    movimiento.setCantidad(cantidad);
    movimiento.setFecha(LocalDateTime.now());
    movimientoRepository.save(movimiento);

    return producto;
}
```

* **Escenario de Éxito (Commit):** Si el producto tiene stock suficiente, se descuenta la cantidad y se persiste el movimiento de salida simultáneamente.
* **Escenario de Error (Rollback):** Si se solicitan 99999 unidades, se dispara `StockInsuficienteException` (HTTP 409 Conflict). Spring intercepta la excepción en tiempo de ejecución (`RuntimeException`), revierte cualquier cambio pendiente y garantiza que **el stock original no se modifique** y **no quede ningún movimiento huérfano**.

---

## 6. Matriz de Pruebas de Integración (Postman / Automatizadas)

| N.° | Escenario | Credencial / Rol | Código HTTP | Resultado Esperado |
| :---: | :--- | :--- | :---: | :--- |
| **1** | Login USER válido | `usuario` / `user123` | **200 OK** | Retorna `accessToken` JWT válido. |
| **2** | Login ADMIN válido | `admin` / `admin123` | **200 OK** | Retorna `accessToken` JWT con rol ADMIN. |
| **3** | Consultar productos sin token | Ninguna | **401 / 403** | Rechazo por falta de credenciales. |
| **4** | Consultar productos con USER | `Bearer <TOKEN_USER>` | **200 OK** | Acceso concedido al catálogo. |
| **5** | Consulta JPQL por rango | `Bearer <TOKEN_USER>` | **200 OK** | Lista filtrada ordenada por precio. |
| **6** | USER intenta crear producto | `Bearer <TOKEN_USER>` | **403 Forbidden** | Rechazado por falta de autoridad ADMIN. |
| **7** | ADMIN crea producto | `Bearer <TOKEN_ADMIN>` | **201 Created** | Producto creado satisfactoriamente. |
| **8** | Salida de inventario válida | `Bearer <TOKEN_ADMIN>` | **200 OK** | Stock disminuye y movimiento registrado. |
| **9** | Salida con stock insuficiente | `Bearer <TOKEN_ADMIN>` | **409 Conflict** | Excepción de negocio y rollback verificado. |
| **10** | USER intenta resumen admin | `Bearer <TOKEN_USER>` | **403 Forbidden** | Endpoint administrativo inaccesible para USER. |
| **11** | ADMIN consulta resumen admin | `Bearer <TOKEN_ADMIN>` | **200 OK** | Métricas consolidadas del sistema. |
| **12** | Token adulterado o expirado | Firma inválida | **401 Unauthorized** | Token rechazado de inmediato. |

---

## 7. Respuestas a Preguntas de Reflexión (Sección 17 de la Guía)

### 1. ¿Por qué `@Transactional` debe colocarse en una operación de negocio que coordina varios cambios?
> **Respuesta:**  
> Porque garantiza las propiedades **ACID** (especialmente **Atomicidad** y **Consistencia**). Una operación de negocio que modifica dos o más tablas (como descontar stock en `productos` y registrar una fila en `movimientos_stock`) debe ejecutarse como una sola unidad indivisible. Si `@Transactional` no estuviera presente y el sistema fallara a mitad de camino, la base de datos quedaría en un estado corrupto e inconsistente.

### 2. ¿Qué diferencia práctica existe entre una consulta JPQL y una consulta SQL nativa?
> **Respuesta:**  
> * **JPQL (Java Persistence Query Language):** Opera sobre el modelo orientado a objetos (entidades Java como `Producto p` y sus atributos `p.precio`). Es agnóstica de la base de datos subyacente, lo que permite migrar entre MySQL, PostgreSQL u Oracle sin reescribir consultas.
> * **SQL Nativo:** Opera directamente sobre tablas y columnas físicas de la base de datos relacional (`SELECT * FROM productos WHERE precio > ...`), dependiendo de la sintaxis específica del motor de base de datos.

### 3. ¿Por qué un JWT válido puede terminar en 403?
> **Respuesta:**  
> Porque el proceso de seguridad consta de dos fases distintas: **Autenticación (401)** y **Autorización (403)**.  
> Un JWT válido demuestra con éxito la **identidad** del usuario (superó la autenticación). Sin embargo, si ese usuario posee el rol `ROLE_USER` e intenta acceder a una ruta protegida exclusivamente para administradores (`ROLE_ADMIN`), como `/api/admin/resumen`, Spring Security rechaza la petición con **403 Forbidden** (identidad válida, pero permisos insuficientes).

### 4. ¿Qué valida Spring Security antes de permitir que una solicitud llegue al Controller?
> **Respuesta:**  
> A través de la cadena `SecurityFilterChain`, Spring Security valida:
> 1. Que no sea una petición maliciosa (CORS / CSRF si aplica).
> 2. Si la ruta es pública (`permitAll()`).
> 3. Si la ruta requiere autenticación, intercepta el encabezado `Authorization: Bearer <token>`, valida la firma criptográfica, la vigencia temporal (expiración) y el emisor (`issuer`).
> 4. Si el token es legítimo, construye el objeto `Authentication` con los roles y autoridades del usuario en el `SecurityContext`.
> 5. Finalmente, verifica si las autoridades del usuario cumplen con las reglas de acceso declaradas (`hasRole`, `hasAnyRole`) antes de delegar la llamada al `@RestController`.

### 5. ¿Qué ocurriría si se descontara stock y el insert del movimiento fallara sin una transacción?
> **Respuesta:**  
> Ocurriría una **inconsistencia grave de datos**. El stock de la prenda se reduciría en la base de datos, pero no existiría ningún registro ni comprobante en la tabla de auditoría `movimientos_stock`. Contablemente se perdería el rastro de la mercadería, haciendo imposible conciliar inventarios físicos con registros del sistema.

### 6. ¿Por qué la contraseña no debe aparecer dentro del JWT?
> **Respuesta:**  
> Porque el cuerpo (*payload*) de un token JWT únicamente está codificado en **Base64Url**, **no está cifrado** (a menos que se use JWE). Cualquier intermediario, proxy o el propio cliente en el navegador puede decodificarlo instantáneamente con herramientas como `jwt.io`. Colocar contraseñas o datos altamente sensibles expondría las credenciales del usuario a ataques de interceptación (Man-in-the-Middle o lectura local).

### 7. ¿Qué ventaja aporta inyectar Repository/Service por constructor?
> **Respuesta:**  
> 1. **Inmutabilidad:** Permite declarar los atributos como `final`, garantizando que las dependencias no cambien tras la instanciación.
> 2. **Facilidad para pruebas unitarias:** Permite instanciar la clase directamente en tests unitarios (`new InventarioService(mockRepo, mockMovRepo)`) sin requerir levantar todo el contenedor pesado de Spring.
> 3. **Seguridad en tiempo de compilación:** Evita errores `NullPointerException` en tiempo de ejecución al obligar a suministrar todas las dependencias requeridas al crear el objeto.

### 8. ¿Qué evidencias permiten afirmar que el back-end está listo para pasar a la integración con Angular?
> **Respuesta:**  
> 1. **Contratos HTTP claros y estables:** Respuestas consistentes en JSON (códigos 200, 201, 401, 403, 404, 409).
> 2. **Autenticación desacoplada vía JWT:** Mecanismo *stateless* compatible con cabeceras HTTP `Authorization: Bearer <token>`, estándar en arquitecturas SPA (Single Page Application).
> 3. **Consistencia transaccional probada:** Operaciones de inventario y pedidos atómicas y resistentes a fallos.
> 4. **Pruebas automatizadas aprobadas (`BUILD SUCCESS`):** Cobertura de seguridad y lógica de negocio validada por Maven antes de conectar la capa frontend.

---

## 8. Guía de Ejecución del Proyecto

1. Abrir terminal en el directorio del laboratorio:
   ```bash
   cd "Laboratorio10/Antonela_Jaimes"
   ```
2. Ejecutar las pruebas automatizadas (utiliza base de datos en memoria H2):
   ```bash
   .\mvnw.cmd test
   ```
3. Ejecutar la aplicación Spring Boot:
   ```bash
   .\mvnw.cmd spring-boot:run
   ```
   *La API estará disponible en:* `http://localhost:8080/`

4. Empaquetar el artefacto JAR para la entrega APF2:
   ```bash
   .\mvnw.cmd clean package -DskipTests
   ```
   *Artefacto generado:* `target/tienda-0.0.1-SNAPSHOT.jar`
