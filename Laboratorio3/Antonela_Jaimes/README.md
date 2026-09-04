# GUÍA DE LABORATORIO N.° 03: Test-Driven Development (TDD) e Integración de TDD en Spring Boot

- **Curso:** Desarrollo Web Integrado (100000ST61)
- **Semana:** 3
- **Estudiante:** Antonela Jaimes
- **Unidad:** Unidad 1: API REST
- **Herramientas:** Visual Studio Code, Java 21/25, Maven, Spring Boot 3, JUnit 5, Mockito, MockMvc y Postman

---

## 📌 1. Presentación y Logro de Aprendizaje

Al finalizar el laboratorio, el estudiante implementa funcionalidades de una API REST en Spring Boot aplicando el ciclo **Red-Green-Refactor**, mediante pruebas unitarias con JUnit 5, simulación de dependencias con Mockito y pruebas de controlador con MockMvc.

---

## 🔄 2. Fundamento Metodológico: Ciclo TDD

| Fase | Qué se hace | Resultado esperado |
| :---: | :--- | :--- |
| **RED** | Se escribe una prueba automatizada que inicialmente falla. | La prueba falla porque la funcionalidad aún no existe o no cumple la regla de negocio. |
| **GREEN** | Se implementa el código mínimo necesario para satisfacer la prueba. | La prueba pasa satisfactoriamente. |
| **REFACTOR** | Se mejora el código, nombres, diseño y duplicaciones sin alterar el comportamiento. | Todas las pruebas siguen pasando exitosamente después de la refactorización. |

---

## 📋 3. Reglas de Negocio Implementadas

1. **Registrar producto:** Asigna un identificador numérico automático (`id`) y almacena el producto.
2. **Validar producto:**
   - Nombre obligatorio (no `null` ni vacío/en blanco).
   - Precio mayor que cero (`precio > 0`).
   - Stock no negativo (`stock >= 0`).
3. **Listar productos:** Retorna la lista de todos los productos registrados.
4. **Buscar por ID:** Retorna el producto si existe (`200 OK`) o `404 Not Found` si no existe.
5. **Eliminar producto (Ejercicio comp. 2):** Elimina el producto existente retornando `204 No Content`, o `404 Not Found` si no existe.
6. **Actualizar producto (Ejercicio comp. 3):** Endpoint `PUT /productos/{id}` que valida existencia y mantiene reglas de precio y stock.

---

## 🎯 4. Ejercicios Complementarios Implementados

* **Ejercicio 1: Buscar producto existente**
  Se agregaron pruebas unitarias y de integración que validan que al consultar `GET /productos/{id}` de un producto existente se retorna el código `200 OK` y el JSON correspondiente.
* **Ejercicio 2: Eliminar producto**
  Se implementó `eliminar(Long id)` en `ProductoService` y el endpoint `DELETE /productos/{id}` en `ProductoController` retornando `204 No Content`.
* **Ejercicio 3: Actualización de producto**
  Se implementó `actualizar(Long id, Producto datos)` en `ProductoService` y el endpoint `PUT /productos/{id}` en `ProductoController`, validando que no se actualicen productos inexistentes (`404`) y que se cumplan las reglas de validación de precio y stock.

---

## 💡 5. Respuestas a las Preguntas de Reflexión (Página 14)

### 1. ¿Por qué TDD no es lo mismo que escribir pruebas después de terminar el código?
En TDD, las pruebas conducen y guían el diseño de la solución antes de escribir la implementación. Escribir pruebas a posteriori suele estar sesgado a validar cómo quedó escrito el código (verificando la implementación en vez de los requerimientos) y genera acoplamiento, mientras que TDD garantiza que el código sea testeable, modular y solo contenga lo estrictamente necesario.

### 2. ¿Qué representa la fase RED?
Representa la verificación inicial de que la prueba falla por el motivo correcto (porque la funcionalidad aún no existe o la regla aún no se cumple). Esto asegura que la prueba realmente tiene capacidad de detectar errores y no pasa por accidente.

### 3. ¿Por qué no debe saltarse la fase REFACTOR?
Porque la fase GREEN solo busca la solución mínima necesaria, la cual muchas veces contiene código duplicado o mejorable. La fase REFACTOR permite limpiar el diseño, mejorar la legibilidad y aplicar buenas prácticas con la red de seguridad de que las pruebas automatizadas vigilan que nada se rompa.

### 4. ¿Qué diferencia hay entre probar `ProductoService` y `ProductoController`?
* Probar `ProductoService`: Es una prueba unitaria pura enfocada en la lógica de negocio y reglas de validación sin involucrar el ciclo de vida HTTP.
* Probar `ProductoController`: Es una prueba de integración web con `MockMvc` enfocada en la capa de transporte: mapeo de URLs, códigos de estado HTTP (200, 201, 204, 404), serialización y deserialización de JSON.

### 5. ¿Para qué sirve `MockMvc`?
Permite simular peticiones HTTP y verificar las respuestas del controlador REST sin necesidad de levantar un servidor web real (como Tomcat), lo que hace que las pruebas sean extremadamente rápidas y ligeras.

### 6. ¿Por qué en `@WebMvcTest` se usa un mock del servicio?
Porque el propósito de `@WebMvcTest` es aislar la prueba exclusivamente a la capa web del controlador. Simular el servicio permite controlar los escenarios de respuesta (éxito, listas vacías, excepciones) sin depender del estado ni del comportamiento real del servicio.

### 7. ¿Qué error se genera cuando Angular o Postman envía un JSON con tipos incorrectos?
Spring Boot genera un error `400 Bad Request` debido a un fallo de deserialización con Jackson (`HttpMessageNotReadableException`), al no poder convertir los tipos enviados (por ejemplo un String en un campo numérico double o int).

### 8. ¿Cómo ayuda TDD a mejorar el diseño de una API REST?
Obliga al desarrollador a ponerse en el lugar del consumidor de la API antes de programar, diseñando contratos de endpoints claros, métodos HTTP semánticos, payloads limpios y respuestas con códigos de estado adecuados.

---

## 🧪 6. Comandos de Ejecución

```bash
# Compilar y ejecutar pruebas automatizadas
./mvnw clean test

# Iniciar la aplicación
./mvnw spring-boot:run
```
