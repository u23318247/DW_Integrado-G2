# Laboratorio N.° 05: Integración de temas: API REST con Spring Boot y preparación del APF1

- **Curso:** Desarrollo Web Integrado
- **Unidad:** Unidad 1 - API REST
- **Semana:** 5
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java, Spring Boot 3, Spring Web, Maven, MockMvc, JUnit 5, Postman

---

## 📌 1. Descripción del Proyecto

Este laboratorio consolida los temas abordados a lo largo de la Unidad 1 (Spring Boot, controladores REST, servicios de negocio, inyección de dependencias, TDD y semántica de métodos HTTP).
Se implementa una API REST completa para la gestión en memoria de un catálogo de productos (`Productos API v1`), sirviendo como base arquitectónica por capas desacopladas antes de la transición a persistencia real con JPA/Hibernate en la semana 6.

### Componentes de la Arquitectura:
- **`ProductoController`**: Expone endpoints HTTP bajo `/api/productos`, delega las operaciones al servicio e inyecta `ProductoService` mediante constructor.
- **`ProductoService`**: Administra la lógica de negocio, validaciones y la colección en memoria utilizando estructuras concurrentes (`ConcurrentHashMap` y `AtomicLong`).
- **`Producto`**: Clase modelo Java de dominio.
- **`ApiExceptionHandler`**: Manejador global de excepciones con `@RestControllerAdvice` para capturar `IllegalArgumentException` y responder con `400 Bad Request`.
- **Pruebas Automatizadas**: Pruebas unitarias siguiendo el ciclo TDD (`ProductoServiceTest`) y pruebas de controlador con `MockMvc` (`ProductoControllerTest`).

---

## 📋 2. Endpoints Implementados

| Método | Endpoint | Parámetros / Body (JSON) | Código HTTP | Descripción |
| :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/productos` | N/A | `200 OK` | Retorna la lista completa de productos registrados en memoria. |
| **GET** | `/api/productos/{id}` | Path variable `id` | `200 OK` / `404 Not Found` | Obtiene un producto por su ID. |
| **GET** | `/api/productos/buscar?nombre={nombre}` | Query param `nombre` | `200 OK` | Busca productos cuyo nombre contenga el texto recibido (sin importar mayúsculas/minúsculas). |
| **POST** | `/api/productos` | `{"nombre":"...","precio":0.0,"stock":0,"categoria":"..."}` | `201 Created` / `400 Bad Request` | Registra un nuevo producto y genera su ID automático. |
| **PUT** | `/api/productos/{id}` | `{"nombre":"...","precio":0.0,"stock":0,"categoria":"..."}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Actualiza todos los campos de un producto existente. |
| **PATCH** | `/api/productos/{id}/precio?valor={valor}` | Query param `valor` | `200 OK` / `400 Bad Request` / `404 Not Found` | Actualiza únicamente el precio de un producto. |
| **DELETE** | `/api/productos/{id}` | Path variable `id` | `204 No Content` / `404 Not Found` | Elimina un producto por su ID. |

---

## 🎯 3. Reto Integrador (Sección 16)
1. **Atributo `categoria`**: Agregado al modelo `Producto` junto a constructores y métodos de acceso (`getCategoria`, `setCategoria`).
2. **Endpoint de búsqueda `GET /api/productos/buscar?nombre={nombre}`**: Implementado en el servicio usando Streams y filtrado case-insensitive (`contains(filtro.toLowerCase())`).
3. **Pruebas TDD**: Se implementó primero la prueba unitaria `debeBuscarProductosPorNombreIgnorandoMayusculas()` en `ProductoServiceTest` y la prueba de integración WebMvc en `ProductoControllerTest`.

---

## 💡 4. Respuestas a las Preguntas de Reflexión (Sección 18)

### 1. ¿Qué responsabilidad tiene el Controller y cuál tiene el Service?
- **Controller**: Se encarga exclusivamente de la capa web y del protocolo HTTP: atiende las solicitudes entrantes, extrae parámetros de la URI/query/body (`@PathVariable`, `@RequestParam`, `@RequestBody`), delega la operación al servicio correspondiente y construye la respuesta HTTP adecuada (`ResponseEntity` con su respectivo código de estado).
- **Service**: Alberga la lógica de negocio, las reglas de validación (por ejemplo verificar que los precios sean positivos o el stock válido) y la coordinación del estado de los datos.

### 2. ¿Qué problema resuelve la inyección de dependencias?
Resuelve el acoplamiento fuerte entre componentes. En lugar de que una clase instancie directamente con `new` sus dependencias, el contenedor de Spring se encarga de suministrarlas en tiempo de ejecución (preferentemente por constructor). Esto facilita enormemente el principio de responsabilidad única, la modularidad y la realización de pruebas unitarias mediante dobles de prueba (*mocks*).

### 3. ¿Por qué una prueba que falla primero es importante en TDD?
En el ciclo Red-Green-Refactor de TDD, ver fallar la prueba en la fase **RED** garantiza que la prueba realmente está evaluando el comportamiento esperado y que no está pasando por casualidad o dando un falso positivo. Asegura además que la prueba define con precisión el requerimiento antes de implementar la solución mínima necesaria en la fase **GREEN**.

### 4. ¿Qué diferencia existe entre PUT y PATCH en este laboratorio?
- **PUT**: Realiza una actualización completa del recurso; reemplaza todos sus campos (`nombre`, `precio`, `stock`, `categoria`) a partir del payload enviado.
- **PATCH**: Aplica una modificación parcial específica; en este laboratorio se utiliza `/api/productos/{id}/precio?valor=...` para alterar únicamente el atributo del precio sin requerir reenviar todo el objeto completo.

### 5. ¿Por qué almacenar datos en memoria no es suficiente para una aplicación real?
Porque la memoria RAM es volátil: cuando la aplicación finaliza, se reinicia o el servidor se detiene, toda la información almacenada se pierde irremediablemente. Además, no permite escalar horizontalmente múltiples instancias de la aplicación compartiendo el mismo estado de manera consistente, ni ofrece capacidades avanzadas de concurrencia ACID o consultas complejas.

### 6. ¿Qué componente debería reemplazar el almacenamiento en memoria durante la semana 6?
Debe ser reemplazado por una base de datos relacional (como MySQL) administrada a través de un ORM con **Spring Data JPA** e **Hibernate**, donde el almacenamiento en `ConcurrentHashMap` se sustituye por interfaces `Repository` (como `JpaRepository`) conectadas a tablas persistentes.
