import { Component, EventEmitter, Output, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { WishlistService } from '../../services/wishlist.service';
import { ProductoService } from '../../services/producto.service';
import { Producto } from '../../models/producto.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app-header.html',
  styleUrl: './app-header.scss'
})
export class AppHeader {
  public authService = inject(AuthService);
  public cartService = inject(CartService);
  public wishlistService = inject(WishlistService);
  public productoService = inject(ProductoService);

  @Output() generoSeleccionado = new EventEmitter<string>();
  @Output() abrirBolsa = new EventEmitter<void>();
  @Output() productoSeleccionado = new EventEmitter<Producto>();
  @Output() toggleFavoritosFiltro = new EventEmitter<void>();

  // Estado del Buscador Predictivo
  public busquedaTexto = '';
  public buscando = false;
  public resultados: Producto[] = [];
  public mostrarDropdown = false;
  public debounceTimer: any = null;

  // Estado Modal Google / Auth Rápido
  public mostrarModalGoogle = false;
  public usuarioGoogle: { nombre: string; email: string; avatar: string } | null = null;

  // Estado de Búsqueda Visual con IA
  public procesandoFoto = false;
  public mensajeVisualIa = '';

  onSelectGenero(genero: string): void {
    this.generoSeleccionado.emit(genero);
  }

  onToggleBolsa(): void {
    this.cartService.toggleCarrito();
    this.abrirBolsa.emit();
  }

  onToggleFavoritos(): void {
    this.toggleFavoritosFiltro.emit();
  }

  onInputBusqueda(event: Event): void {
    const val = (event.target as HTMLInputElement).value;
    this.busquedaTexto = val;

    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer);
    }

    if (!val || val.trim().length < 2) {
      this.resultados = [];
      this.mostrarDropdown = false;
      return;
    }

    this.buscando = true;
    this.debounceTimer = setTimeout(() => {
      this.productoService.listarProductos(undefined, val.trim()).subscribe({
        next: (data) => {
          this.resultados = (data || []).slice(0, 5);
          this.mostrarDropdown = true;
          this.buscando = false;
        },
        error: () => {
          this.buscando = false;
          this.resultados = [];
        }
      });
    }, 280);
  }

  seleccionarResultado(p: Producto): void {
    this.mostrarDropdown = false;
    this.busquedaTexto = '';
    this.productoSeleccionado.emit(p);
  }

  cerrarDropdown(): void {
    setTimeout(() => {
      this.mostrarDropdown = false;
    }, 250);
  }

  // ==========================================
  // BÚSQUEDA VISUAL POR FOTO (AI OUTFIT MATCH)
  // ==========================================
  onSubirFoto(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.procesandoFoto = true;
    this.mensajeVisualIa = 'Analizando patrón, color y textura con IA...';
    this.mostrarDropdown = true;

    // Simulación de análisis inteligente de prendas por visión computacional
    setTimeout(() => {
      const nombre = file.name.toLowerCase();
      let queryTerm = 'terno';
      if (nombre.includes('pantalon') || nombre.includes('jean')) queryTerm = 'pantalón';
      else if (nombre.includes('zapato') || nombre.includes('calzado') || nombre.includes('mocas')) queryTerm = 'mocasín';
      else if (nombre.includes('camisa') || nombre.includes('blazer')) queryTerm = 'camisa';
      else if (nombre.includes('abrigo') || nombre.includes('saco')) queryTerm = 'abrigo';

      this.busquedaTexto = queryTerm;
      this.productoService.listarProductos(undefined, queryTerm).subscribe({
        next: (items) => {
          this.resultados = (items || []).slice(0, 5);
          this.procesandoFoto = false;
          this.mensajeVisualIa = `✨ Coincidencias visuales para: "${queryTerm.toUpperCase()}"`;
        },
        error: () => {
          this.procesandoFoto = false;
          this.mensajeVisualIa = 'No pudimos reconocer prendas en la imagen.';
        }
      });
    }, 1200);
  }

  // ==========================================
  // GOOGLE IDENTITY / OAUTH2 ONE-CLICK SIGN IN
  // ==========================================
  abrirGoogleModal(): void {
    this.mostrarModalGoogle = true;
  }

  cerrarGoogleModal(): void {
    this.mostrarModalGoogle = false;
  }

  simularLoginGoogle(): void {
    this.usuarioGoogle = {
      nombre: 'Antonela Jaimes Daza',
      email: 'antonela.jaimes@utp.edu.pe',
      avatar: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80'
    };
    this.mostrarModalGoogle = false;
    this.cartService.mensajeToast.set('¡Bienvenida, Antonela! Sesión iniciada con Google');
  }

  cerrarSesionGoogle(): void {
    this.usuarioGoogle = null;
    this.cartService.mensajeToast.set('Has cerrado sesión de Google');
  }
}
