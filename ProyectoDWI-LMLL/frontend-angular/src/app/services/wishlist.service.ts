import { Injectable, inject, signal, computed, Signal } from '@angular/core';
import { ConfigService } from './config.service';
import { Producto } from '../models';

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private readonly config = inject(ConfigService);

  private readonly _ids = signal<number[]>(this.leerIds());
  private _catalogoProductos: Signal<Producto[]> = signal<Producto[]>([]);

  readonly ids = this._ids.asReadonly();
  readonly total = computed(() => this._ids().length);
  readonly totalFavoritos = this.total;

  readonly productos = computed<Producto[]>(() => {
    const idsSet = new Set(this._ids());
    return this._catalogoProductos().filter(
      (p) => p.id !== undefined && idsSet.has(p.id)
    );
  });

  conectarCatalogo(productosSignal: Signal<Producto[]>): void {
    this._catalogoProductos = productosSignal;
  }

  toggle(productoId: number): void {
    const actual = this._ids();
    const siguiente = actual.includes(productoId)
      ? actual.filter((id) => id !== productoId)
      : [...actual, productoId];

    this._ids.set(siguiente);
    this.guardarIds(siguiente);
  }

  tiene(productoId: number): boolean {
    return this._ids().includes(productoId);
  }

  esFavorito(productoId: number | undefined): boolean {
    if (productoId === undefined) return false;
    return this.tiene(productoId);
  }

  limpiar(): void {
    this._ids.set([]);
    this.guardarIds([]);
  }

  private leerIds(): number[] {
    try {
      const raw = localStorage.getItem(this.config.storageKeys.wishlist);
      if (!raw) return [];
      const parsed = JSON.parse(raw);
      return Array.isArray(parsed)
        ? parsed.filter((x): x is number => typeof x === 'number')
        : [];
    } catch {
      return [];
    }
  }

  private guardarIds(ids: number[]): void {
    try {
      localStorage.setItem(this.config.storageKeys.wishlist, JSON.stringify(ids));
    } catch {
      // Sin acción si localStorage no está disponible
    }
  }
}