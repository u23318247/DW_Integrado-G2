export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegistroRequest {
  nombre: string;
  email: string;
  password: string;
  telefono?: string;
  direccion?: string;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  username: string;
  email: string;
  roles: string[];
}

export interface UsuarioInfo {
  nombre: string;
  email: string;
  roles: string[];
}
