export interface Producto {
  id: number;
  nombre: string;
  precio: number;
  stock: number;
  fechaRegistro: string;
}

export interface ProductoRequest {
  nombre: string;
  precio: number;
  stock: number;
  fechaRegistro: string;
}

export interface ErrorResponse {
  fecha: string;
  estado: number;
  error: string;
  mensaje: string;
  ruta: string;
}
