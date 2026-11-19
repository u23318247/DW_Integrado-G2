export interface UsuarioSesion {
  id: number;
  nombre: string;
  email: string;
  rol: 'ROLE_ADMIN' | 'ROLE_CLIENTE' | 'ROLE_VENDEDOR';
}

export interface AuthResponse {
  token: string;
  tipo: string;
  usuario: UsuarioSesion;
}
