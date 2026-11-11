import {
  AbstractControl,
  ValidationErrors,
  ValidatorFn
} from '@angular/forms';

export function edadMinimaValidator(minima: number): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value;
    // Validators.required será responsable del valor vacío.
    if (valor === null || valor === '') {
      return null;
    }
    const edad = Number(valor);
    if (Number.isNaN(edad) || edad < minima) {
      return {
        edadMinima: {
          minima,
          actual: valor
        }
      };
    }
    return null;
  };
}

export const passwordsIgualesValidator: ValidatorFn =
  (grupo: AbstractControl): ValidationErrors | null => {
    const password = grupo.get('password')?.value;
    const confirmar = grupo.get('confirmarPassword')?.value;
    if (!password || !confirmar) {
      return null;
    }
    return password === confirmar
      ? null
      : { passwordsNoCoinciden: true };
  };

/**
 * Actividad Complementaria 1: Validador de DNI peruano
 * Debe aceptar exactamente 8 dígitos numéricos.
 */
export const dniPeruanoValidator: ValidatorFn =
  (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value;
    if (valor === null || valor === '') {
      return null;
    }
    const regexDni = /^\d{8}$/;
    return regexDni.test(String(valor).trim())
      ? null
      : { dniInvalido: true };
  };

/**
 * Actividad Complementaria 2: Validador personalizado de correo institucional
 * Solo permite correos con dominio @universidad.edu.pe
 */
export const correoInstitucionalValidator: ValidatorFn =
  (control: AbstractControl): ValidationErrors | null => {
    const valor = control.value;
    if (valor === null || valor === '') {
      return null;
    }
    const email = String(valor).toLowerCase().trim();
    return email.endsWith('@universidad.edu.pe')
      ? null
      : { dominioInvalido: true };
  };
