import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-seguimiento',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Seguimiento de Pedido</h1>
        <p class="text-muted">En construcción - Rastreo de envío</p>
        <a routerLink="/" class="btn btn-brand mt-3">Volver al inicio</a>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class SeguimientoComponent {}