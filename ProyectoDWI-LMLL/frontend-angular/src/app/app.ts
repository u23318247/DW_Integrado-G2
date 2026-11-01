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
  'calzado': { orden: 1, nombreMostrar: 'Zapatos Formales & Mocasines', icono: '👞', subtitulo: 'Calzado Oxford de charol, mocasines italianos y stilettos' },
  'superiores': { orden: 2, nombreMostrar: 'Trajes, Blazers & Camisas', icono: '👔', subtitulo: 'Trajes sastreros slim fit, blazers cruzados y camisas pima' },
  'polos': { orden: 3, nombreMostrar: 'Polos Pima & Blusas de Seda', icono: '✨', subtitulo: 'Polos camiseros de lujo, blusas de satén y cortes finos' },
  'abrigos': { orden: 4, nombreMostrar: 'Abrigos de Paño & Gabardinas', icono: '🧥', subtitulo: 'Abrigos de lana cashmere, trench coats y sobretodos sastreros' },
  'pantalones': { orden: 5, nombreMostrar: 'Pantalones de Vestir & Sastreros', icono: '👖', subtitulo: 'Pantalones de pinzas, corte sastrero italiano y palazzos' },
  'gorras y chullos': { orden: 6, nombreMostrar: 'Sombreros Clásicos & Boinas', icono: '🎩', subtitulo: 'Sombreros Fedora de fieltro, panamá hats y boinas de lana' },
  'accesorios': { orden: 7, nombreMostrar: 'Portafolios & Accesorios de Cuero', icono: '💼', subtitulo: 'Maletines ejecutivos, corbatas de seda italiana y cinturones' },
  'medias': { orden: 8, nombreMostrar: 'Calcetines Ejecutivos & Medias Finas', icono: '🧦', subtitulo: 'Calcetines de hilo de Escocia y medias de vestir de alta costura' },
  'joyería y pines': { orden: 9, nombreMostrar: 'Relojería & Joyería Fina', icono: '⌚', subtitulo: 'Relojes automáticos de vestir, gemelos de plata y perlas' },
  'joyeria y pines': { orden: 9, nombreMostrar: 'Relojería & Joyería Fina', icono: '⌚', subtitulo: 'Relojes automáticos de vestir, gemelos de plata y perlas' }
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
  colorParaModal: string = 'Negro Azabache';

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
              this.coloresSeleccionados[p.id] = (p.colores && p.colores.length > 0) ? p.colores[0] : 'Negro Azabache';
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
        icono: '👔',
        subtitulo: 'Colección de Alta Costura & Sastrería'
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

    // Ordenar con la prioridad elegante
    resultado.sort((a, b) => {
      const ordenA = (CATEGORIA_META[a.slug] || { orden: 99 }).orden;
      const ordenB = (CATEGORIA_META[b.slug] || { orden: 99 }).orden;
      return ordenA - ordenB;
    });

    return resultado;
  }

  obtenerIconoCategoria(nombre: string): string {
    const key = (nombre || '').toLowerCase().trim();
    return (CATEGORIA_META[key] || { icono: '👔' }).icono;
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
    if (!prod.id) return 'Negro Azabache';
    return this.coloresSeleccionados[prod.id] || (prod.colores && prod.colores.length > 0 ? prod.colores[0] : 'Negro Azabache');
  }

  obtenerColoresProducto(prod: Producto): string[] {
    if (prod.colores && prod.colores.length > 0) {
      return prod.colores;
    }
    return ['Negro Azabache', 'Azul Marino', 'Blanco Marfil'];
  }

  obtenerHexColor(color: string): string {
    const mapa: Record<string, string> = {
      'negro azabache': '#111113',
      'negro': '#111113',
      'azul marino': '#0a192f',
      'azul noche': '#0b132b',
      'marino': '#0a192f',
      'blanco marfil': '#fdfbf7',
      'blanco': '#fdfbf7',
      'marfil': '#fdfbf7',
      'champagne': '#f7e7ce',
      'gris oxford': '#4a4e5a',
      'gris': '#5c677d',
      'antracita': '#2b2d42',
      'vino tinto': '#58111a',
      'burdeos': '#4a0e17',
      'borgoña': '#58111a',
      'beige camel': '#c19a6b',
      'camel': '#c19a6b',
      'beige': '#d4b996',
      'arena': '#e3d5ca',
      'verde botella': '#1b4332',
      'verde esmeralda': '#2d6a4f',
      'verde inglés': '#1f3626',
      'dorado': '#d4af37',
      'oro': '#d4af37',
      'plateado': '#c0c0c0',
      'plata': '#c0c0c0',
      'café': '#6f4e37',
      'café coñac': '#6f4e37',
      'coñac': '#8b5a2b',
      'oro rosa': '#b76e79',
      'blanco perla': '#f4edea',
      'perla': '#f4edea',
      'nude': '#e8d8c8'
    };
    const key = (color || '').toLowerCase().trim();
    return mapa[key] || '#1a1a1a';
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
