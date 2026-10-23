import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from './services/producto.service';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';
import { Producto } from './models/producto.model';
import { Categoria } from './models/categoria.model';
import { AppHeader } from './shared/app-header/app-header';
import { AppFooter } from './shared/app-footer/app-footer';
import { SummaryPanel } from './features/summary-panel/summary-panel';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule, AppHeader, AppFooter, SummaryPanel],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  private productoService = inject(ProductoService);
  public cartService = inject(CartService);
  public authService = inject(AuthService);
  private cdr = inject(ChangeDetectorRef);

  productos: Producto[] = [];
  categorias: Categoria[] = [];
  cargando: boolean = true;
  errorMensaje: string = '';

  textoBusqueda: string = '';
  categoriaSeleccionada?: number;
  generoSeleccionado: string = 'Todos';

  // Mapa de talla seleccionada por cada producto (id -> talla)
  tallasSeleccionadas: Record<number, string> = {};

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.cargando = true;
    this.errorMensaje = '';

    this.productoService.listarCategorias().subscribe({
      next: (cats) => {
        this.categorias = cats;
        this.cdr.markForCheck();
      },
      error: (e) => console.warn('No se pudieron cargar categorías vía API:', e)
    });

    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando = true;
    this.productoService.listarProductos(this.categoriaSeleccionada, this.textoBusqueda).subscribe({
      next: (data) => {
        this.productos = data;
        // Inicializar tallas por defecto
        for (const p of this.productos) {
          if (p.id && p.tallas && p.tallas.length > 0 && !this.tallasSeleccionadas[p.id]) {
            this.tallasSeleccionadas[p.id] = p.tallas[0];
          }
        }
        this.cargando = false;
        this.cdr.markForCheck();
      },
      error: (err) => {
        console.error(err);
        this.errorMensaje = 'No se pudo conectar con el catálogo de productos.';
        this.cargando = false;
        this.cdr.markForCheck();
      }
    });
  }

  get productosFiltrados(): Producto[] {
    if (this.generoSeleccionado === 'Todos') {
      return this.productos;
    }
    return this.productos.filter(p => p.genero?.toLowerCase() === this.generoSeleccionado.toLowerCase());
  }

  get totalProductos(): number {
    return this.productos.length;
  }

  get totalDisponibles(): number {
    return this.productos.filter(p => this.obtenerStockActual(p) > 0).length;
  }

  get totalSinStock(): number {
    return this.productos.filter(p => this.obtenerStockActual(p) === 0).length;
  }

  buscar(): void {
    this.cargarProductos();
  }

  filtrarCategoria(catId?: number): void {
    this.categoriaSeleccionada = catId;
    this.cargarProductos();
  }

  filtrarGenero(genero: string): void {
    this.generoSeleccionado = genero;
  }

  limpiarFiltros(): void {
    this.categoriaSeleccionada = undefined;
    this.generoSeleccionado = 'Todos';
    this.textoBusqueda = '';
    this.cargarProductos();
  }

  seleccionarTalla(productoId: number, talla: string): void {
    this.tallasSeleccionadas[productoId] = talla;
    this.cdr.markForCheck();
  }

  obtenerTallaSeleccionada(prod: Producto): string {
    if (!prod.id) return 'Estándar';
    return this.tallasSeleccionadas[prod.id] || (prod.tallas && prod.tallas.length > 0 ? prod.tallas[0] : 'Estándar');
  }

  obtenerVarianteActual(prod: Producto) {
    const talla = this.obtenerTallaSeleccionada(prod);
    if (prod.variantes && prod.variantes.length > 0) {
      return prod.variantes.find(v => v.talla === talla);
    }
    return undefined;
  }

  obtenerPrecioActual(prod: Producto): number {
    const variante = this.obtenerVarianteActual(prod);
    return variante ? Number(variante.precioTotal) : Number(prod.precioBase);
  }

  obtenerStockActual(prod: Producto): number {
    const variante = this.obtenerVarianteActual(prod);
    return variante ? variante.stock : 99;
  }

  agregarAlCarrito(prod: Producto): void {
    const talla = this.obtenerTallaSeleccionada(prod);
    this.cartService.agregarProducto(prod, talla);
    this.cdr.markForCheck();
  }
}
