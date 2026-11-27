import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-pedidos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Gestión de Pedidos</h1>
        <p class="text-muted">En construcción - Lista y gestión de pedidos admin</p>
        <a routerLink="/admin/dashboard" class="btn btn-brand mt-3">Volver al dashboard</a>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class AdminPedidosComponent {}