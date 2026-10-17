# Ali_Contreras — Guía de Laboratorio 06

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 6
**Proyecto:** API REST CRUD de Productos persistida en MySQL (Spring Boot + Spring Data JPA + Hibernate)

Evolución de la API de la semana 5: se reemplaza el almacenamiento en memoria
(`ConcurrentHashMap`) por persistencia real con **Spring Data JPA**, **Hibernate** y **MySQL**.
El Controller y el Service conservan su responsabilidad; solo cambia el mecanismo de
almacenamiento, gracias al trabajo por capas.

## Arquitectura

```
Cliente HTTP
   ↓
ProductoController        (capa web: recibe HTTP, construye ResponseEntity)
   ↓
ProductoService           (lógica, validaciones, @Transactional)
   ↓
ProductoRepository        (Spring Data JPA)
   ↓
JPA / Hibernate → JDBC → MySQL (base de datos productos_db)
```

```
src/main/java/com/utp/productosapi/
├── ProductosApiApplication.java
├── controller/ProductoController.java
├── exception/ApiExceptionHandler.java   # IllegalArgumentException -> 400
├── model/Producto.java                  # @Entity @Table(name="productos")
├── repository/ProductoRepository.java   # extends JpaRepository<Producto, Long>
└── service/ProductoService.java         # delega en el repository
src/test/java/com/utp/productosapi/
└── repository/ProductoRepositoryTest.java   # @DataJpaTest (corre sobre H2)
```

## Requisitos

- Java 21+ (el `pom.xml` compila con `java.version=21`).
- MySQL Server activo en `localhost:3306`.
- Maven.

## Preparar la base de datos

En MySQL Workbench (o cliente equivalente):

```sql
CREATE DATABASE productos_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

No es necesario crear la tabla: Hibernate la genera a partir de la entidad
(`spring.jpa.hibernate.ddl-auto=update`).

## Configuración (`src/main/resources/application.properties`)

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productos_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root      # cambiar por la contraseña local de MySQL
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> **Seguridad:** en el laboratorio local la contraseña va en el archivo. En un proyecto
> real debe ir en una variable de entorno o gestor de secretos, nunca versionada.

## Cómo ejecutar

```bash
mvn spring-boot:run
```

La API queda en http://localhost:8080/api/productos

## Endpoints (CRUD)

| Método | URL | Descripción | Respuesta |
|--------|-----|-------------|-----------|
| POST | /api/productos | Crear producto | 201 / 400 |
| GET | /api/productos | Listar todos | 200 |
| GET | /api/productos/{id} | Obtener por id | 200 / 404 |
| PUT | /api/productos/{id} | Actualización completa | 200 / 400 / 404 |
| PATCH | /api/productos/{id}/precio?valor=4200 | Actualizar solo el precio | 200 / 400 / 404 |
| DELETE | /api/productos/{id} | Eliminar | 204 / 404 |

Body de ejemplo (POST / PUT):

```json
{"nombre":"Laptop Lenovo","categoria":"Tecnologia","precio":3500,"stock":10}
```

## Reto práctico — campo `categoria`

Se agregó el campo `categoria` a la entidad y Hibernate actualizó el esquema
(`ddl-auto=update`). Se registran productos de distintas categorías y el nuevo campo
aparece en MySQL:

```sql
DESCRIBE productos;   -- incluye: categoria varchar(80) NOT NULL
SELECT id, nombre, categoria FROM productos;
```

## Verificación realizada

Se probó el CRUD completo contra MySQL con la aplicación en ejecución:

| Operación | Resultado obtenido |
|-----------|--------------------|
| POST (x2) | 201 Created, ids 1 y 2 generados por MySQL |
| GET lista | 200, ambos productos |
| PUT /1 | 200, producto actualizado |
| PATCH /1/precio?valor=4200 | 200, solo cambió el precio |
| GET /999 | 404 Not Found |
| POST precio=0 | 400 `{"error":"El precio debe ser mayor que cero"}` |
| DELETE /2 | 204 No Content |

**Prueba de persistencia (paso 16 de la guía):** se crearon productos, se detuvo la
aplicación y se volvió a iniciar. Con la app apagada, `SELECT COUNT(*) FROM productos`
seguía devolviendo las filas, y tras reiniciar, `GET /api/productos` las mostró intactas.
Los datos ya no dependen de la memoria del proceso: están persistidos en MySQL.

En la consola de Hibernate (`show-sql=true`) se observan las sentencias
`insert into productos ...`, `update productos set ... where id=?`,
`select ... from productos` y `delete from productos where id=?`.

## Pruebas automatizadas

```bash
mvn test
```

`ProductoRepositoryTest` usa `@DataJpaTest` y una base embebida **H2** (dependencia con
alcance `test`), de modo que las pruebas corren en cualquier equipo sin necesidad de MySQL.
Verifican: guardar y recuperar por id, persistir el campo `categoria` y eliminar.

> **Nota de versiones:** la guía usa Spring Boot 4.x, donde `@DataJpaTest` está en
> `org.springframework.boot.data.jpa.test.autoconfigure` y requiere
> `spring-boot-starter-data-jpa-test`. Este proyecto usa **Spring Boot 3.5.5**, por lo que
> `@DataJpaTest` se importa de `org.springframework.boot.test.autoconfigure.orm.jpa`.

## Diferencia con la semana 5

| Semana 5 | Semana 6 |
|----------|----------|
| Datos en memoria (`ConcurrentHashMap`) | Datos en MySQL |
| Se pierden al reiniciar | Persisten tras reiniciar |
| `Producto` es un POJO | `Producto` es una `@Entity` |
| El Service administra una colección | El Service delega en `ProductoRepository` |

## Preguntas de reflexión

1. **¿Cuál es la diferencia entre JPA e Hibernate?** JPA (Jakarta Persistence) es la
   especificación: define las anotaciones y contratos estándar. Hibernate es una
   implementación concreta de esa especificación que traduce las operaciones sobre
   entidades a SQL.

2. **¿Qué ventaja ofrece Spring Data JPA frente a JDBC manual?** Genera automáticamente la
   implementación del repositorio (`save`, `findAll`, `findById`, `deleteById`, …), evitando
   escribir SQL y el código repetitivo de conexiones, `PreparedStatement` y `ResultSet`.

3. **¿Por qué `ProductoRepository` es una interfaz y aun así puede inyectarse?** Spring Data
   JPA crea en tiempo de ejecución una clase proxy que implementa la interfaz y la registra
   como bean; Spring inyecta ese proxy.

4. **¿Qué función cumple `@Entity`?** Marca la clase como administrada por JPA, indicando que
   se mapea a una tabla de la base de datos.

5. **¿Qué ocurre con el identificador con `GenerationType.IDENTITY`?** Lo genera la propia
   base de datos mediante su columna `AUTO_INCREMENT`; el id queda disponible en la entidad
   después de ejecutarse el `INSERT`.

6. **¿Por qué el Controller de la semana 5 necesitó pocos cambios?** Porque depende de la
   abstracción `ProductoService` y no conoce cómo se almacenan los datos; el cambio de
   almacenamiento en memoria a MySQL quedó encapsulado en Service y Repository.

7. **¿Qué riesgo existe al usar `ddl-auto=update` en producción?** Deja que Hibernate
   modifique el esquema automáticamente; puede provocar cambios no controlados o pérdida de
   datos. En producción se usan migraciones versionadas (Flyway, Liquibase) y `validate`.
