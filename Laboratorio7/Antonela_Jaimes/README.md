# Laboratorio N.° 07: JPQL y Transacciones con Spring Boot

- **Curso:** Desarrollo Web Integrado
- **Unidad:** Unidad 2 - Back-end con bases de datos
- **Semana:** 7
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java, Spring Boot 3, Spring Data JPA, Hibernate, MySQL, Maven

---

## 📌 1. Descripción del Proyecto

Este laboratorio amplía la API de productos implementada en la Semana 6, incorporando:
1. **Consultas personalizadas con JPQL y `@Query`**, aprovechando expresiones orientadas al modelo de objetos/entidades y parámetros nombrados (`@Param`).
2. **Consultas derivadas** mediante convenciones de nomenclatura de Spring Data JPA (`findByCategoriaIgnoreCase`).
3. **Gestión transaccional con `@Transactional`** en la capa de servicio (`ProductoService`), delimitando unidades de trabajo atómicas, asegurando consistencia y demostrando los mecanismos de **commit**, **rollback** y **dirty checking** de Hibernate al coordinar la actualización de `Producto` y el registro de `MovimientoStock`.
4. **Manejo global de excepciones** con `@RestControllerAdvice` retornando respuestas JSON consistentes con códigos de estado HTTP semánticos (`400 Bad Request`, `404 Not Found`, `500 Internal Server Error`).

---

## 📋 2. Endpoints Implementados

| Método | Endpoint | Parámetros / Body | Código HTTP | Descripción |
| :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/productos/buscar?texto={texto}` | Query param `texto` | `200 OK` / `400 Bad Request` | Búsqueda JPQL insensible a mayúsculas/minúsculas por nombre. Valida que no esté vacío. |
| **GET** | `/api/productos/categoria/{categoria}` | Path variable `categoria` | `200 OK` | Consulta derivada filtrando por categoría (ignorando mayúsculas/minúsculas). |
| **GET** | `/api/productos/precio?min={min}&max={max}` | Query params `min`, `max` | `200 OK` / `400 Bad Request` | Búsqueda JPQL por rango de precios (BETWEEN). Valida que min <= max. |
| **GET** | `/api/productos/stock-bajo?limite={limite}` | Query param `limite` (def: 5) | `200 OK` / `400 Bad Request` | Consulta JPQL para productos con stock menor o igual al límite especificado. |
| **POST** | `/api/productos/{id}/salidas?cantidad={n}` | Path variable `id`, param `cantidad` | `200 OK` / `400` / `404` | Descuenta el stock y registra el movimiento de tipo `SALIDA` en una sola transacción atómica. |
| **POST** | `/api/productos/{id}/entradas?cantidad={n}` | Path variable `id`, param `cantidad` | `200 OK` / `400` / `404` | Incrementa el stock y registra el movimiento de tipo `ENTRADA` en una sola transacción atómica. |
| **POST** | `/api/productos/{id}/salidas/simular-error?cantidad={n}` | Path variable `id`, param `cantidad` | `500 Internal Server Error` | Simula un fallo de negocio lanzando excepción para verificar el rollback total de la transacción. |

---

## 🎯 3. Actividades de Consolidación (Sección 17)

1. **Endpoint `GET /api/productos/stock-bajo?limite=5`**: Implementado con éxito usando `buscarConStockBajo(@Param("limite") Integer limite)` en `ProductoRepository` y expuesto en el controlador.
2. **Validación del texto de búsqueda**: Se valida en `ProductoService.buscarPorNombre` que el parámetro no sea nulo ni contenga únicamente espacios en blanco (`texto.trim().isEmpty()`), arrojando `ReglaNegocioException` (`400 Bad Request`).
3. **Operación transaccional `registrarEntrada(id, cantidad)`**: Incrementa el stock del producto mediante dirty checking y persiste el registro en `movimientos_stock` con tipo `ENTRADA`.
4. **Comprobación de Rollback y Commit:**
   - **Commit exitoso:** Al invocar `POST /api/productos/{id}/salidas?cantidad=2`, ambas operaciones (descuento de stock en la tabla `productos` e inserción en la tabla `movimientos_stock`) se confirman al culminar el método del servicio.
   - **Rollback verificado:** Al invocar `POST /api/productos/{id}/salidas/simular-error?cantidad=1`, se lanza una excepción de tipo `IllegalStateException` (subclase de `RuntimeException`), activando el rollback automático de Spring; ni el stock se decrementa ni se inserta ningún registro en la base de datos MySQL.

### 🔍 Reto Avanzado Opcional (Sección 17.1): Actualización Masiva con `@Modifying`
Para consultas masivas de actualización o borrado en JPQL se utiliza la combinación `@Modifying` y `@Query`:
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("UPDATE Producto p SET p.precio = p.precio * :factor WHERE p.categoria = :categoria")
int incrementarPreciosPorCategoria(@Param("categoria") String categoria, @Param("factor") BigDecimal factor);
```
**Cuidado con el contexto de persistencia:** Las sentencias `@Modifying` de tipo `UPDATE` o `DELETE` se ejecutan directamente sobre la base de datos omitiendo el ciclo de vida habitual de las entidades gestionadas por Hibernate. En consecuencia, las entidades que ya estuvieran cargadas en el contexto de persistencia de primer nivel (*first-level cache / EntityManager*) mantendrán en memoria los valores desactualizados. Por ello, se recomienda configurar `clearAutomatically = true` o invocar `entityManager.clear()` para limpiar el caché de primer nivel y forzar que las siguientes consultas recuperen el estado fresco y real de la base de datos.

---

## 💡 4. Respuestas a las Preguntas de Reflexión (Sección 18)

### 1. ¿Por qué JPQL utiliza el nombre de la entidad y no el nombre de la tabla?
Porque JPQL es un lenguaje orientado a objetos integrado con la capa de dominio de la aplicación. Trabaja con las clases Java mapeadas como entidades (`Producto`) y sus atributos (`p.precio`, `p.nombre`), desacoplándose de la estructura y nombres físicos de las tablas relacionales en la base de datos.

### 2. ¿Cuándo elegirías una consulta derivada y cuándo `@Query`?
* **Consulta derivada:** Para consultas sencillas cuyos criterios se puedan expresar de forma concisa y legible en el nombre del método (ej. `findByCategoriaIgnoreCase`, `findByActivoTrue`).
* **`@Query` (JPQL):** Cuando se requiere mayor expresividad y complejidad: operaciones con comodines (`LIKE CONCAT(...)`), funciones agregadas, rangos (`BETWEEN`), ordenamiento explícito, proyecciones específicas o filtros combinados complejos.

### 3. ¿Por qué la frontera transaccional se ubica normalmente en la capa Service?
Porque la capa de Servicio es donde reside la lógica de negocio y se coordinan casos de uso completos. Es allí donde se define la unidad de trabajo coherente que puede involucrar validaciones de negocio y llamadas a múltiples repositorios que deben confirmarse o revertirse como un todo atómico. El controlador solo gestiona la comunicación HTTP y el repositorio se limita al acceso a datos atómico individual.

### 4. ¿Qué diferencia existe entre llamar `save()` y confiar en dirty checking dentro de una transacción?
Cuando una entidad ya está administrada (*managed*) por el contexto de persistencia dentro de un método `@Transactional`, cualquier modificación en sus atributos mediante sus setters es detectada automáticamente por Hibernate al final de la transacción (*dirty checking*) y se genera el `UPDATE` correspondiente en la base de datos sin necesidad de invocar explícitamente `repository.save(entidad)`. Llamar a `save()` es redundante en entidades administradas, aunque se requiere cuando se crea una entidad nueva para que entre a dicho contexto o fuera de transacciones.

### 5. ¿Qué ocurriría si el stock se descuenta pero el registro del movimiento falla y no existiera una transacción?
El descuento de stock quedaría guardado y permanente en la tabla `productos`, pero el registro de auditoría en `movimientos_stock` nunca se crearía. Esto generaría una **inconsistencia de datos grave**: el inventario físico o del sistema se reduce sin ningún comprobante ni trazabilidad de por qué se descontó el stock.

### 6. ¿Qué tipo de excepción provoca rollback por defecto en Spring?
Spring ejecuta rollback automáticamente ante excepciones no comprobadas (*unchecked exceptions*), es decir, instancias de `java.lang.RuntimeException` y `java.lang.Error`. Ante excepciones comprobadas (*checked exceptions* derivadas de `java.lang.Exception`), Spring realiza commit a menos que se configure explícitamente con `@Transactional(rollbackFor = Exception.class)`.

### 7. ¿Por qué una transacción no debería mantenerse abierta mientras se realiza una llamada remota lenta?
Mantener una transacción abierta mantiene retenida una conexión del pool de conexiones a la base de datos y conserva bloqueos (*locks*) a nivel de filas o tablas. Si una llamada externa a un servicio web o API remota tarda varios segundos o se cae por latencia de red, se agotarán las conexiones disponibles de la base de datos (*connection pool starvation*), degradando el rendimiento general de la aplicación y pudiendo provocar caídas de servicio.

### 8. ¿Qué problema podría aparecer si devolvemos directamente entidades JPA con relaciones LAZY como respuesta REST?
Aparece la conocida excepción `LazyInitializationException` si el serializador JSON (Jackson) intenta acceder a los atributos perezosos fuera de la sesión transaccional de Hibernate (cuando la sesión ya se cerró). Además, puede derivar en problemas de rendimiento por consultas N+1 o recursión infinita en relaciones bidireccionales. La buena práctica para evitar esto es utilizar **DTOs** (*Data Transfer Objects*) para mapear únicamente los datos que el cliente necesita recibir.
