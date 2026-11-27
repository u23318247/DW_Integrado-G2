import { Component, OnInit, inject, signal, computed, Inject, PLATFORM_ID, afterNextRender } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { isPlatformBrowser } from '@angular/common';
import { ProductoService } from '../../services/producto.service';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { WishlistService } from '../../services/wishlist.service';
import { CurrencyService } from '../../services/currency.service';
import { CatalogFilterService, Genero } from '../../services/catalog-filter.service';
import { Producto, ProductoDetalle } from '../../models/producto.model';
import { Categoria } from '../../models/categoria.model';

import { HeroBanner } from '../../features/hero-banner/hero-banner';
import { SummaryPanel } from '../../features/summary-panel/summary-panel';
import { ProductModal } from '../../features/product-modal/product-modal';

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
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    HeroBanner,
    SummaryPanel,
    ProductModal
  ],
  templateUrl: './home.html',
  styleUrl: './home.scss'
})
export class HomeComponent implements OnInit {
  private productoService = inject(ProductoService);
  private http = inject(HttpClient);
  public cartService = inject(CartService);
  public wishlistService = inject(WishlistService);
  public authService = inject(AuthService);
  public currencyService = inject(CurrencyService);
  public catalogFilter = inject(CatalogFilterService);
  private platformId = inject(PLATFORM_ID);

  // Signals reactivos
  productos = signal<Producto[]>([]);
  categorias = signal<Categoria[]>([]);
  cargando = signal(true);
  errorMensaje = signal('');

  textoBusqueda = signal('');
  categoriaSeleccionada = signal<number | undefined>(undefined);
  colorFiltro = signal<string>('');
  ordenSeleccionado = signal<string>('relevancia');
  soloConStock = signal(false);

  monedaActiva = signal<'PEN' | 'USD'>('PEN');

  coloresFiltroPaleta: string[] = [
    'Negro Azabache',
    'Azul Marino',
    'Blanco Marfil',
    'Gris Oxford',
    'Vino Tinto',
    'Beige Camel',
    'Verde Esmeralda',
    'Café Coñac',
    'Dorado',
    'Plateado',
    'Oro Rosa'
  ];

  // Estado para modal de vista rápida
  productoParaModal = signal<Producto | null>(null);
  tallaParaModal = signal<string>('M');
  colorParaModal = signal<string>('Negro Azabache');

  // Mapa de talla y color seleccionados por producto
  tallasSeleccionadas = signal<Record<number, string>>({});
  coloresSeleccionados = signal<Record<number, string>>({});

  // Computados derivados
  tasaCambio = computed(() => this.currencyService.tasaCambio());

  // Exponer signals del servicio para el template
  generoSeleccionado = this.catalogFilter.genero;
  mostrarSoloFavoritos = this.catalogFilter.soloFavoritos;

  productosFiltrados = computed(() => {
    let resultado = [...this.productos()];

    if (this.mostrarSoloFavoritos()) {
      resultado = resultado.filter(p => this.wishlistService.esFavorito(p.id));
    }

    // Filtro por género (desde servicio compartido)
    const genero = this.generoSeleccionado();
    if (genero !== 'Todos') {
      resultado = resultado.filter(p => p.genero?.toLowerCase() === genero.toLowerCase());
    }

    // Filtro por color
    if (this.colorFiltro()) {
      const cf = this.colorFiltro().toLowerCase().trim();
      resultado = resultado.filter(p =>
        this.obtenerColoresProducto(p).some(c => c.toLowerCase().trim() === cf)
      );
    }

    // Filtro solo con stock
    if (this.soloConStock()) {
      resultado = resultado.filter(p => this.obtenerStockActual(p) > 0);
    }

    // Ordenamiento
    if (this.ordenSeleccionado() === 'precio-menor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(a) - this.obtenerPrecioActual(b));
    } else if (this.ordenSeleccionado() === 'precio-mayor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(b) - this.obtenerPrecioActual(a));
    } else if (this.ordenSeleccionado() === 'nombre') {
      resultado.sort((a, b) => a.nombre.localeCompare(b.nombre));
    }

    return resultado;
  });

  gruposPorCategoria = computed(() => {
    const prods = this.productosFiltrados();
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
  });

  totalProductos = computed(() => this.productos().length);
  totalDisponibles = computed(() => this.productos().filter(p => this.obtenerStockActual(p) > 0).length);
  totalSinStock = computed(() => this.productos().filter(p => this.obtenerStockActual(p) === 0).length);

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.cargando.set(true);
    this.errorMensaje.set('');

    this.productoService.listarCategorias().subscribe({
      next: (cats) => {
        this.categorias.set(cats);
      },
      error: (e) => console.warn('No se pudieron cargar categorías vía API:', e)
    });

    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando.set(true);
    this.productoService.listarProductos(this.categoriaSeleccionada(), this.textoBusqueda()).subscribe({
      next: (data) => {
        this.productos.set(data);
        // Inicializar tallas y colores por defecto
        const nuevasTallas: Record<number, string> = {};
        const nuevosColores: Record<number, string> = {};
        for (const p of data) {
          if (p.id) {
            if (p.tallas && p.tallas.length > 0 && !this.tallasSeleccionadas()[p.id]) {
              nuevasTallas[p.id] = p.tallas[0];
            }
            if (!this.coloresSeleccionados()[p.id]) {
              nuevosColores[p.id] = (p.colores && p.colores.length > 0) ? p.colores[0] : 'Negro Azabache';
            }
          }
        }
        this.tallasSeleccionadas.update(current => ({ ...current, ...nuevasTallas }));
        this.coloresSeleccionados.update(current => ({ ...current, ...nuevosColores }));
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.errorMensaje.set('No se pudo conectar con el catálogo de productos.');
        this.cargando.set(false);
      }
    });
  }

  obtenerIconoCategoria(nombre: string): string {
    const key = (nombre || '').toLowerCase().trim();
    return (CATEGORIA_META[key] || { icono: '👔' }).icono;
  }

  obtenerNombreMostrarCategoria(nombre: string): string {
    const key = (nombre || '').toLowerCase().trim();
    return (CATEGORIA_META[key] || { nombreMostrar: nombre }).nombreMostrar;
  }

  buscar(): void {
    this.cargarProductos();
  }

  filtrarCategoria(catId?: number): void {
    this.categoriaSeleccionada.set(catId);
    this.cargarProductos();
  }

  // Usar el servicio compartido para género
  filtrarGenero(genero: string): void {
    this.catalogFilter.setGenero(genero as Genero);
  }

  filtrarPorColor(color: string): void {
    this.colorFiltro.update(current => current === color ? '' : color);
    if (this.colorFiltro()) {
      for (const p of this.productos()) {
        if (p.id && this.obtenerColoresProducto(p).includes(this.colorFiltro())) {
          this.coloresSeleccionados.update(current => ({ ...current, [p.id!]: this.colorFiltro() }));
        }
      }
    }
  }

  esColorClaro(color: string): boolean {
    const key = (color || '').toLowerCase().trim();
    return ['blanco', 'blanco marfil', 'blanco perla', 'marfil', 'perla', 'champagne', 'arena', 'nude', 'beige'].includes(key);
  }

  limpiarFiltros(): void {
    this.categoriaSeleccionada.set(undefined);
    this.colorFiltro.set('');
    this.textoBusqueda.set('');
    this.ordenSeleccionado.set('relevancia');
    this.soloConStock.set(false);
    this.catalogFilter.clearAll(); // Limpia género y favoritos también
    this.cargarProductos();
  }

  seleccionarTalla(productoId: number | undefined, talla: string): void {
    if (productoId !== undefined) {
      this.tallasSeleccionadas.update(current => ({ ...current, [productoId]: talla }));
    }
  }

  obtenerTallaSeleccionada(prod: Producto): string {
    if (!prod.id) return 'Estándar';
    return this.tallasSeleccionadas()[prod.id] || (prod.tallas && prod.tallas.length > 0 ? prod.tallas[0] : 'Estándar');
  }

  seleccionarColor(productoId: number | undefined, color: string): void {
    if (productoId !== undefined) {
      this.coloresSeleccionados.update(current => ({ ...current, [productoId]: color }));
    }
  }

  obtenerColorSeleccionado(prod: Producto): string {
    if (!prod.id) return 'Negro Azabache';
    return this.coloresSeleccionados()[prod.id] || (prod.colores && prod.colores.length > 0 ? prod.colores[0] : 'Negro Azabache');
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

  obtenerPrecioEnMoneda(montoPen: number): number {
    const tasa = this.tasaCambio();
    if (this.monedaActiva() === 'USD') {
      return Number((montoPen / (tasa || 3.75)).toFixed(2));
    }
    return montoPen;
  }

  alternarMoneda(): void {
    this.monedaActiva.update(current => current === 'PEN' ? 'USD' : 'PEN');
  }

  agregarAlCarrito(prod: Producto): void {
    const talla = this.obtenerTallaSeleccionada(prod);
    const color = this.obtenerColorSeleccionado(prod);
    this.cartService.agregarProducto(prod, talla, color);
  }

  abrirVistaRapida(prod: Producto): void {
    this.productoParaModal.set(prod);
    this.tallaParaModal.set(this.obtenerTallaSeleccionada(prod));
    this.colorParaModal.set(this.obtenerColorSeleccionado(prod));
  }

  cerrarVistaRapida(): void {
    this.productoParaModal.set(null);
  }

  agregarDesdeModal(event: { producto: Producto; talla: string; color: string }): void {
    this.cartService.agregarProducto(event.producto, event.talla, event.color);
    this.cerrarVistaRapida();
  }

  scrollHaciaCatalogo(): void {
    if (!isPlatformBrowser(this.platformId)) return;
    afterNextRender(() => {
      const elem = document.getElementById('seccion-catalogo');
      if (elem) {
        elem.scrollIntoView({ behavior: 'smooth' });
      }
    });
  }

  // Usar el servicio compartido para favoritos
  toggleFiltroFavoritos(): void {
    this.catalogFilter.toggleFavoritos();
  }

  toggleFavoritoItem(p: Producto, event: Event): void {
    event.stopPropagation();
    const agregado = this.wishlistService.toggleFavorito(p);
    this.cartService.mensajeToast.set(agregado ? `¡${p.nombre} guardado en Favoritos! ❤️` : 'Eliminado de Favoritos');
  }
}