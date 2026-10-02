# Laboratorio N.° 06: Manejo de datos con Spring Boot, JPA, Hibernate y operaciones CRUD

- **Curso:** Desarrollo Web Integrado
- **Unidad:** Unidad 2 - Back-end con bases de datos
- **Semana:** 6
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java, Spring Boot 3, Spring Data JPA, Hibernate, MySQL Driver

---

## 📌 1. Descripción del Proyecto

En esta práctica se implementa la persistencia real de datos en una base de datos relacional MySQL para la API REST de productos, reemplazando el almacenamiento temporal en memoria por una capa de persistencia basada en **Spring Data JPA** e **Hibernate**.
Se mantiene la arquitectura modular por capas desacopladas:
- **Entity (`Producto`):** Mapeo objeto-relacional mediante JPA/Hibernate.
- **Repository (`ProductoRepository`):** Interfaz que extiende `JpaRepository` para operaciones CRUD directas.
- **Service (`ProductoService`):** Lógica de negocio, validaciones y delimitación transaccional con `@Transactional`.
- **Controller (`ProductoController`):** Endpoints REST semánticos que atienden solicitudes HTTP.

---

## 📋 2. Endpoints Implementados

| Método | URL | Body Esperado (JSON) | Código HTTP | Descripción |
| :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/productos` | N/A | `200 OK` | Listar todos los productos registrados |
| **GET** | `/api/productos/{id}` | N/A | `200 OK` / `404 Not Found` | Obtener producto por ID |
| **POST** | `/api/productos` | `{"nombre":"...","precio":0.0,"stock":0,"categoria":"..."}` | `201 Created` | Crear y persistir nuevo producto en MySQL |
| **PUT** | `/api/productos/{id}` | `{"nombre":"...","precio":0.0,"stock":0,"categoria":"..."}` | `200 OK` / `404 Not Found` | Actualizar todos los datos de un producto existente |
| **PATCH** | `/api/productos/{id}/precio?valor=...` | N/A | `200 OK` / `404 Not Found` | Actualizar de forma parcial el precio del producto |
| **DELETE** | `/api/productos/{id}` | N/A | `204 No Content` / `404 Not Found` | Eliminar producto por ID de la base de datos |

---

## 🎯 3. Reto Práctico Cumplido
Se incorporó el atributo `categoria` a la entidad `Producto` con la anotación `@Column(nullable = false, length = 80)` y sus respectivos constructores y métodos getter/setter, permitiendo a Hibernate actualizar automáticamente la tabla `productos` con la columna `categoria`.

---

## 💡 4. Respuestas a las Preguntas de Reflexión

### 1. ¿Cuál es la diferencia entre JPA e Hibernate?
JPA (*Jakarta Persistence*) es una especificación estándar de Java que define interfaces y anotaciones de mapeo objeto-relacional sin implementar lógica directamente. Hibernate es la implementación ORM concreta del estándar JPA que traduce las entidades y operaciones Java a sentencias SQL compatibles con el motor relacional (MySQL).

### 2. ¿Qué ventaja ofrece Spring Data JPA frente a implementar acceso JDBC manual?
Elimina la necesidad de escribir código repetitivo (apertura/cierre de conexiones, `PreparedStatement`, mapeo de `ResultSet` fila por fila) y genera automáticamente en tiempo de ejecución las consultas y operaciones CRUD básicas (`save`, `findById`, `findAll`, `deleteById`) a través de interfaces declarativas.

### 3. ¿Por qué ProductoRepository es una interfaz y aun así puede inyectarse como objeto?
Spring Data JPA crea dinámicamente en tiempo de ejecución un objeto proxy (*Dynamic Proxy*) que implementa dicha interfaz y proporciona la lógica de acceso a datos correspondiente, permitiendo al contenedor de dependencias de Spring inyectarlo como un bean gestionado en el servicio.

### 4. ¿Qué función cumple @Entity?
Le indica a JPA e Hibernate que la clase Java representa una entidad persistente cuya estructura se mapeará a una tabla de base de datos relacional y cuyo ciclo de vida será administrado por el contexto de persistencia.

### 5. ¿Qué ocurre con el identificador cuando se utiliza GenerationType.IDENTITY?
Delegará la generación del identificador primario al mecanismo autoincremental propio de la base de datos (columna `AUTO_INCREMENT` en MySQL). El ID es generado automáticamente por el motor de BD al ejecutar la sentencia `INSERT`.

### 6. ¿Por qué el Controller de la semana 5 necesitó pocos cambios al introducir la base de datos?
Gracias a la arquitectura por capas y al principio de inversión y desacoplamiento de dependencias: el controlador únicamente interactúa con la capa de servicio (`ProductoService`) y desconoce los detalles de persistencia (si los datos están en memoria, en archivos o en una base de datos MySQL).

### 7. ¿Qué riesgo existe al usar ddl-auto=update en producción?
En entornos de producción puede ocasionar modificaciones no controladas del esquema, bloqueo de tablas o inconsistencias en los datos ante cambios inesperados en el código de las entidades. Para entornos productivos se recomienda utilizar `ddl-auto=none` o `validate`, y gestionar las modificaciones del esquema mediante herramientas de migración estructuradas como Flyway o Liquibase.
