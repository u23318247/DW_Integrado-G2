# Laboratorio N.° 04: Implementación de API REST, herramientas de prueba y métodos HTTP

- **Curso:** Desarrollo Web Integrado (100000ST61)
- **Semana:** 4
- **Estudiante:** Antonela Jaimes
- **Tecnologías:** Java 21/25, Spring Boot 3, Maven, MockMvc, JUnit 5, Postman / Thunder Client / cURL

---

## 📌 1. Descripción del Proyecto

API RESTful para la gestión de un catálogo de productos desarrollada con Spring Boot y arquitectura por capas (Controller, Service, Model, DTO, Exception). Se implementan operaciones CRUD completas utilizando métodos HTTP semánticos (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`), validaciones con Jakarta Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Positive`, `@Min`) y manejo global centralizado de excepciones con `@RestControllerAdvice`.

---

## 📋 2. Documentación de Endpoints (Ejercicio 3)

| Método | URL | Body Esperado (JSON) | Código HTTP | Descripción |
| :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/productos` | N/A | `200 OK` | Lista todos los productos o filtra por categoría con `?categoria={valor}` |
| **GET** | `/api/productos/{id}` | N/A | `200 OK` / `404 Not Found` | Obtiene un producto por su ID |
| **GET** | `/api/productos/buscar?texto={texto}` | N/A | `200 OK` | Busca productos cuyo nombre contenga el texto indicado (Ejercicio 1) |
| **POST** | `/api/productos` | `{"nombre":"...","categoria":"...","precio":0.0,"stock":0}` | `201 Created` / `400 Bad Request` | Crea un nuevo producto y retorna header `Location` |
| **PUT** | `/api/productos/{id}` | `{"nombre":"...","categoria":"...","precio":0.0,"stock":0}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Reemplaza o actualiza completamente un producto |
| **PATCH** | `/api/productos/{id}/stock` | `{"stock": 20}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Actualiza parcialmente el stock de un producto |
| **PATCH** | `/api/productos/{id}/disminuir-stock` | `{"cantidad": 5}` | `200 OK` / `400 Bad Request` / `404 Not Found` | Disminuye el stock validando no dejar valores negativos (Ejercicio 2) |
| **DELETE** | `/api/productos/{id}` | N/A | `204 No Content` / `404 Not Found` | Elimina un producto por su ID |

---

## 💡 3. Respuestas a las Preguntas de Reflexión

### 1. ¿Por qué GET no debe usarse para crear, actualizar o eliminar datos?
Porque el protocolo HTTP define que el método `GET` debe ser **seguro** e **idempotente**, destinado únicamente a consultar y recuperar recursos sin producir efectos secundarios ni mutar el estado en el servidor. Además, las solicitudes `GET` pueden ser cacheadas por navegadores y proxies o registradas en historiales y logs de URLs.

### 2. ¿Qué diferencia existe entre POST y PUT?
* **POST**: Se utiliza para crear un nuevo recurso secundario subordinado a una colección. No suele ser idempotente (múltiples llamadas generan múltiples recursos con nuevos IDs).
* **PUT**: Se utiliza para reemplazar o actualizar **completamente** un recurso existente ubicado en una URI específica. Es idempotente (ejecutar la misma petición varias veces produce el mismo estado en el servidor).

### 3. ¿Qué diferencia existe entre PUT y PATCH?
* **PUT**: Realiza un reemplazo completo del recurso; requiere enviar la totalidad de los campos del objeto.
* **PATCH**: Realiza una actualización **parcial** del recurso; solo se envían los atributos o campos específicos que se desean modificar (por ejemplo, únicamente el campo `stock`).

### 4. ¿Por qué es útil `ResponseEntity` en una API REST?
`ResponseEntity` permite un control total y explícito sobre la respuesta HTTP enviada al cliente, facilitando configurar el **código de estado HTTP** (200, 201, 204, 400, 404), añadir **cabeceras personalizadas** (como `Location`) y definir el **cuerpo** de la respuesta serializado en JSON.

### 5. ¿Qué función cumple `@RequestBody`?
Indica a Spring MVC que debe deserializar automáticamente el cuerpo (payload) de la solicitud HTTP (generalmente formato JSON) y mapearlo a un objeto Java correspondiente (ej. un DTO).

### 6. ¿Qué diferencia hay entre `@PathVariable` y `@RequestParam`?
* **`@PathVariable`**: Extrae valores que forman parte directa de la estructura de la URI del recurso (ejemplo: `/api/productos/{id}`).
* **`@RequestParam`**: Extrae parámetros de consulta (query parameters) ubicados tras el signo `?` en la URL (ejemplo: `/api/productos?categoria=Tecnologia`).

### 7. ¿Por qué conviene usar DTOs en lugar de recibir directamente cualquier objeto?
* Desacopla la capa de presentación/API del modelo interno o de entidades de base de datos.
* Permite controlar con precisión qué campos acepta la API y qué campos devuelve.
* Facilita aplicar validaciones específicas de entrada (`@NotBlank`, `@Min`, `@Positive`) sin ensuciar el modelo de dominio.
* Previene ataques de sobreasignación masiva (*mass assignment*).

### 8. ¿Qué significa devolver un error 400?
Significa **Bad Request (Solicitud Incorrecta)**. Indica que la petición del cliente contiene errores de sintaxis, formato incorrecto o no cumple con las reglas de validación de datos (por ejemplo, enviar campos obligatorios vacíos o valores numéricos negativos).

### 9. ¿Qué significa devolver un error 404?
Significa **Not Found (No Encontrado)**. Indica que el servidor no pudo encontrar el recurso solicitado identificado por la URI (por ejemplo, buscar un producto con un `id` que no existe en el sistema).

### 10. ¿Qué ventaja tiene centralizar errores con `@RestControllerAdvice`?
Permite interceptar de manera global todas las excepciones lanzadas por cualquier controlador de la aplicación, evitando bloques repetitivos de `try-catch`. Garantiza que todos los errores se respondan con un formato JSON estructurado, consistente y con el código de estado HTTP adecuado.

---

## 🚀 4. Comandos de Ejecución y Verificación

### Compilar y Ejecutar Pruebas:
```bash
./mvnw clean test
```

### Ejecutar la Aplicación:
```bash
./mvnw spring-boot:run
```

### Empaquetar JAR:
```bash
./mvnw clean package
java -jar target/semana4-api-rest-0.0.1-SNAPSHOT.jar
```
