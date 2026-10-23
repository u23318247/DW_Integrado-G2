import { Component, computed, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { SummaryPanel } from './features/summary-panel/summary-panel';
import { AppFooter } from './shared/app-footer/app-footer';
import { AppHeader } from './shared/app-header/app-header';

interface Producto {
  id: number;
  nombre: string;
  precio: number;
  stock: number;
}

@Component({
  selector: 'app-root',
  imports: [AppHeader, AppFooter, SummaryPanel, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly titulo = 'Panel de productos';
  protected readonly mostrarStock = signal(true);

  // Enlace bidireccional con [(ngModel)] (practica opcional de la seccion 9).
  protected readonly busqueda = signal('');

  protected readonly productos: Producto[] = [
    { id: 1, nombre: 'Laptop', precio: 3500, stock: 8 },
    { id: 2, nombre: 'Teclado mecánico', precio: 250, stock: 0 },
    { id: 3, nombre: 'Monitor 27 pulgadas', precio: 980, stock: 4 },
    { id: 4, nombre: 'Mouse inalámbrico', precio: 120, stock: 0 },
  ];

  // Metricas derivadas (extension recomendada): se recalculan solas a partir de los datos.
  protected readonly totalProductos = this.productos.length;
  protected readonly totalSinStock = this.productos.filter((p) => p.stock === 0).length;

  protected readonly productosFiltrados = computed(() => {
    const texto = this.busqueda().trim().toLowerCase();
    return this.productos.filter((p) => p.nombre.toLowerCase().includes(texto));
  });

  protected alternarStock(): void {
    this.mostrarStock.update((valor) => !valor);
  }

  protected limpiarBusqueda(): void {
    this.busqueda.set('');
  }
}
