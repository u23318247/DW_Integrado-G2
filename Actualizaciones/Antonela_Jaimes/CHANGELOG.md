# CHANGELOG — HISTORIAL DE VERSIONES
## Proyecto LMLL (La Moda te LLama) — Antonela Jaimes

Todas las modificaciones notables realizadas en este proyecto se documentan en este archivo según el estándar [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/).

---

## [2.5.0] - 2026-11-11
### Agregado
* **Google Identity Services API:** Integración de inicio de sesión con Google en `login.html`.
* **OpenStreetMap & Leaflet API:** Mapa interactivo en `pago.html` con marcador arrastrable y geocodificación inversa para fijar la dirección de despacho.
* **Email Service HTML:** Formato visual de ticket de compra con PIN de delivery y llamada a webhook de auditoría.
* **API de Tipo de Cambio en Vivo:** Servicio `ExchangeRateService` en Angular con conmutador dinámico Soles/Dólares en tiempo real.
* **Chatbot de Moda (IA):** Componente `AiAssistant` con recomendaciones inteligentes de ropa y tallas.
* **API RENIEC en Registro:** Campo DNI con autocompletado de nombre completo en `registro.html`.

## [2.1.0] - 2026-11-05
### Agregado
* **Laboratorio 13 Completo:** Implementación de vistas `/plantilla` y `/reactivo`.
* Validadores personalizados para DNI peruano y correo institucional `@universidad.edu.pe`.
* Manejo dinámico de colecciones ordenadas mediante `FormArray` para habilidades.

## [2.0.0] - 2026-11-01
### Modificado
* Transformación de los 180 productos a moda y sastrería elegante (20 prendas por cada una de las 9 categorías).
* Selector dinámico de 6 a 7 colores elegantes por cada prenda con muestras interactivas sincronizadas con la bolsa de compras.

## [1.0.0] - 2026-10-17
### Agregado
* Configuración inicial del proyecto full-stack unificado (Angular 21 + Spring Boot 3 + PostgreSQL).
* Módulos de autenticación JWT y roles de usuario (`ROLE_ADMIN`, `ROLE_CLIENTE`).
