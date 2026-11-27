import { Injectable, signal } from '@angular/core';
import { slugify } from '../shared/utils';

export type OrdenCatalogo = 'relevancia' | 'precio-asc' | 'precio-desc';

export interface EstadoFiltros {
  busqueda: string;
  categoriaSlug: string;
  color: string;
  talla: string;
  orden: OrdenCatalogo;
}

const FILTROS_INICIALES: EstadoFiltros = {
  busqueda: '',
  categoriaSlug: 'todos',
  color: '',
  talla: '',
  orden: 'relevancia',
};

@Injectable({ providedIn: 'root' })
export class FiltrosStore {
  private readonly _filtros = signal<EstadoFiltros>({ ...FILTROS_INICIALES });
  readonly filtros = this._filtros.asReadonly();

  setBusqueda(busqueda: string): void {
    this._filtros.update((f) => ({ ...f, busqueda }));
  }

  setCategoria(categoria: string): void {
    const normalizada =
      !categoria || categoria.toLowerCase() === 'todos' ? 'todos' : slugify(categoria);
    this._filtros.update((f) => ({ ...f, categoriaSlug: normalizada }));
  }

  setColor(color: string): void {
    this._filtros.update((f) => ({ ...f, color: color ? slugify(color) : '' }));
  }

  setTalla(talla: string): void {
    this._filtros.update((f) => ({
      ...f,
      talla: talla ? talla.trim().toUpperCase() : '',
    }));
  }

  setOrden(orden: OrdenCatalogo): void {
    this._filtros.update((f) => ({ ...f, orden }));
  }

  resetear(): void {
    this._filtros.set({ ...FILTROS_INICIALES });
  }
}