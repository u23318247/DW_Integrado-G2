import { Component, EventEmitter, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app-header.html',
  styleUrl: './app-header.scss'
})
export class AppHeader {
  public authService = inject(AuthService);
  public cartService = inject(CartService);

  @Output() generoSeleccionado = new EventEmitter<string>();
  @Output() abrirBolsa = new EventEmitter<void>();

  onSelectGenero(genero: string): void {
    this.generoSeleccionado.emit(genero);
  }

  onToggleBolsa(): void {
    this.cartService.toggleCarrito();
    this.abrirBolsa.emit();
  }
}
