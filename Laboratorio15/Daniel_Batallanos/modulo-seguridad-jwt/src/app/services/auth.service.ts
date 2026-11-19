import { Injectable, signal, computed, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { AuthResponse, UsuarioSesion } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  private readonly TOKEN_KEY = 'jwt_auth_token';
  private readonly USER_KEY = 'jwt_auth_user';

  usuarioActual = signal<UsuarioSesion | null>(this.obtenerUsuarioAlmacenado());
  token = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));

  estaAutenticado = computed(() => !!this.token());
  esAdmin = computed(() => this.usuarioActual()?.rol === 'ROLE_ADMIN');

  login(credenciales: { email: string; password: string }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('http://localhost:8080/api/auth/login', credenciales).pipe(
      tap((res) => {
        this.guardarSesion(res.token, res.usuario);
      })
    );
  }

  guardarSesion(token: string, usuario: UsuarioSesion): void {
    localStorage.setItem(this.TOKEN_KEY, token);
    localStorage.setItem(this.USER_KEY, JSON.stringify(usuario));
    this.token.set(token);
    this.usuarioActual.set(usuario);
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.token.set(null);
    this.usuarioActual.set(null);
    this.router.navigate(['/login']);
  }

  private obtenerUsuarioAlmacenado(): UsuarioSesion | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }
}
