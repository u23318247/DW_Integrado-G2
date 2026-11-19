import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  cargando = signal<boolean>(false);
  mensajeError = signal<string>('');

  formulario = this.fb.nonNullable.group({
    email: ['admin@tienda.com', [Validators.required, Validators.email]],
    password: ['Admin123!', [Validators.required, Validators.minLength(6)]]
  });

  ingresar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.mensajeError.set('');

    const cred = this.formulario.getRawValue();
    // Simulación o llamada real
    setTimeout(() => {
      if (cred.email === 'admin@tienda.com' && cred.password === 'Admin123!') {
        this.authService.guardarSesion('fake-jwt-token-xyz-1234567890', {
          id: 1,
          nombre: 'Antonela Jaimes (Admin)',
          email: cred.email,
          rol: 'ROLE_ADMIN'
        });
        this.cargando.set(false);
        this.router.navigate(['/panel']);
      } else {
        this.cargando.set(false);
        this.mensajeError.set('Credenciales inválidas. Compruebe su correo o contraseña.');
      }
    }, 500);
  }
}
