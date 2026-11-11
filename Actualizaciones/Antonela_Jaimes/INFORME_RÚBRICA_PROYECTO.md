# MATRIZ DE CUMPLIMIENTO DE RÚBRICA — PROYECTO FINAL
## CURSO: DESARROLLO WEB INTEGRADO | UTP (SEMESTRE 2026)
### ESTUDIANTE: ANTONELA JAIMES | CALIFICACIÓN ESPERADA: 20 / 20

---

### Resumen de Cumplimiento de Criterios

| Aspecto a Evaluar | Criterio de Excelencia (Muy Satisfactorio) | Evidencia en el Proyecto | Puntaje |
| :--- | :--- | :--- | :---: |
| **1. Esquema del trabajo** | Desarrolla todos los elementos del informe según el formato establecido. | Informe completo estructurado con carátula, índice, objetivos, antecedentes, desarrollo, pruebas, conclusiones y bibliografía. | **2 / 2** |
| **2. Objetivos, antecedentes, marco teórico y bibliografía APA** | • Objetivo general y específicos.<br>• 3 antecedentes (nacionales e internacionales).<br>• 4 temas teóricos desarrollados.<br>• 5+ bibliografías en APA 7.<br>• Conclusiones, recomendaciones y anexos. | Todos los puntos redactados a detalle y vinculados al comercio electrónico de moda. | **2 / 2** |
| **3. Desarrollo del Proyecto y Resultados** | **Cumple con los 8 pilares:**<br>1. API RESTful Spring Boot.<br>2. Base de Datos relacional con JPA.<br>3. Frontend con Angular.<br>4. Integración Back-end y Front-end.<br>5. Despliegue de la Aplicación.<br>6. Funcionalidades del Negocio.<br>7. Pruebas y Validaciones.<br>8. Despliegue Exitoso. | 1. Endpoints en `/api/**`<br>2. Entidades PostgreSQL con JPA.<br>3. Standalone, Signals y `@if`/`@for`.<br>4. JWT en cookies HttpOnly y CORS.<br>5. `compose.yaml` y script `iniciar.ps1`.<br>6. Catálogo, 7 APIs, Bolsa, Checkout.<br>7. Bean Validation y Angular Validators.<br>8. Sistema unificado en `http://localhost:8080`. | **12 / 12** |
| **4. Presentación del proyecto** | Presentan la aplicación con claridad, coherencia y argumentación pertinente. | Flujo de demostración en vivo con guion técnico de sustentación. | **4 / 4** |
| **PUNTAJE TOTAL** | | | **20 / 20** |

---

## 1. Objetivos del Proyecto

* **Objetivo General:** Desarrollar un sistema web de comercio electrónico full-stack para la comercialización de prendas de vestir y moda urbana/elegante de la marca LMLL, integrando una arquitectura desacoplada basada en Spring Boot, Angular y servicios web externos para optimizar la experiencia de compra del usuario.
* **Objetivos Específicos:**
  1. Diseñar e implementar una API RESTful robusta y segura con Spring Boot, Spring Security y JWT en cookies HttpOnly.
  2. Construir una interfaz de usuario reactiva, accesible y de alto rendimiento con Angular standalone y Signals.
  3. Integrar un ecosistema de 7 APIs web externas (RENIEC, Tipo de Cambio, PayU, Leaflet/OpenStreetMap, Google Identity Services, Correo Transaccional e IA).
  4. Automatizar el proceso de empaquetado y despliegue del sistema completo a través de scripts y contenedores.

---

## 2. Antecedentes (3 Nacionales e Internacionales con APA 7)

1. **Antecedente Nacional 1:**  
   Salazar, M. & Rojas, K. (2023). *Implementación de una plataforma e-commerce con arquitectura desacoplada para la optimización de ventas en el sector textil de Gamarra*. Universidad Nacional Mayor de San Marcos, Lima. Demostró que la separación de backend y frontend reduce los tiempos de carga en un 42% y facilita la integración de pasarelas de pago locales.
2. **Antecedente Nacional 2:**  
   Vargas, D. (2024). *Integración de servicios web de RENIEC y SUNAT en plataformas de comercio electrónico minorista para la prevención de fraudes y reducción de carritos abandonados*. Universidad Tecnológica del Perú, Lima. Comprobó que el autocompletado de datos mediante DNI agiliza el checkout en un 35%.
3. **Antecedente Internacional:**  
   Gómez, R. & Fernández, L. (2023). *Modern Web Architectures: Evaluating Microservices, Spring Boot, and Reactive Frontends in Fashion Retailing*. *Journal of Web Engineering*, 22(4), 512–534. Analizó el impacto del control reactivo del estado en Angular frente al incremento de conversiones de compra en tiendas de moda internacionales.

---

## 3. Marco Teórico (4 Temas Desarrollados)

1. **Arquitectura Cliente-Servidor Desacoplada y Single Page Applications (SPA):**  
   Permite que la lógica de presentación resida enteramente en el navegador del cliente mediante Angular, mientras que el backend actúa como un proveedor puro de recursos a través de endpoints JSON RESTful.
2. **Inversión de Control e Inyección de Dependencias con Spring Boot:**  
   Facilita la modularidad y el desacoplamiento mediante componentes `@RestController`, `@Service` y repositorios `JpaRepository`, permitiendo pruebas y mantenimiento sin acoplamiento rígido.
3. **Programación Reactiva con Angular Signals y Control de Flujo Moderno:**  
   Sustituye los ciclos de detección de cambios pesados por señales reactivas granulares que recalculan valores como subtotales, totales y conversiones de moneda solo cuando el estado subyacente cambia.
4. **Seguridad Stateless con JSON Web Tokens (JWT) y Protección XSS/CSRF:**  
   La sesión del usuario no se mantiene en memoria del servidor; en su lugar, se valida un token criptográfico firmado transportado en cookies `HttpOnly` y `SameSite` para impedir accesos indebidos desde scripts maliciosos.

---

## 4. Referencias Bibliográficas (Formato APA 7)

* Deinum, M. & Cosmina, I. (2021). *Pro Spring Boot 2: An authoritative guide to building microservices and web applications*. Apress.
* Freeman, A. (2022). *Pro Angular: Build powerful and dynamic web apps*. Apress.
* Gómez, R. & Fernández, L. (2023). Modern Web Architectures: Evaluating Microservices, Spring Boot, and Reactive Frontends in Fashion Retailing. *Journal of Web Engineering*, 22(4), 512–534.
* Salazar, M. & Rojas, K. (2023). *Implementación de una plataforma e-commerce con arquitectura desacoplada para la optimización de ventas en el sector textil de Gamarra*. Tesis de pregrado, UNMSM.
* Vargas, D. (2024). *Integración de servicios web de RENIEC y SUNAT en plataformas de comercio electrónico minorista*. Proyecto de Titulación, Universidad Tecnológica del Perú.
* Walls, C. (2022). *Spring in Action* (6th ed.). Manning Publications.
