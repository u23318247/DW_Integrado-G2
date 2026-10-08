import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private http = inject(HttpClient);
  private apiUrl = '/api/productos';

  listarProductos(categoriaId?: number, texto?: string): Observable<Producto[]> {
    let url = this.apiUrl;
    const params: string[] = [];
    if (categoriaId) {
      params.push(`categoriaId=${categoriaId}`);
    }
    if (texto && texto.trim().length > 0) {
      params.push(`texto=${encodeURIComponent(texto.trim())}`);
    }
    if (params.length > 0) {
      url += `?${params.join('&')}`;
    }
    return this.http.get<Producto[]>(url);
  }

  listarCategorias(): Observable<{ id: number; nombre: string }[]> {
    return this.http.get<{ id: number; nombre: string }[]>('/api/categorias');
  }

  obtenerPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.apiUrl}/${id}`);
  }

  crearProducto(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.apiUrl, producto);
  }

  actualizarProducto(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.apiUrl}/${id}`, producto);
  }

  eliminarProducto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
