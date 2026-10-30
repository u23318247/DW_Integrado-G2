import { Component } from '@angular/core';
import { PRODUCTOS } from '../../data/productos.mock';

@Component({
  selector: 'app-categorias',
  templateUrl: './categorias.html',
  styleUrl: './categorias.scss',
})
export class Categorias {
  readonly categorias = [...new Set(PRODUCTOS.map((producto) => producto.categoria))];
}
