import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { Producto } from '../models';
import { ConfigService } from './config.service';
import { CategoriaService } from './categoria.service';
import { FiltrosStore } from './filtros.store';
import { slugify } from '../shared/utils';

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private readonly http = inject(HttpClient);
  private readonly config = inject(ConfigService);
  private readonly categoriaService = inject(CategoriaService);
  private readonly filtrosStore = inject(FiltrosStore);

  private readonly _productos = signal<Producto[]>([]);
  private readonly _cargando = signal<boolean>(false);

  readonly productos = this._productos.asReadonly();
  readonly cargando = this._cargando.asReadonly();
  readonly categorias = this.categoriaService.categorias;

  readonly productosFiltrados = computed(() => {
    const lista = this._productos();
    const { busqueda, categoriaSlug, color, talla, orden } = this.filtrosStore.filtros();

    const filtrados = lista.filter((prod) => {
      const coincideTexto =
        !busqueda || slugify(prod.nombre).includes(slugify(busqueda));

      const catProdSlug = prod.categoriaNombre ? slugify(prod.categoriaNombre) : '';
      const coincideCategoria =
        categoriaSlug === 'todos' || catProdSlug === categoriaSlug;

      const coincideColor =
        !color || (prod.colores ?? []).some((c) => slugify(c) === color);

      const coincideTalla =
        !talla || (prod.tallas ?? []).some((t) => t.trim().toUpperCase() === talla);

      return coincideTexto && coincideCategoria && coincideColor && coincideTalla;
    });

    if (orden === 'precio-asc') {
      return [...filtrados].sort((a, b) => a.precioBase - b.precioBase);
    }
    if (orden === 'precio-desc') {
      return [...filtrados].sort((a, b) => b.precioBase - a.precioBase);
    }
    return filtrados;
  });

  cargarTodo() {
    this._cargando.set(true);
    this.categoriaService.cargarCategorias().subscribe({ error: () => {} });

    return this.http.get<Producto[]>(this.config.apiUrl('/productos')).pipe(
      tap({
        next: (items) => {
          this._productos.set(items);
          this._cargando.set(false);
        },
        error: () => this._cargando.set(false),
      })
    );
  }
}