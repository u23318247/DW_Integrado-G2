# Laboratorio 3: TDD con JUnit 5, Mockito y MockMvc

- **Curso:** Desarrollo Web Integrado
- **Semana:** 3
- **Estudiante:** Antonela Jaimes
- **Tema:** Desarrollo Guiado por Pruebas (TDD), pruebas unitarias con JUnit 5 y pruebas de controladores con MockMvc y Mockito.

---

## 📌 Contenido del Laboratorio

1. **Modelo:** `Producto` (id, nombre, precio, stock).
2. **Servicio (`ProductoService`):** Lógica de negocio con validaciones de campos obligatorios, precio mayor a cero y stock no negativo.
3. **Controlador (`ProductoController`):** Endpoints REST para `/productos`:
   - `GET /productos`: Listar productos.
   - `GET /productos/{id}`: Buscar producto por id (200 / 404).
   - `POST /productos`: Registrar producto (201 Created).
   - `DELETE /productos/{id}`: Eliminar producto (204 No Content / 404).
4. **Pruebas Automatizadas:**
   - Pruebas unitarias de servicio con **JUnit 5** y **AssertJ**.
   - Pruebas de integración web con **MockMvc** y **Mockito**.

---

## 🧪 Comandos de Ejecución

```bash
# Ejecutar todas las pruebas
./mvnw test

# Iniciar la aplicación
./mvnw spring-boot:run
```
