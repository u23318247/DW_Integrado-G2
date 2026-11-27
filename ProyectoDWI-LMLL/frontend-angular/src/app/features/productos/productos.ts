import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Productos</h1>
        <p class="text-muted">En construcción - Lista completa de productos</p>
        <a routerLink="/" class="btn btn-brand mt-3">Volver al inicio</a>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class ProductosComponent {}