import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-admin-ingresos',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Panel de Ingresos</h1>
        <p class="text-muted">En construcción - Reportes de ingresos admin</p>
        <a routerLink="/admin/dashboard" class="btn btn-brand mt-3">Volver al dashboard</a>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class AdminIngresosComponent {}