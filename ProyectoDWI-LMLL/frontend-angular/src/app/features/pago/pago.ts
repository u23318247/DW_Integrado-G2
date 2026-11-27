import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-pago',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Proceso de Pago</h1>
        <p class="text-muted">En construcción - Checkout y pasarela de pagos</p>
        <a routerLink="/carrito" class="btn btn-brand mt-3">Volver al carrito</a>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class PagoComponent {}