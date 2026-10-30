import { Component } from '@angular/core';
import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-categorias',
  imports: [],
  templateUrl: './categorias.html',
  styleUrl: './categorias.scss'
})
export class Categorias {
  // Reto A: categorías únicas de PRODUCTOS (Set elimina duplicados)
  readonly categorias = [...new Set(PRODUCTOS.map(p => p.categoria))];

  cantidad(categoria: string): number {
    return PRODUCTOS.filter(p => p.categoria === categoria).length;
  }
}
