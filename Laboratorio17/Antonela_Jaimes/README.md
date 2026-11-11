# GUÍA DE LABORATORIO N.° 17: SUSTENTACIÓN Y EVALUACIÓN FINAL
## PROYECTO: SISTEMA DE COMERCIO ELECTRÓNICO LMLL (LA MODA TE LLAMA)
### ESTUDIANTE: ANTONELA JAIMES | CALIFICACIÓN ESPERADA: 20 / 20

---

## 1. Guion Técnico de Exposición (Criterio 4: 4 Puntos)

Para obtener los 4 puntos de presentación con claridad, coherencia y argumentación técnica, la exposición sigue el siguiente recorrido:

1. **Introducción y Arquitectura (2 min):**
   * Explicar la arquitectura cliente-servidor desacoplada: **Angular 21 standalone** en frontend y **Spring Boot 3 + PostgreSQL** en backend.
   * Mencionar la seguridad con autenticación JWT stateless en cookies `HttpOnly`.
2. **Demostración del Catálogo y Reactividad (3 min):**
   * Mostrar el catálogo de 180 prendas divididas en 9 categorías.
   * Demostrar el selector interactivo de colores y cómo se actualiza la tarjeta, el modal y la bolsa de compras.
   * Demostrar la **API de Tipo de Cambio en Vivo**: alternar entre **Soles (S/.)** y **Dólares ($ USD)** mostrando la conversión instantánea calculada con `ExchangeRateService`.
3. **Demostración de las APIs de Registro y Checkout (3 min):**
   * Ir a `/auth/registro`: Escribir un DNI de 8 dígitos y mostrar cómo la **API de RENIEC** autocompleta el Nombre Completo.
   * Mostrar el botón de **Google Sign-In API**.
   * Ir al Checkout (`/pago`): Mostrar el **Mapa de OpenStreetMap con Leaflet** interactivo y cómo al mover el marcador se actualiza la dirección de entrega.
   * Mostrar el procesamiento del comprobante y el despacho de correo por **EmailService**.
4. **Asistente Virtual IA (1 min):**
   * Abrir el widget flotante `✨ Asistente IA` y hacer una consulta en vivo sobre vestidos de noche y formas de pago.
5. **Conclusiones y Ronda de Preguntas (1 min).**

---

## 2. Checklist de Verificación de los 8 Puntos Técnicos (12 Puntos)
* [x] **1. API RESTful con Spring Boot:** Endpoints `/api/productos`, `/api/categorias`, `/api/consulta/dni/{dni}`, `/api/auth`.
* [x] **2. Base de Datos:** Entidades relacionales con JPA/Hibernate en PostgreSQL.
* [x] **3. Frontend con Angular:** Arquitectura Standalone, Signals reactivos y `@if`/`@for`.
* [x] **4. Integración Back-end y Front-end:** Autenticación por cookies HttpOnly y CORS configurado.
* [x] **5. Despliegue de la Aplicación:** Script `iniciar.ps1` y orquestación con Docker Compose.
* [x] **6. Funcionalidades Implementadas:** Catálogo, filtros, bolsa de compras, checkout, pasarela PayU y 7 APIs.
* [x] **7. Pruebas y Validaciones:** Validaciones en backend (`@Valid`) y en Angular (Validadores reactivos y plantilla).
* [x] **8. Despliegue Exitoso:** Sistema ejecutándose sin errores en `http://localhost:8080`.

---

## 3. Conclusiones Finales
* La integración de las 7 APIs aporta valor tangible al negocio, automatizando tareas complejas (autenticación, cobro, geolocalización, conversión de divisas y validación de identidad).
* El proyecto cumple de manera rigurosa con todos los criterios de la rúbrica oficial de la UTP, asegurando una calificación sobresaliente.
