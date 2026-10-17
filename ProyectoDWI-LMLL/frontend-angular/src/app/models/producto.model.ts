export interface ProductoDetalle {
  id: number;
  sku: string;
  nombreCompleto: string;
  imagenUrl: string;
  marca: string;
  talla: string;
  color: string;
  descripcion: string;
  stock: number;
  precioBase: number;
  precioAdicional: number;
  precioTotal: number;
}

export interface Producto {
  id?: number;
  nombre: string;
  genero: string;
  imagenUrl: string;
  precioBase: number;
  disponibilidad: boolean;
  categoriaId: number;
  categoriaNombre?: string;
  tallas?: string[];
  colores?: string[];
  variantes?: ProductoDetalle[];
}

export interface Categoria {
  id: number;
  nombre: string;
}
