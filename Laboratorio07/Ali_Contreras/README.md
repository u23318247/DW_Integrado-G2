# Ali_Contreras — Guía de Laboratorio 07

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 7
**Proyecto:** API Tienda — JPQL y Transacciones con Spring Boot (Spring Data JPA + Hibernate + MySQL)

Continúa la API de la semana 6 agregando consultas personalizadas (derivadas y JPQL con
`@Query`) y operaciones de inventario transaccionales que confirman (**commit**) o revierten
(**rollback**) todos sus cambios como una sola unidad.

## Arquitectura

```
Controller  →  Service (@Transactional)  →  Repository  →  Hibernate / MySQL
```

```
src/main/java/com/utp/tienda/
├── TiendaApplication.java
├── controller/ProductoController.java
├── model/
│   ├── Producto.java                  # precio BigDecimal(12,2), stock Integer
│   └── MovimientoStock.java           # @ManyToOne LAZY hacia Producto
├── repository/
│   ├── ProductoRepository.java        # consulta derivada + 3 consultas JPQL
│   └── MovimientoStockRepository.java
├── service/ProductoService.java       # frontera transaccional
└── exception/
    ├── RecursoNoEncontradoException.java   -> 404
    ├── ReglaNegocioException.java          -> 400
    └── ApiExceptionHandler.java            (IllegalStateException -> 500)
```

## Cómo ejecutar

Requisitos: Java 21+, Maven y MySQL en `localhost:3306` (usuario `root`, contraseña `root`;
cambiarla en `application.properties` si tu MySQL usa otra).

```bash
mvn spring-boot:run
```

La base `tienda_db` se crea sola (`createDatabaseIfNotExist=true`) y Hibernate genera las
tablas `productos` y `movimientos_stock` (`ddl-auto=update`). Datos de ejemplo (paso 9):

```sql
INSERT INTO productos (nombre, categoria, precio, stock) VALUES
('Laptop Lenovo ThinkPad', 'Tecnologia', 4200.00, 8),
('Mouse Logitech MX', 'Tecnologia', 320.00, 20),
('Silla ergonómica', 'Muebles', 850.00, 6),
('Escritorio ejecutivo', 'Muebles', 1200.00, 4),
('Monitor 27 pulgadas', 'Tecnologia', 1450.00, 10);
```

## Endpoints

| Método | Endpoint | Tipo de consulta / operación | Respuesta |
|--------|----------|------------------------------|-----------|
| GET | /api/productos | CRUD heredado (`findAll`) | 200 |
| GET | /api/productos/{id} | CRUD heredado (`findById`) | 200 / 404 |
| POST | /api/productos | Crear | 201 / 400 |
| PUT | /api/productos/{id} | Actualizar (dirty checking) | 200 / 400 / 404 |
| DELETE | /api/productos/{id} | Eliminar (rechaza si tiene movimientos) | 204 / 400 / 404 |
| GET | /api/productos/buscar?texto=lap | JPQL `LIKE` sin distinguir mayúsculas | 200 / 400 |
| GET | /api/productos/categoria/{categoria} | Consulta derivada `findByCategoriaIgnoreCase` | 200 |
| GET | /api/productos/precio?min=300&max=1500 | JPQL `BETWEEN` ordenado por precio | 200 / 400 |
| GET | /api/productos/stock-bajo?limite=5 | JPQL `stock <= :limite` *(consolidación)* | 200 / 400 |
| POST | /api/productos/{id}/salidas?cantidad=2 | Transacción: descuenta stock + movimiento SALIDA | 200 / 400 / 404 |
| POST | /api/productos/{id}/entradas?cantidad=5 | Transacción: suma stock + movimiento ENTRADA *(consolidación)* | 200 / 400 / 404 |
| POST | /api/productos/{id}/salidas/simular-error?cantidad=1 | Fuerza `IllegalStateException` → rollback | 500 |

## Evidencia de la verificación en MySQL

Pruebas ejecutadas contra MySQL 8.0 con los datos de ejemplo:

| Prueba | Resultado |
|--------|-----------|
| `buscar?texto=lap` | 200, solo *Laptop Lenovo ThinkPad* |
| `categoria/tecnologia` (minúsculas) | 200, los 3 productos de Tecnologia |
| `precio?min=300&max=1500` | 200, 4 productos ordenados de 320 a 1450 |
| `precio?min=2000&max=100` | 400 "El precio minimo no puede superar al maximo" |
| `stock-bajo?limite=5` | 200, *Escritorio ejecutivo* (stock 4) |
| `buscar?texto=` (solo espacios) | 400 "El texto de busqueda no puede estar vacio" |
| `4/salidas?cantidad=50` | 400 "Stock insuficiente. Disponible: 4" |
| `999/salidas?cantidad=1` | 404 "Producto no encontrado: 999" |

### Commit vs. rollback (producto id = 1)

| Operación | Stock | Movimientos | ¿Qué ocurrió? |
|-----------|-------|-------------|---------------|
| Estado inicial | 8 | 0 | — |
| `POST /1/salidas?cantidad=2` | **6** | **1** | **COMMIT**: stock y movimiento confirmados juntos |
| `POST /1/salidas/simular-error?cantidad=1` → 500 | **6** | **1** | **ROLLBACK**: no quedó ningún cambio parcial |
| `POST /1/entradas?cantidad=5` | **11** | **2** | **COMMIT** |

```
SELECT id, producto_id, tipo, cantidad FROM movimientos_stock;
id | producto_id | tipo    | cantidad
1  | 1           | SALIDA  | 2
3  | 1           | ENTRADA | 5
```

El **id 2 no existe**: la operación con error sí ejecutó el `INSERT` del movimiento (y consumió
ese valor de `AUTO_INCREMENT`), pero al propagarse la `IllegalStateException` Spring hizo
rollback y el registro desapareció junto con el descuento de stock. Es la prueba de que la
transacción revierte **todo**, no solo una parte.

SQL que Hibernate generó para la consulta JPQL `buscarPorNombre`:

```sql
select p1_0.id, p1_0.categoria, p1_0.nombre, p1_0.precio, p1_0.stock
from productos p1_0
where lower(p1_0.nombre) like lower(concat('%', ?, '%')) escape ''
order by p1_0.nombre
```

## Pruebas automatizadas

```bash
mvn test
```

Corren sobre H2 en memoria (`src/test/resources/application.properties`), sin necesitar MySQL.
**10 pruebas, 0 fallos:**

- `ProductoRepositoryTest` (`@DataJpaTest`, 4): búsqueda parcial por nombre, consulta derivada
  por categoría, rango de precios ordenado y stock bajo.
- `ProductoServiceTransaccionTest` (`@SpringBootTest`, 6): commit de una salida, **rollback**
  del error simulado, salida con stock insuficiente sin cambios, entrada exitosa, texto de
  búsqueda vacío y rango invertido. La clase de prueba **no** usa `@Transactional` a propósito,
  para que cada llamada al Service abra y cierre su propia transacción real.

> **Nota de versiones:** se usa Spring Boot 3.5.5 con Java 21 (compatible con JDK 21 o superior).

## Reto avanzado opcional: `@Modifying` + `@Query`

```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("UPDATE Producto p SET p.precio = p.precio * :factor WHERE p.categoria = :categoria")
int reajustarPrecios(@Param("factor") BigDecimal factor, @Param("categoria") String categoria);
```

Una actualización masiva JPQL se ejecuta directamente en la base de datos y **se salta el
contexto de persistencia**: las entidades ya cargadas en memoria no se enteran del cambio y
quedan desactualizadas, y los cambios pendientes podrían no haberse enviado antes. Por eso se
usan `flushAutomatically` (envía los cambios pendientes antes) y `clearAutomatically` (limpia el
contexto después), y el método debe ejecutarse dentro de una transacción. No se incluyó en el
flujo principal, como indica la guía.

## Preguntas de reflexión

1. **¿Por qué JPQL usa el nombre de la entidad y no el de la tabla?** Porque consulta el modelo
   de objetos que administra JPA (`Producto`, `p.precio`); Hibernate traduce eso al SQL de la
   tabla y columnas reales. Así la consulta no depende de nombres físicos ni del motor.

2. **¿Cuándo una consulta derivada y cuándo `@Query`?** La derivada para filtros simples que el
   nombre del método expresa con claridad (`findByCategoriaIgnoreCase`). `@Query` cuando se
   necesita más expresividad: `LIKE` con `CONCAT`, `BETWEEN` con orden, funciones, joins.

3. **¿Por qué la frontera transaccional va en el Service?** Porque ahí se coordinan las reglas
   de negocio y varios repositorios (producto + movimiento) que deben confirmarse juntos. El
   Controller solo maneja HTTP y el Repository solo accede a datos.

4. **¿Diferencia entre `save()` y dirty checking?** `save()` pide persistir explícitamente. Con
   dirty checking, una entidad cargada dentro de la transacción está administrada: basta con
   modificarla y Hibernate emite el `UPDATE` al hacer commit (así funcionan `registrarSalida` y
   `actualizar`).

5. **¿Qué pasaría si el stock se descuenta, el movimiento falla y no hay transacción?** El stock
   quedaría descontado sin registro que lo justifique: datos inconsistentes. Con la
   transacción, ambos cambios se revierten.

6. **¿Qué excepción provoca rollback por defecto?** Las `RuntimeException` (no verificadas) y
   los `Error`. Las excepciones *checked* requieren `rollbackFor`.

7. **¿Por qué no mantener una transacción abierta durante una llamada remota lenta?** Mantiene
   conexiones y bloqueos de la base de datos ocupados mientras espera, lo que reduce la
   concurrencia y puede provocar esperas o *timeouts* en otras operaciones.

8. **¿Qué problema hay al devolver entidades con relaciones LAZY por REST?** Al serializar, Jackson
   intenta leer la relación fuera de la transacción (con `open-in-view=false`) y se produce
   `LazyInitializationException`, o se exponen más datos de los necesarios. Por eso
   `MovimientoStock` no se devuelve directamente; en esos casos se usan DTO.
