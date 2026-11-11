# UNIVERSIDAD TECNOLÓGICA DEL PERÚ (UTP)
## FACULTAD DE INGENIERÍA DE SISTEMAS E INFORMÁTICA
### CURSO: DESARROLLO WEB INTEGRADO

---

# GUÍA DE LABORATORIO N.° 13: FORMULARIOS EN ANGULAR
**Formularios basados en plantillas, formularios reactivos, validaciones y controles dinámicos**

* **Estudiante:** Antonela Jaimes
* **Semana:** 13
* **Tecnologías:** Angular 21/22, TypeScript 5.9/6, HTML5, CSS3
* **Herramienta:** Visual Studio Code
* **Modalidad:** Laboratorio guiado paso a paso con actividades complementarias

---

## 1. Resumen Ejecutivo y Logro de Aprendizaje

En este laboratorio se desarrollaron dos enfoques principales de formularios en **Angular**:
1. **Formularios basados en plantillas (`Template-driven forms`):** Utilizando directivas declarativas (`ngModel`, `#contactoForm="ngForm"`), sincronización bidireccional asíncrona y validaciones en la plantilla HTML.
2. **Formularios reactivos (`Reactive forms`):** Utilizando programación reactiva con `FormBuilder`, controles fuertemente tipados (`nonNullable`), validadores síncronos y asíncronos (`ValidatorFn`), validación a nivel de grupo (`passwordsIgualesValidator`), y colecciones dinámicas ordenadas con `FormArray` para habilidades.

Adicionalmente, se implementaron las **4 Actividades Complementarias** requeridas en la guía:
* **Actividad 1:** Validador personalizado para DNI peruano (8 dígitos numéricos).
* **Actividad 2:** Validador para correo institucional (`@universidad.edu.pe`).
* **Actividad 3:** Arquitectura de `FormArray` para direcciones dinámicas compuestas (`departamento`, `provincia`, `direccion`).
* **Actividad 4:** Integración con backend Spring Boot (`HttpClient`, método `POST` y sincronización de validaciones).

---

## 2. Estructura del Proyecto

```text
Laboratorio13/Antonela_Jaimes/
├── README.md                                  <- Informe completo del laboratorio
└── laboratorio-formularios/                   <- Proyecto Angular standalone
    ├── angular.json
    ├── package.json
    ├── tsconfig.json
    ├── src/
    │   ├── index.html
    │   ├── main.ts
    │   ├── styles.css                         <- Estilos globales y reset
    │   └── app/
    │       ├── app.routes.ts                  <- Carga perezosa (loadComponent)
    │       ├── app.ts                         <- Componente raíz standalone
    │       ├── app.html                       <- Cabecera y router-outlet
    │       ├── app.css                        <- Navegación responsiva
    │       ├── validators/
    │       │   └── registro.validators.ts     <- edadMinima, passwords, DNI, correo
    │       └── pages/
    │           ├── formulario-plantilla/
    │           │   ├── formulario-plantilla.ts
    │           │   ├── formulario-plantilla.html
    │           │   └── formulario-plantilla.css
    │           └── formulario-reactivo/
    │               ├── formulario-reactivo.ts
    │               ├── formulario-reactivo.html
    │               └── formulario-reactivo.css
```

---

## 3. Puntos de Control y Respuestas Teóricas

### Punto de Control 1: Inicialización
* **Pregunta:** ¿Qué ventaja ofrece la bandera `--file-name-style-guide=2025` y el modelo standalone en Angular moderno?
* **Respuesta:** Permite nombrar los archivos de forma concisa (`formulario-plantilla.ts`, `app.ts`) sin redundancias como `.component.ts`, además de prescindir de módulos `NgModule`, agilizando la compilación mediante *tree-shaking* y habilitando la carga diferida (`loadComponent`).

### Punto de Control 2: Navegación y Rutas
* **Configuración de rutas:** Se configuró `src/app/app.routes.ts` con redirección por defecto `redirectTo: 'plantilla'` y ruta comodín `**`. La carga se realiza mediante funciones asíncronas `import().then()`, optimizando el peso del paquete inicial descargado por el navegador.

### Punto de Control 3: Formulario Basado en Plantillas
* **Mecanismo de validación:** Los errores solo se renderizan cuando el campo está tocado (`touched`) o cuando el formulario ha sido enviado (`contactoForm.submitted`).
* **Atributo `name`:** Cada control con `ngModel` requiere un atributo `name` único para registrarse internamente en la instancia de `NgForm`.
* **Depuración:** El bloque `<details class="depuracion">` refleja en tiempo real las propiedades booleanas `valid`, `touched` y `dirty`, así como el objeto `modelo` formateado con `JsonPipe`.

### Punto de Control 4: Formulario Reactivo y `FormArray`
* **Validación de grupo vs. campo:**
  * `edadMinimaValidator(18)` se aplica a un único `FormControl` (`edad`).
  * `passwordsIgualesValidator` se aplica a nivel de `FormGroup`, ya que requiere comparar dos controles independientes (`password` y `confirmarPassword`). Por esta razón, el error `passwordsNoCoinciden` se consulta desde `registroForm.hasError(...)`.
* **Manejo dinámico con `FormArray`:** Permite añadir nuevas instancias de control mediante `this.habilidades.push(...)` y eliminarlas con `this.habilidades.removeAt(indice)`, deshabilitando el botón de eliminación cuando la longitud es 1 para preservar al menos un campo obligatorio.

---

## 4. Actividades Complementarias Desarrolladas

### Actividad 1: Validador de DNI Peruano (8 dígitos)
Implementado en `registro.validators.ts`:
```typescript
export const dniPeruanoValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const valor = control.value;
  if (valor === null || valor === '') return null;
  const regexDni = /^\d{8}$/;
  return regexDni.test(String(valor).trim()) ? null : { dniInvalido: true };
};
```

### Actividad 2: Validador de Correo Institucional
Implementado en `registro.validators.ts`:
```typescript
export const correoInstitucionalValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const valor = control.value;
  if (valor === null || valor === '') return null;
  const email = String(valor).toLowerCase().trim();
  return email.endsWith('@universidad.edu.pe') ? null : { dominioInvalido: true };
};
```

### Actividad 3: Direcciones Dinámicas con `FormArray`
Permite gestionar múltiples direcciones estructuradas:
```typescript
crearGrupoDireccion(): FormGroup {
  return this.fb.group({
    departamento: this.fb.nonNullable.control('', [Validators.required]),
    provincia: this.fb.nonNullable.control('', [Validators.required]),
    direccion: this.fb.nonNullable.control('', [Validators.required, Validators.minLength(5)])
  });
}
```

### Actividad 4: Conexión con Spring Boot (Full Stack)
Envío reactivo al backend mediante `HttpClient`:
```typescript
enviarABackend(): void {
  if (this.registroForm.invalid) return;
  const payload = this.registroForm.getRawValue();
  this.http.post('http://localhost:8080/api/usuarios/registro', payload).subscribe({
    next: (res) => console.log('Usuario registrado en Spring Boot:', res),
    error: (err) => console.error('Error al registrar:', err)
  });
}
```

---

## 5. Conclusiones

1. Los formularios basados en plantillas son ideales para formularios pequeños y sencillos como formularios de contacto o búsquedas rápidas, reduciendo código TypeScript.
2. Los formularios reactivos ofrecen control absoluto, inmutabilidad, mayor facilidad para pruebas unitarias automatizadas y soporte nativo para controles dinámicos complejos mediante `FormArray`.
3. La validación en el cliente en Angular mejora sustancialmente la experiencia del usuario (UX) previniendo envíos incompletos, pero siempre debe complementarse con validaciones en el backend (Spring Boot con `@Valid` y `Bean Validation`) por motivos de seguridad informática.

---
**Autor:** Antonela Jaimes  
**Curso:** Desarrollo Web Integrado — Semana 13  
**Universidad Tecnológica del Perú**
