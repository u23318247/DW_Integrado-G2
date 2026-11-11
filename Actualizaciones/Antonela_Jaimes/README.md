# BITÁCORA DE ACTUALIZACIONES TÉCNICAS
## PROYECTO: SISTEMA DE COMERCIO ELECTRÓNICO LMLL (LA MODA TE LLAMA)
### ESTUDIANTE: ANTONELA JAIMES | CURSO: DESARROLLO WEB INTEGRADO

---

## 📌 Resumen de Actualizaciones Implementadas

En este repositorio se han centralizado las mejoras, optimizaciones y nuevas integraciones desarrolladas para el proyecto final y los laboratorios guiados:

### 1. Actualización del Catálogo y Sistema de Prendas
* **Transformación Integral del Inventario:** 180 prendas modeladas con alta fidelidad en 9 categorías de moda (20 productos por categoría).
* **Selector Dinámico de Colores:** Cada prenda cuenta con una paleta de 6 a 7 colores elegantes con renderizado visual de muestras interactivas (*swatches*), sincronizadas en tiempo real con la tarjeta, la bolsa de compras y el modal de detalle.
* **Filtros Avanzados:** Filtrado instantáneo por Género (`Todos`, `Hombre`, `Mujer`, `Unisex`), búsqueda textual y ordenamiento por relevancia y precio.

### 2. Integración de las 7 APIs Externas Principales
1. **API RENIEC / DNI (Perú):** Endpoint `/api/consulta/dni/{dni}` conectado al servicio nacional para autocompletar nombres y apellidos en el registro (`registro.html`) y en el checkout (`pago.html`).
2. **API de Tipo de Cambio en Vivo:** Consumo reactivo de `open.er-api.com` para mostrar cotización oficial y permitir al usuario alternar entre **Soles (S/.)** y **Dólares ($ USD)** en toda la tienda.
3. **Pasarela de Pagos (PayU Latam Sandbox):** Procesamiento de cobros con generación de firmas criptográficas MD5 y soporte para tarjetas de crédito/débito y pagos en efectivo.
4. **Asistente Virtual IA (Chatbot de Moda):** Widget flotante interactivo con respuestas contextuales para guiar al cliente en tallas, prendas de gala y envíos.
5. **Mapas y Geolocalización (OpenStreetMap + Leaflet):** Mapa interactivo de Lima que permite fijar el punto de entrega y geocodifica la dirección automáticamente.
6. **Autenticación Social (Google Identity Services):** Inicio de sesión con un solo clic con Google OAuth2 en `login.html`.
7. **API de Notificaciones por Correo:** Servicio transaccional que despacha comprobantes electrónicos en HTML con PIN de entrega para delivery.

### 3. Formularios en Angular (Laboratorio 13)
* Implementación de formularios basados en plantillas (`Template-driven`) con directivas declarativas.
* Implementación de formularios reactivos (`Reactive forms`) con `FormBuilder`, validadores personalizados (`edadMinimaValidator`, `passwordsIgualesValidator`, `dniPeruanoValidator`, `correoInstitucionalValidator`) y colecciones dinámicas con `FormArray` para habilidades.

---

## 📂 Archivos y Evidencias Asociadas
* [`CHANGELOG.md`](./CHANGELOG.md): Registro cronológico de versiones y cambios por fecha.
