import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-drawer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cart-drawer.html',
  styleUrl: './cart-drawer.scss'
})
export class CartDrawer {
  public cartService = inject(CartService);
  private router = inject(Router);

  cerrar(): void {
    this.cartService.cerrarCarrito();
  }

  incrementar(index: number): void {
    this.cartService.incrementar(index);
  }

  decrementar(index: number): void {
    this.cartService.decrementar(index);
  }

  eliminar(index: number): void {
    this.cartService.eliminar(index);
  }

  vaciar(): void {
    if (confirm('¿Estás seguro de que deseas vaciar tu bolsa de compras?')) {
      this.cartService.vaciar();
    }
  }

  irAPagar(): void {
    this.cartService.cerrarCarrito();
    this.router.navigate(['/pago']);
  }
}