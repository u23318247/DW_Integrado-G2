# Ali_Contreras — Guía de Laboratorio 05

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 5
**Proyecto:** Productos API v1 — API REST en memoria con TDD (Spring Boot, JAR)

Laboratorio integrador de la Unidad 1: consolida Spring Boot, controladores, servicios,
inyección de dependencias, TDD (Red-Green-Refactor) y métodos HTTP. Los datos se guardan
temporalmente en memoria (`ConcurrentHashMap` + `AtomicLong`); en la semana 6 se migrará a
MySQL con Spring Data JPA.

## Estructura

```
src/
├── main/java/com/utp/productosapi/
│   ├── ProductosApiApplication.java
│   ├── controller/ProductoController.java     # Capa web: recibe HTTP y delega
│   ├── exception/ApiExceptionHandler.java     # IllegalArgumentException -> 400
│   ├── model/Producto.java                    # POJO (aún no es entidad JPA)
│   └── service/ProductoService.java           # Lógica, validaciones y almacenamiento
└── test/java/com/utp/productosapi/
    ├── service/ProductoServiceTest.java       # Pruebas unitarias (TDD)
    └── controller/ProductoControllerTest.java # MockMvc + @MockitoBean
```

## Cómo ejecutar

1. Abrir la carpeta `Ali_Contreras` en Visual Studio Code (File > Open Folder).
2. Esperar a que VS Code descargue las dependencias de Maven.
3. En la terminal integrada:

```bash
mvn spring-boot:run
```

La API queda disponible en: http://localhost:8080/api/productos

> El `pom.xml` usa `java.version=21` (LTS) para que compile y ejecute con cualquier JDK 21 o
> superior instalado (21, 22 o 25), sin necesidad de parámetros adicionales.

## Endpoints

| Método | URL | Descripción | Respuesta |
|--------|-----|-------------|-----------|
| GET | /api/productos | Listar todos los productos | 200 |
| GET | /api/productos/{id} | Obtener producto por id | 200 / 404 |
| GET | /api/productos/buscar?nombre=lap | Reto: buscar por nombre (ignora mayúsculas) | 200 |
| POST | /api/productos | Crear producto | 201 / 400 |
| PUT | /api/productos/{id} | Actualización completa | 200 / 400 / 404 |
| PATCH | /api/productos/{id}/precio?valor=4100 | Actualizar solo el precio | 200 / 400 / 404 |
| DELETE | /api/productos/{id} | Eliminar producto | 204 / 404 |

Ejemplo de body para POST / PUT:

```json
{"nombre":"Laptop","categoria":"Tecnologia","precio":3500,"stock":10}
```

Errores de validación (nombre vacío, precio ≤ 0, stock negativo) responden `400` con:

```json
{"error":"El precio debe ser mayor que cero"}
```

## Pruebas realizadas (paso 14 de la guía)

| N.° | Método | URL / Body | Resultado obtenido |
|-----|--------|------------|--------------------|
| 1 | GET | /api/productos | 200 y `[]` |
| 2 | POST | `{"nombre":"Laptop","precio":3500,"stock":10}` | 201 y producto con `id: 1` |
| 3 | GET | /api/productos/1 | 200 y producto |
| 4 | PUT | /api/productos/1 `{"nombre":"Laptop Pro","precio":3900,"stock":7}` | 200 |
| 5 | PATCH | /api/productos/1/precio?valor=4100 | 200 |
| 6 | DELETE | /api/productos/1 | 204 |
| 7 | GET | /api/productos/999 | 404 |
| 8 | POST | precio = 0 | 400 `{"error":"El precio debe ser mayor que cero"}` |
| Reto | GET | /api/productos/buscar?nombre=lap | 200 y lista con coincidencias |

## TDD aplicado

1. **RED:** se escribió `ProductoServiceTest.debeCrearProductoYAsignarId()` antes de que
   existiera `ProductoService`; la compilación de pruebas falló (`cannot find symbol`).
2. **GREEN:** se implementó `ProductoService` con el mínimo necesario y la prueba pasó.
3. **REFACTOR / ampliación:** se agregaron el resto de pruebas del servicio, la prueba del
   controlador con `MockMvc` y, para el reto, primero la prueba `debeBuscarPorNombreSinDistinguirMayusculas()`
   y luego el método `buscarPorNombre()`.

Resultado final: **28 pruebas, 0 fallos** (16 del servicio + 12 del controlador).

## Pruebas y empaquetado

```bash
mvn test
mvn clean package
java -jar target/productos-api-0.0.1-SNAPSHOT.jar
```

## Lista de verificación final

- [x] La aplicación inicia sin errores.
- [x] Todos los endpoints devuelven códigos HTTP coherentes.
- [x] El Controller no crea manualmente `ProductoService` con `new` (inyección por constructor).
- [x] El Service contiene la lógica y validaciones.
- [x] Las pruebas Maven terminan correctamente.
- [x] Los errores de validación retornan 400 y los recursos inexistentes 404.
- [x] La API fue probada con los 8 casos del paso 14.

## Preguntas de reflexión

1. **¿Qué responsabilidad tiene el Controller y cuál tiene el Service?**
   El Controller recibe la solicitud HTTP, la traduce (path variables, params, body), delega
   en el Service y construye la respuesta HTTP (`ResponseEntity`, códigos de estado). El Service
   contiene la lógica de negocio: validaciones, reglas y acceso al almacenamiento.

2. **¿Qué problema resuelve la inyección de dependencias?**
   Evita que el Controller cree sus dependencias con `new`, lo que acoplaría las clases y haría
   difícil probarlas. Spring crea y entrega el `ProductoService`, y en las pruebas se puede
   sustituir por un mock (`@MockitoBean`) sin tocar el Controller.

3. **¿Por qué una prueba que falla primero es importante en TDD?**
   Demuestra que la prueba realmente verifica algo: si pasara desde el inicio, no sabríamos si
   valida la funcionalidad o si está mal escrita. El fallo inicial (RED) confirma que el código
   posterior (GREEN) es lo que la hace pasar.

4. **¿Qué diferencia existe entre PUT y PATCH en este laboratorio?**
   PUT (`/api/productos/{id}`) reemplaza el recurso completo: recibe nombre, categoría, precio y
   stock. PATCH (`/api/productos/{id}/precio?valor=…`) modifica solo un atributo (el precio) y
   deja el resto intacto.

5. **¿Por qué almacenar datos en memoria no es suficiente para una aplicación real?**
   Los datos se pierden al reiniciar la aplicación, no se comparten entre varias instancias, no
   hay transacciones ni consultas complejas, y la memoria del proceso limita el volumen de datos.

6. **¿Qué componente debería reemplazar el almacenamiento en memoria durante la semana 6?**
   Un repositorio de Spring Data JPA (`JpaRepository`) con Hibernate como implementación de JPA,
   persistiendo en una base de datos MySQL; `Producto` pasará a ser una `@Entity`.
