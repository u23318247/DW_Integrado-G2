import { Injectable, inject, signal, computed } from '@angular/core';
import { Router } from '@angular/router';

export type Genero = 'Todos' | 'Hombre' | 'Mujer' | 'Unisex';

@Injectable({ providedIn: 'root' })
export class CatalogFilterService {
  private router = inject(Router);

  // Estado reactivo compartido
  genero = signal<Genero>('Todos');
  soloFavoritos = signal(false);

  // Helpers para navegación
  setGenero(g: Genero): void {
    this.genero.set(g);
    this.router.navigate(['/']);
  }

  toggleFavoritos(): void {
    this.soloFavoritos.update(v => !v);
    this.router.navigate(['/']);
  }

  clearAll(): void {
    this.genero.set('Todos');
    this.soloFavoritos.set(false);
  }
}