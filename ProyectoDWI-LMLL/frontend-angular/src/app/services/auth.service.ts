import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegistroRequest, UsuarioInfo } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly USER_KEY = 'auth_user';

  currentUser = signal<UsuarioInfo | null>(this.cargarUsuarioDeStorage());

  isLoggedIn = computed(() => !!this.currentUser());
  isAdmin = computed(() => this.currentUser()?.roles.includes('ROLE_ADMIN') ?? false);
  isVendedor = computed(() => this.currentUser()?.roles.includes('ROLE_VENDEDOR') ?? false);

  login(credenciales: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/login', credenciales).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

  registro(datos: RegistroRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('/api/auth/registro', datos).pipe(
      tap(res => this.guardarSesion(res))
    );
  }

  obtenerPerfil(): Observable<any> {
    return this.http.get<any>('/api/auth/me');
  }

  logout(): void {
    try {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    } catch (e) {
      console.warn(e);
    }
    this.currentUser.set(null);
  }

  getToken(): string | null {
    try {
      return localStorage.getItem(this.TOKEN_KEY);
    } catch {
      return null;
    }
  }

  private guardarSesion(res: AuthResponse): void {
    try {
      localStorage.setItem(this.TOKEN_KEY, res.token);
      const user: UsuarioInfo = {
        nombre: res.username,
        email: res.email,
        roles: res.roles || []
      };
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
      this.currentUser.set(user);
    } catch (e) {
      console.warn('Error guardando sesión:', e);
    }
  }

  private cargarUsuarioDeStorage(): UsuarioInfo | null {
    try {
      const data = localStorage.getItem(this.USER_KEY);
      if (data) {
        return JSON.parse(data);
      }
    } catch {
      return null;
    }
    return null;
  }
}
