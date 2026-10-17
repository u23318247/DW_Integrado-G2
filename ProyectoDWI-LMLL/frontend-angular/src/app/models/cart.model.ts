export interface CartItem {
  id?: number;
  productoId?: number;
  sku?: string;
  nombre: string;
  precio: number;
  cantidad: number;
  imagen: string;
  talla?: string;
  color?: string;
  marca?: string;
  stockMaximo?: number;
}
