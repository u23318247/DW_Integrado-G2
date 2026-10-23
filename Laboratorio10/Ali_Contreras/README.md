# API Tienda — Back-end seguro integrado (Laboratorio 10 / APF2)

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 10 · **Autor:** Ali Contreras

## 1. Objetivo

Demostrar que el back-end construido en la Unidad 2 funciona como **una sola solución integrada,
segura y consistente**: persistencia JPA/Hibernate en MySQL (semana 6), consultas JPQL y
transacciones (semana 7), usuarios y roles con BCrypt (semana 8) y autenticación JWT (semana 9).
Esta semana no agrega tecnología nueva: integra, estabiliza y deja evidencia para el APF2.

## 2. Arquitectura

```
Cliente / Postman
   │  Authorization: Bearer <JWT>
   ▼
SecurityFilterChain + JwtDecoder         ← firma HS256, exp e iss; roles → ROLE_USER / ROLE_ADMIN
   │  401 si el token falta o es inválido · 403 si el rol no alcanza
   ▼
@RestController                          ← solo HTTP: parámetros, códigos de estado
   ▼
@Service  (@Transactional)               ← reglas de negocio y frontera transaccional
   ▼
JpaRepository  (derivadas + JPQL)        ← acceso a datos
   ▼
Hibernate / JPA  →  MySQL (tienda_db)
```

| Capa | Clases principales |
|------|--------------------|
| Controller | `ProductoController`, `InventarioController`, `AdminController`, `AuthController`, `PublicoController` |
| Service | `ProductoService` (CRUD, JPQL, salidas/entradas transaccionales), `AdminResumenService`, `ReporteService` |
| Repository | `ProductoRepository` (JPQL + derivadas), `MovimientoStockRepository`, `UsuarioRepository`, `RolRepository` |
| Seguridad | `SecurityConfig`, `JwtConfig`, `TokenService`, `CustomUserDetailsService` |
| Errores | `ApiExceptionHandler` → 400 / 404 / 409 / 500 con JSON uniforme |

**Decisiones del equipo:**
- La transacción vive en el **Service**, no en el Controller: ahí se coordinan producto y movimiento.
- La salida de inventario de la semana 10 **reutiliza** `ProductoService.registrarSalida` de la semana 7
  (la guía pide no duplicar la lógica); solo se agregó el endpoint `POST /api/inventario/{id}/salida`.
- **Stock insuficiente → 409 Conflict** (`StockInsuficienteException`): la petición es válida, pero
  choca con el estado actual del recurso. Hereda de `ReglaNegocioException`, así que sigue siendo una
  `RuntimeException` y provoca rollback.
- Secretos fuera de Git: contraseña de MySQL y clave JWT se leen de variables de entorno.

## 3. Requisitos

- Java 21 o superior (el `pom.xml` compila con `java.version=21`; funciona con JDK 21, 22 o 25).
- Maven 3.9+.
- MySQL 8 en `localhost:3306` (la base `tienda_db` se crea sola si no existe).

> **Versiones:** la guía usa Spring Boot 4.1.1 / Spring Security 7.1.1. Este proyecto mantiene
> **Spring Boot 3.5.5** (Spring Security 6.5), igual que los laboratorios 5–9 del equipo. Las
> diferencias son solo de nombres de paquete en las pruebas (`@WebMvcTest`) y del starter
> (`spring-boot-starter-oauth2-resource-server`); el comportamiento es el mismo.

## 4. Variables de entorno

| Variable | Uso | Ejemplo |
|----------|-----|---------|
| `DB_PASSWORD` | Contraseña de MySQL (usuario `root`) | la de tu instalación local |
| `JWT_SECRET_BASE64` | Clave HS256 de al menos 32 bytes en Base64 | se genera aleatoriamente (ver abajo) |

Sin ellas la aplicación **no arranca** (`Could not resolve placeholder ...`), lo cual es intencional.

## 5. Base de datos

`tienda_db` con las tablas que genera Hibernate (`ddl-auto=update`): `productos`,
`movimientos_stock`, `usuarios`, `roles`, `usuarios_roles`. Al iniciar, `DatosSeguridadIniciales`
crea los roles y los usuarios de prueba si no existen. Datos de productos de ejemplo:

```sql
INSERT INTO productos (nombre, categoria, precio, stock) VALUES
('Laptop Lenovo ThinkPad', 'Tecnologia', 4200.00, 8),
('Mouse Logitech MX', 'Tecnologia', 320.00, 20),
('Silla ergonómica', 'Muebles', 850.00, 6),
('Escritorio ejecutivo', 'Muebles', 1200.00, 4),
('Monitor 27 pulgadas', 'Tecnologia', 1450.00, 10);
```

## 6. Ejecución

**Windows PowerShell**
```powershell
$env:DB_PASSWORD = "tu_password_mysql"
$bytes = New-Object byte[] 32
[System.Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
$env:JWT_SECRET_BASE64 = [Convert]::ToBase64String($bytes)
mvn spring-boot:run
```

**Windows CMD**
```cmd
set DB_PASSWORD=tu_password_mysql
for /f %i in ('powershell -NoProfile -Command "$b=New-Object byte[] 32;[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b);[Convert]::ToBase64String($b)"') do set JWT_SECRET_BASE64=%i
mvn spring-boot:run
```

**Empaquetado y ejecución del JAR** (en la misma terminal con las variables):
```bash
mvn clean package
java -jar target/tienda-0.0.1-SNAPSHOT.jar
```

## 7. Endpoints

| Operación | Método y ruta | Acceso |
|-----------|---------------|--------|
| Iniciar sesión | `POST /api/auth/login` | Público |
| Consultar perfil | `GET /api/auth/me` | JWT válido |
| Estado de la API | `GET /api/publico/estado` | Público |
| Listar productos | `GET /api/productos` | USER o ADMIN |
| Buscar por rango (**JPQL**) | `GET /api/productos/rango?min=&max=` | USER o ADMIN |
| Buscar por nombre / categoría / stock bajo | `GET /api/productos/buscar?texto=`, `/categoria/{c}`, `/stock-bajo?limite=` | USER o ADMIN |
| Crear / actualizar / eliminar producto | `POST /api/productos`, `PUT` y `DELETE /api/productos/{id}` | ADMIN |
| **Registrar salida (transaccional)** | `POST /api/inventario/{id}/salida?cantidad=` | ADMIN |
| Ajuste de inventario | `POST /api/inventario/ajustes` | ADMIN |
| Consultar stock bajo | `GET /api/inventario/stock-bajo` | USER o ADMIN |
| **Resumen administrativo** | `GET /api/admin/resumen` | ADMIN |
| Reporte administrativo | `GET /api/admin/reporte` | ADMIN |

Respuesta de `GET /api/admin/resumen`:
```json
{"totalProductos":6,"productosConStockBajo":1,"totalMovimientos":4,"estado":"BACKEND_OPERATIVO"}
```

## 8. Usuarios y roles de prueba

| Usuario | Roles | Contraseña |
|---------|-------|------------|
| `usuario` | ROLE_USER | credencial didáctica de laboratorio (ver `DatosSeguridadIniciales`) |
| `admin` | ROLE_ADMIN, ROLE_USER | credencial didáctica de laboratorio (ver `DatosSeguridadIniciales`) |

Las contraseñas se guardan solo como hash **BCrypt** (`$2a$10$...`) y nunca viajan dentro del JWT.

## 9. Pruebas

```bash
mvn clean test      # 60 pruebas, 0 fallos (H2 en memoria, no requiere MySQL)
mvn clean package   # genera target/tienda-0.0.1-SNAPSHOT.jar
```

| Clase | Pruebas | Qué verifica |
|-------|---------|--------------|
| `IntegracionApf2Test` (nueva) | 14 | **Matriz completa 1–12** end-to-end con login real, más cantidad 0 → 400 y salida con USER → 403 |
| `AdminControllerSecurityTest` (nueva, de la guía) | 3 | `/api/admin/resumen` con `jwt()` simulado: ADMIN 200, USER 403, sin token 401 |
| `JwtIntegracionTest` | 18 | Login, token alterado/vencido/otro emisor, `/me`, reporte, BCrypt (semana 9) |
| `ProductoControllerSecurityTest` | 14 | Reglas por rol con `@WebMvcTest` (semanas 8–9) |
| `ProductoServiceTransaccionTest` | 6 | Commit y rollback reales en el Service (semana 7) |
| `ProductoRepositoryTest` | 4 | Consultas derivadas y JPQL (semana 7) |
| `TokenServiceTest` | 1 | Emisión y decodificación del JWT (semana 9) |

Línea base antes de modificar el proyecto (paso 1.4): 43/43 pruebas aprobadas.

## 10. Evidencias de transacción y seguridad

Ejecución del **JAR empaquetado** contra MySQL real, con secretos por variables de entorno.

### Matriz de pruebas de integración (sección 13)

| N.° | Escenario | Credencial | Esperado | Obtenido |
|-----|-----------|------------|----------|----------|
| 1 | Login USER válido | usuario | 200 + token | **200** + `accessToken` |
| 2 | GET /api/productos | ninguna | 401 | **401** |
| 3 | GET /api/productos | ROLE_USER | 200 | **200** |
| 4 | POST /api/productos | ROLE_USER | 403 | **403** |
| 5 | POST /api/productos | ROLE_ADMIN | 200/201 | **201** |
| 6 | GET /api/productos/rango?min=100&max=1000 (JPQL) | ROLE_USER | 200 + filtrado | **200**, solo productos entre 100 y 1000 ordenados por precio |
| 7 | POST /api/inventario/1/salida?cantidad=2 | ROLE_ADMIN | 200 + stock baja | **200**, stock 11 → 9 |
| 8 | POST /api/inventario/1/salida?cantidad=99999 | ROLE_ADMIN | 409 + rollback | **409** `"Stock insuficiente. Disponible: 9, solicitado: 99999"` |
| 9 | GET /api/admin/resumen | ROLE_USER | 403 | **403** |
| 10 | GET /api/admin/resumen | ROLE_ADMIN | 200 | **200** `BACKEND_OPERATIVO` |
| 11 | Token con firma alterada | — | 401 | **401** |
| 12 | Salida sobre producto 999 | ROLE_ADMIN | 404 | **404** `"Producto no encontrado: 999"` |

### Evidencia decisiva del commit y del rollback (producto id = 1)

```sql
SELECT stock FROM productos WHERE id = 1;
SELECT COUNT(*) FROM movimientos_stock WHERE producto_id = 1;
```

| Momento | stock | movimientos | Resultado |
|---------|-------|-------------|-----------|
| Antes de la salida | 11 | 2 | — |
| Tras `salida?cantidad=2` (200) | **9** | **3** | **COMMIT**: stock y movimiento confirmados juntos |
| Tras `salida?cantidad=99999` (409) | **9** | **3** | **ROLLBACK**: sin ningún cambio parcial |

### JPQL traducida por Hibernate

```java
@Query("SELECT p FROM Producto p WHERE p.precio BETWEEN :min AND :max ORDER BY p.precio ASC")
```
```sql
select p1_0.id, p1_0.categoria, p1_0.nombre, p1_0.precio, p1_0.stock
from productos p1_0
where p1_0.precio between ? and ?
order by p1_0.precio
```

### Quality Gate (sección 15)

| Control | Resultado |
|---------|-----------|
| Compilación — `mvn clean test` | BUILD SUCCESS, 60/60 |
| Empaquetado — `mvn clean package` | `target/tienda-0.0.1-SNAPSHOT.jar` |
| Persistencia | `tienda_db`: productos, usuarios (2), roles (2) y movimientos coherentes |
| JPQL | Rango filtrado y ordenado correctamente |
| Transacción | Commit en éxito; rollback en error (tabla anterior) |
| Autenticación | Token solo con credenciales válidas |
| Autorización | Matriz 401 / 403 / 200 correcta |
| Secretos | `application.properties` usa `${DB_PASSWORD}` y `${JWT_SECRET_BASE64}`; nada sensible en Git |

## Preguntas de reflexión

1. **¿Por qué `@Transactional` en una operación que coordina varios cambios?** Porque descontar stock
   e insertar el movimiento forman una sola unidad de negocio: o se confirman ambos o ninguno. Sin la
   frontera transaccional, un fallo intermedio dejaría datos inconsistentes.
2. **JPQL vs. SQL nativo:** JPQL consulta entidades y atributos Java (`Producto`, `p.precio`) y
   Hibernate la traduce al SQL del motor; es portable y se valida al iniciar. El SQL nativo usa
   tablas y columnas reales y queda atado a un motor concreto.
3. **¿Por qué un JWT válido puede terminar en 403?** Porque la firma y la vigencia solo prueban la
   identidad (autenticación). Si sus roles no cumplen la regla del recurso (autorización), la
   respuesta es 403, como USER pidiendo `/api/admin/resumen`.
4. **¿Qué valida Spring Security antes del Controller?** Que exista el Bearer Token, su firma HS256,
   que no esté vencido (`exp`), el emisor (`iss`), y luego que las autoridades del claim `roles`
   cumplan la regla de la ruta y método.
5. **¿Qué pasaría sin transacción si el insert del movimiento falla?** El stock quedaría descontado
   sin un movimiento que lo justifique: inventario inconsistente e imposible de auditar.
6. **¿Por qué la contraseña no va en el JWT?** El payload solo está codificado en Base64URL, no
   cifrado: cualquiera que tenga el token puede leerlo.
7. **Ventaja de inyectar por constructor:** dependencias `final` e inmutables, explícitas, y fáciles
   de reemplazar por mocks en las pruebas.
8. **¿Qué evidencia que el back-end está listo para Angular?** Contratos HTTP coherentes y
   documentados, login JWT funcional, reglas por rol verificadas (401/403), errores con JSON
   uniforme, transacciones probadas y build reproducible (60 pruebas + JAR).
