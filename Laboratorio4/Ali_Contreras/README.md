# Ali_Contreras — Guía de Laboratorio 04

**Curso:** Desarrollo Web Integrado (100000ST61) — Semana 4
**Proyecto:** API REST — Catálogo de Productos (Spring Boot, JAR)

## Cómo ejecutar

1. Abrir la carpeta `Ali_Contreras` en Visual Studio Code (File > Open Folder).
2. Esperar a que VS Code descargue las dependencias de Maven.
3. En la terminal integrada:

```bash
mvn spring-boot:run
```

La API queda disponible en: http://localhost:8080/api/productos

## Endpoints

| Método | URL | Descripción | Respuesta |
|--------|-----|-------------|-----------|
| GET | /api/productos | Listar todos los productos | 200 |
| GET | /api/productos?categoria=Tecnologia | Filtrar por categoría | 200 |
| GET | /api/productos/{id} | Buscar por id | 200 / 404 |
| POST | /api/productos | Crear producto | 201 / 400 |
| PUT | /api/productos/{id} | Actualización completa | 200 / 400 / 404 |
| PATCH | /api/productos/{id}/stock | Actualizar solo stock | 200 / 400 / 404 |
| DELETE | /api/productos/{id} | Eliminar producto | 204 / 404 |

## Pruebas y empaquetado

```bash
mvn test
mvn clean package
java -jar target/semana4-api-rest-0.0.1-SNAPSHOT.jar
```
