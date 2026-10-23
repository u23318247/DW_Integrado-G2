import { Component, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AppHeader } from './shared/app-header/app-header';
import { AppFooter } from './shared/app-footer/app-footer';
import { SummaryPanel } from './features/summary-panel/summary-panel';

export interface Producto {
  id: number;
  nombre: string;
  precio: number;
  stock: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, AppHeader, AppFooter, SummaryPanel],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly titulo = 'Catálogo de productos';
  protected readonly mostrarStock = signal(true);
  protected busqueda = '';

  protected readonly productos: Producto[] = [
    { id: 1, nombre: 'Laptop Gamer Victus 15.6"', precio: 3500, stock: 8 },
    { id: 2, nombre: 'Teclado Mecánico RGB Switch Blue', precio: 250, stock: 0 },
    { id: 3, nombre: 'Monitor Curvo 27" 165Hz IPS', precio: 980, stock: 4 },
    { id: 4, nombre: 'Mouse Inalámbrico Ultraligero Pro', precio: 120, stock: 15 },
    { id: 5, nombre: 'Auriculares Noise Cancelling BT', precio: 450, stock: 0 },
    { id: 6, nombre: 'Silla Ergonómica Pro Reclinable', precio: 799, stock: 6 }
  ];

  protected alternarStock(): void {
    this.mostrarStock.update(valor => !valor);
  }

  get productosFiltrados(): Producto[] {
    const termino = this.busqueda.trim().toLowerCase();
    if (!termino) {
      return this.productos;
    }
    return this.productos.filter(p => p.nombre.toLowerCase().includes(termino));
  }

  get totalProductos(): number {
    return this.productos.length;
  }

  get totalDisponibles(): number {
    return this.productos.filter(p => p.stock > 0).length;
  }

  get totalSinStock(): number {
    return this.productos.filter(p => p.stock === 0).length;
  }
}
