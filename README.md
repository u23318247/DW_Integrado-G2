# Proyecto Integrador - Desarrollo Web Integrado (Grupo 2)
## Tienda de Ropa y Accesorios: "Llama a la Moda"

- **Curso:** Desarrollo Web Integrado (100000ST61)
- **Ciclo:** Sexto Ciclo
- **Entrega:** Avance 1 (Alcance Semanas 1 a 4: Arquitectura en Capas y API RESTful)

---

## 📌 1. Descripción del Proyecto

"Llama a la Moda" es una plataforma e-commerce de prendas y accesorios andinos y urbanos. 

En este **Avance 1**, el backend implementa una **API RESTful completa** en Spring Boot 3 para la gestión del catálogo de productos y categorías, incorporando:
* Arquitectura limpia por capas: **Controller, Service, Model, DTO y Exception**.
* Métodos HTTP semánticos (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`).
* Validaciones automáticas de datos con **Jakarta Validation**.
* Manejo centralizado y uniforme de errores en formato JSON con `@RestControllerAdvice`.
* Almacenamiento concurrente y seguro en memoria (`ConcurrentHashMap` y `AtomicLong`) como paso previo a la persistencia con JPA/MySQL en la Unidad 2.
* Cobertura de pruebas automatizadas con **JUnit 5**, **AssertJ** y **MockMvc**.

---

## 📋 2. Catálogo de Endpoints de la API REST

### Recursos de Productos (`/api/productos`)

| Método | Endpoint | Body (JSON) | Códigos HTTP | Descripción |
| :---: | :--- | :--- | :---: | :--- |
| **GET** | `/api/productos` | Ninguno | `200 OK` | Lista todos los productos. Soporta filtros: `?categoria={cat}` y `?genero={gen}` |
| **GET** | `/api/productos/{id}` | Ninguno | `200 OK` / `404 Not Found` | Obtiene el detalle de una prenda por su ID |
| **GET** | `/api/productos/buscar` | Ninguno | `200 OK` | Búsqueda por coincidencia parcial de texto: `?texto={nombre}` |
| **POST** | `/api/productos` | `{ "nombre": "...", "categoria": "...", "genero": "...", "precio": 0.0, "stock": 0 }` | `201 Created` / `400 Bad Request` | Registra una nueva prenda en el catálogo y devuelve header `Location` |
| **PUT** | `/api/productos/{id}` | `{ "nombre": "...", "categoria": "...", "genero": "...", "precio": 0.0, "stock": 0 }` | `200 OK` / `400 Bad Request` / `404 Not Found` | Reemplazo y actualización completa de una prenda |
| **PATCH** | `/api/productos/{id}/stock` | `{ "stock": 50 }` | `200 OK` / `400 Bad Request` / `404 Not Found` | Actualización parcial del stock de la prenda |
| **PATCH** | `/api/productos/{id}/disminuir-stock` | `{ "cantidad": 5 }` | `200 OK` / `400 Bad Request` / `404 Not Found` | Disminución de stock con regla de negocio (no permitir saldo negativo) |
| **DELETE** | `/api/productos/{id}` | Ninguno | `204 No Content` / `404 Not Found` | Elimina una prenda del catálogo |

### Recursos de Categorías (`/api/categorias`)

| Método | Endpoint | Body (JSON) | Códigos HTTP | Descripción |
| :---: | :--- | :--- | :---: | :--- |
| **GET** | `/api/categorias` | Ninguno | `200 OK` | Lista todas las categorías disponibles |
| **GET** | `/api/categorias/{id}` | Ninguno | `200 OK` / `404 Not Found` | Obtiene una categoría por su ID |
| **POST** | `/api/categorias` | `{ "nombre": "...", "descripcion": "..." }` | `201 Created` | Registra una nueva categoría |

---

## 🧪 3. Ejecución y Pruebas

### Ejecutar Pruebas Automatizadas:
```bash
./mvnw clean test
```

### Iniciar el Servidor REST:
```bash
./mvnw spring-boot:run
```
La API estará disponible en `http://localhost:8080/api/productos`.
