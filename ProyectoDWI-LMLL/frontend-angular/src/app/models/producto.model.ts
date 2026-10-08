export interface Producto {
  id?: number;
  nombre: string;
  genero: string;
  imagenUrl: string;
  precioBase: number;
  disponibilidad: boolean;
  categoriaId: number;
  categoriaNombre?: string;
}

export interface Categoria {
  id: number;
  nombre: string;
}
