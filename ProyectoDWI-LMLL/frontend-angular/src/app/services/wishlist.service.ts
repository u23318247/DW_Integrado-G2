import { Injectable, signal, computed } from '@angular/core';
import { Producto } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private STORAGE_KEY = 'lmll_wishlist_items';
  
  public items = signal<Producto[]>(this.cargarItems());
  public totalFavoritos = computed(() => this.items().length);

  private cargarItems(): Producto[] {
    try {
      const data = localStorage.getItem(this.STORAGE_KEY);
      return data ? JSON.parse(data) : [];
    } catch {
      return [];
    }
  }

  private guardarItems(): void {
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(this.items()));
    } catch (e) {
      console.error('Error guardando wishlist:', e);
    }
  }

  public esFavorito(productoId?: number): boolean {
    if (!productoId) return false;
    return this.items().some(p => p.id === productoId);
  }

  public toggleFavorito(producto: Producto): boolean {
    if (!producto.id) return false;
    const actuales = this.items();
    const indice = actuales.findIndex(p => p.id === producto.id);
    let agregado = false;

    if (indice >= 0) {
      this.items.set(actuales.filter(p => p.id !== producto.id));
    } else {
      this.items.set([...actuales, producto]);
      agregado = true;
    }
    this.guardarItems();
    return agregado;
  }
}
