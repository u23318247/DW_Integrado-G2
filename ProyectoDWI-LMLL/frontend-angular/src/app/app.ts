import { ChangeDetectorRef, Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from './services/producto.service';
import { CartService } from './services/cart.service';
import { AuthService } from './services/auth.service';
import { Producto, ProductoDetalle } from './models/producto.model';
import { Categoria } from './models/categoria.model';

import { TopAnnouncement } from './shared/top-announcement/top-announcement';
import { AppHeader } from './shared/app-header/app-header';
import { HeroBanner } from './features/hero-banner/hero-banner';
import { SummaryPanel } from './features/summary-panel/summary-panel';
import { CartDrawer } from './features/cart-drawer/cart-drawer';
import { ProductModal } from './features/product-modal/product-modal';
import { AppFooter } from './shared/app-footer/app-footer';

export interface GrupoCategoria {
  id?: number;
  nombre: string;
  slug: string;
  icono: string;
  subtitulo: string;
  productos: Producto[];
}

interface CategoriaMeta {
  orden: number;
  nombreMostrar: string;
  icono: string;
  subtitulo: string;
}

export const CATEGORIA_META: Record<string, CategoriaMeta> = {
  'calzado': { orden: 1, nombreMostrar: 'Zapatillas & Calzado', icono: '👟', subtitulo: 'Sneakers urbanos, running y calzado streetwear' },
  'polos': { orden: 2, nombreMostrar: 'Polos & Camisetas', icono: '👕', subtitulo: 'Cortes oversize, básicos y estampados urbanos' },
  'abrigos': { orden: 3, nombreMostrar: 'Casacas & Abrigos', icono: '🧥', subtitulo: 'Casacas denim oversize, impermeables y cortavientos' },
  'pantalones': { orden: 4, nombreMostrar: 'Pantalones & Jeans', icono: '👖', subtitulo: 'Pantalones cargo, skinny y rectos clásicos' },
  'gorras y chullos': { orden: 5, nombreMostrar: 'Gorros & Chullos', icono: '🧢', subtitulo: 'Gorras trucker LMTL y beanies urbanos' },
  'superiores': { orden: 6, nombreMostrar: 'Superiores & Chompas', icono: '🧶', subtitulo: 'Chompas con diseño, sudaderas con capucha y camisas' },
  'accesorios': { orden: 7, nombreMostrar: 'Accesorios & Mochilas', icono: '🎒', subtitulo: 'Cangureras streetwear y mochilas minimal' },
  'medias': { orden: 8, nombreMostrar: 'Medias Urbanas', icono: '🧦', subtitulo: 'Medias altas flame y estilo skate' },
  'joyería y pines': { orden: 9, nombreMostrar: 'Joyería & Pines', icono: '✨', subtitulo: 'Cadenas de acero cubanas y pines exclusivos' },
  'joyeria y pines': { orden: 9, nombreMostrar: 'Joyería & Pines', icono: '✨', subtitulo: 'Cadenas de acero cubanas y pines exclusivos' }
};

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    TopAnnouncement,
    AppHeader,
    HeroBanner,
    SummaryPanel,
    CartDrawer,
    ProductModal,
    AppFooter
  ],
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
  ordenSeleccionado: string = 'relevancia';
  soloConStock: boolean = false;

  // Mapa de talla y color seleccionados por producto
  tallasSeleccionadas: Record<number, string> = {};
  coloresSeleccionados: Record<number, string> = {};

  // Estado para modal de vista rápida
  productoParaModal: Producto | null = null;
  tallaParaModal: string = 'M';
  colorParaModal: string = 'Negro';

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
        // Inicializar tallas y colores por defecto
        for (const p of this.productos) {
          if (p.id) {
            if (p.tallas && p.tallas.length > 0 && !this.tallasSeleccionadas[p.id]) {
              this.tallasSeleccionadas[p.id] = p.tallas[0];
            }
            if (!this.coloresSeleccionados[p.id]) {
              this.coloresSeleccionados[p.id] = (p.colores && p.colores.length > 0) ? p.colores[0] : 'Negro';
            }
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
    let resultado = [...this.productos];

    // Filtro por género
    if (this.generoSeleccionado !== 'Todos') {
      resultado = resultado.filter(p => p.genero?.toLowerCase() === this.generoSeleccionado.toLowerCase());
    }

    // Filtro solo con stock
    if (this.soloConStock) {
      resultado = resultado.filter(p => this.obtenerStockActual(p) > 0);
    }

    // Ordenamiento
    if (this.ordenSeleccionado === 'precio-menor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(a) - this.obtenerPrecioActual(b));
    } else if (this.ordenSeleccionado === 'precio-mayor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(b) - this.obtenerPrecioActual(a));
    } else if (this.ordenSeleccionado === 'nombre') {
      resultado.sort((a, b) => a.nombre.localeCompare(b.nombre));
    }

    return resultado;
  }

  get gruposPorCategoria(): GrupoCategoria[] {
    const prods = this.productosFiltrados;
    if (prods.length === 0) return [];

    const gruposMap = new Map<string, { catId?: number; catNombre: string; productos: Producto[] }>();

    for (const p of prods) {
      const key = (p.categoriaNombre || 'Otras Prendas').toLowerCase().trim();
      if (!gruposMap.has(key)) {
        gruposMap.set(key, {
          catId: p.categoriaId,
          catNombre: p.categoriaNombre || 'Otras Prendas',
          productos: []
        });
      }
      gruposMap.get(key)!.productos.push(p);
    }

    const resultado: GrupoCategoria[] = [];
    for (const [key, val] of gruposMap.entries()) {
      const meta = CATEGORIA_META[key] || {
        orden: 99,
        nombreMostrar: val.catNombre,
        icono: '🏷️',
        subtitulo: 'Colección exclusiva de temporada'
      };

      resultado.push({
        id: val.catId,
        nombre: meta.nombreMostrar,
        slug: key,
        icono: meta.icono,
        subtitulo: meta.subtitulo,
        productos: val.productos
      });
    }

    // Ordenar con la prioridad deseada: Zapatillas (1), Polos (2), Casacas (3), Pantalones (4), Gorros (5)...
    resultado.sort((a, b) => {
      const ordenA = (CATEGORIA_META[a.slug] || { orden: 99 }).orden;
      const ordenB = (CATEGORIA_META[b.slug] || { orden: 99 }).orden;
      return ordenA - ordenB;
    });

    return resultado;
  }

  obtenerIconoCategoria(nombre: string): string {
    const key = (nombre || '').toLowerCase().trim();
    return (CATEGORIA_META[key] || { icono: '🏷️' }).icono;
  }

  obtenerNombreMostrarCategoria(nombre: string): string {
    const key = (nombre || '').toLowerCase().trim();
    return (CATEGORIA_META[key] || { nombreMostrar: nombre }).nombreMostrar;
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
    this.ordenSeleccionado = 'relevancia';
    this.soloConStock = false;
    this.cargarProductos();
  }

  seleccionarTalla(productoId: number | undefined, talla: string): void {
    if (productoId !== undefined) {
      this.tallasSeleccionadas[productoId] = talla;
      this.cdr.markForCheck();
    }
  }

  obtenerTallaSeleccionada(prod: Producto): string {
    if (!prod.id) return 'Estándar';
    return this.tallasSeleccionadas[prod.id] || (prod.tallas && prod.tallas.length > 0 ? prod.tallas[0] : 'Estándar');
  }

  seleccionarColor(productoId: number | undefined, color: string): void {
    if (productoId !== undefined) {
      this.coloresSeleccionados[productoId] = color;
      this.cdr.markForCheck();
    }
  }

  obtenerColorSeleccionado(prod: Producto): string {
    if (!prod.id) return 'Negro';
    return this.coloresSeleccionados[prod.id] || (prod.colores && prod.colores.length > 0 ? prod.colores[0] : 'Negro');
  }

  obtenerColoresProducto(prod: Producto): string[] {
    if (prod.colores && prod.colores.length > 0) {
      return prod.colores;
    }
    return ['Negro', 'Blanco', 'Azul Marino'];
  }

  obtenerHexColor(color: string): string {
    const mapa: Record<string, string> = {
      'negro': '#161616',
      'blanco': '#f5f5f7',
      'azul marino': '#0d2b45',
      'azul': '#1b4965',
      'gris melange': '#7d8597',
      'gris': '#6c757d',
      'beige arena': '#d8c4b6',
      'beige': '#d8c4b6',
      'rojo borgoña': '#780000',
      'rojo': '#b00020',
      'verde oliva': '#475841',
      'verde': '#2d6a4f',
      'rosa pastel': '#f3c4fb',
      'rosa': '#ffb4a2',
      'amarillo mostaza': '#d4a373',
      'amarillo': '#f4a261',
      'lavanda': '#a29bfe',
      'plateado': '#c0c0c0',
      'dorado': '#d4af37',
      'celeste': '#89c2d9'
    };
    const key = (color || '').toLowerCase().trim();
    return mapa[key] || '#333333';
  }

  obtenerVarianteActual(prod: Producto): ProductoDetalle | undefined {
    const talla = this.obtenerTallaSeleccionada(prod);
    const color = this.obtenerColorSeleccionado(prod);
    if (prod.variantes && prod.variantes.length > 0) {
      return prod.variantes.find(v => v.talla === talla && v.color === color)
          || prod.variantes.find(v => v.talla === talla)
          || prod.variantes.find(v => v.color === color)
          || prod.variantes[0];
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
    const color = this.obtenerColorSeleccionado(prod);
    this.cartService.agregarProducto(prod, talla, color);
    this.cdr.markForCheck();
  }

  abrirVistaRapida(prod: Producto): void {
    this.productoParaModal = prod;
    this.tallaParaModal = this.obtenerTallaSeleccionada(prod);
    this.colorParaModal = this.obtenerColorSeleccionado(prod);
  }

  cerrarVistaRapida(): void {
    this.productoParaModal = null;
  }

  agregarDesdeModal(event: { producto: Producto; talla: string; color: string }): void {
    this.cartService.agregarProducto(event.producto, event.talla, event.color);
    this.cerrarVistaRapida();
    this.cdr.markForCheck();
  }

  scrollHaciaCatalogo(): void {
    const elem = document.getElementById('seccion-catalogo');
    if (elem) {
      elem.scrollIntoView({ behavior: 'smooth' });
    }
  }
}
