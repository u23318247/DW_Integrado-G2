import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap, catchError, of, shareReplay } from 'rxjs';
import { Categoria } from '../models';
import { ConfigService } from './config.service';
import { slugify } from '../shared/utils';

export interface CategoriaItem extends Categoria {
    slug: string;
}

@Injectable({ providedIn: 'root' })
export class CategoriaService {
    private readonly http = inject(HttpClient);
    private readonly config = inject(ConfigService);

    private readonly _categorias = signal<CategoriaItem[]>([]);
    private readonly _cargando = signal<boolean>(false);
    private readonly _error = signal<string | null>(null);

    readonly categorias = this._categorias.asReadonly();
    readonly cargando = this._cargando.asReadonly();
    readonly error = this._error.asReadonly();

    readonly mapaPorSlug = computed(() => {
        const map = new Map<string, CategoriaItem>();
        for (const cat of this._categorias()) {
            map.set(cat.slug, cat);
        }
        return map;
    });

    cargarCategorias(): Observable<CategoriaItem[]> {
        this._cargando.set(true);
        this._error.set(null);

        return this.http.get<Categoria[]>(this.config.apiUrl('/categorias')).pipe(
            map((items) =>
                (items ?? []).map((item) => ({
                    ...item,
                    slug: slugify(item.nombre),
                }))
            ),
            tap((normalizadas) => {
                this._categorias.set(normalizadas);
                this._cargando.set(false);
            }),
            catchError((err) => {
                this._error.set('No se pudieron cargar las categorías.');
                this._cargando.set(false);
                return of([]);
            }),
            shareReplay({ bufferSize: 1, refCount: true })
        );
    }

    buscarPorSlug(slug: string): CategoriaItem | undefined {
        const normalizado = slugify(slug);
        return this.mapaPorSlug().get(normalizado);
    }
}