# Laboratorio 2 - API REST de Productos

Curso: Desarrollo Web Integrado (100000ST61) - Semana 2

## Ejecutar

```
mvn spring-boot:run
```

La aplicacion levanta en http://localhost:8080

## Endpoints

| Metodo | Endpoint | Descripcion |
|--------|----------|-------------|
| GET | /api/productos | Lista todos los productos |
| GET | /api/productos/{id} | Busca un producto por id |
| GET | /api/productos/buscar?nombre=lap | Busca por coincidencia de nombre |
| GET | /api/productos/stock-bajo?limite=10 | Productos con stock menor o igual al limite |
| GET | /api/productos/precio-mayor?precio=100 | Productos con precio mayor al indicado |
| POST | /api/productos | Crea un producto |
| PUT | /api/productos/{id} | Actualiza un producto |
| DELETE | /api/productos/{id} | Elimina un producto (204 No Content) |

## Body JSON para POST y PUT

```json
{ "nombre": "Monitor LG", "precio": 850, "stock": 5 }
```

## Validaciones

- Nombre obligatorio y de al menos 3 caracteres
- Precio mayor que cero
- Stock no negativo

Los errores se devuelven de forma estandarizada mediante GlobalExceptionHandler.
