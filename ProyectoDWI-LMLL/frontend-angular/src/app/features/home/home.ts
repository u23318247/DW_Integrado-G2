import {
  Component,
  OnInit,
  inject,
  signal,
  computed,
  PLATFORM_ID,
  afterNextRender,
} from '@angular/core';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';
import { CatalogoService } from '../../services/catalogo.service';
import { CategoriaService } from '../../services/categoria.service';
import { FiltrosStore, OrdenCatalogo } from '../../services/filtros.store';
import { MonedaService } from '../../services/moneda.service';
import { WishlistService } from '../../services/wishlist.service';
import { Producto, ProductoDetalle, Categoria } from '../../models';
import { obtenerHexColor, ordenarTallas, slugify } from '../../shared/utils';

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

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule, HeroBanner, SummaryPanel, ProductModal],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent implements OnInit {
  private readonly catalogoService = inject(CatalogoService);
  private readonly categoriaService = inject(CategoriaService);
  public readonly filtrosStore = inject(FiltrosStore);
  public readonly monedaService = inject(MonedaService);
  public readonly wishlistService = inject(WishlistService);
  public readonly cartService = inject(CartService);
  public readonly authService = inject(AuthService);
  private readonly platformId = inject(PLATFORM_ID);

  // Signals reactivos de estado local de vista
  readonly productos = this.catalogoService.productos;
  readonly categorias = signal<Categoria[]>([]);
  readonly cargando = signal<boolean>(true);
  readonly errorMensaje = signal<string>('');

  readonly textoBusqueda = signal<string>('');
  readonly categoriaSeleccionada = signal<number | undefined>(undefined);
  readonly colorFiltro = signal<string>('');
  readonly ordenSeleccionado = signal<string>('relevancia');
  readonly soloConStock = signal<boolean>(false);
  readonly generoSeleccionado = signal<string>('Todos');
  readonly mostrarSoloFavoritos = signal<boolean>(false);

  readonly monedaActiva = this.monedaService.moneda;
  readonly tasaCambio = computed(() => this.monedaService.tasaUsd());

  readonly coloresFiltroPaleta: string[] = [
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
    'Oro Rosa',
  ];

  // Estado para modal de vista rápida
  readonly productoParaModal = signal<Producto | null>(null);
  readonly tallaParaModal = signal<string>('M');
  readonly colorParaModal = signal<string>('Negro Azabache');

  // Mapa de talla y color seleccionados por producto
  readonly tallasSeleccionadas = signal<Record<number, string>>({});
  readonly coloresSeleccionados = signal<Record<number, string>>({});

  readonly productosFiltrados = computed(() => {
    let resultado = [...this.productos()];

    if (this.mostrarSoloFavoritos()) {
      const idsFav = new Set(this.wishlistService.ids());
      resultado = resultado.filter((p) => p.id !== undefined && idsFav.has(p.id));
    }

    const genero = this.generoSeleccionado();
    if (genero && genero !== 'Todos') {
      resultado = resultado.filter(
        (p) => (p.genero ?? '').toLowerCase() === genero.toLowerCase()
      );
    }

    const texto = slugify(this.textoBusqueda());
    if (texto) {
      resultado = resultado.filter(
        (p) =>
          slugify(p.nombre ?? '').includes(texto) ||
          slugify(p.categoriaNombre ?? '').includes(texto)
      );
    }

    const catId = this.categoriaSeleccionada();
    if (catId !== undefined) {
      const catObj = this.categorias().find((c) => c.id === catId);
      const catSlug = catObj ? slugify(catObj.nombre) : '';
      resultado = resultado.filter(
        (p) =>
          p.categoriaId === catId ||
          (catSlug && slugify(p.categoriaNombre ?? '') === catSlug)
      );
    }

    if (this.colorFiltro()) {
      const cf = slugify(this.colorFiltro());
      resultado = resultado.filter((p) =>
        this.obtenerColoresProducto(p).some((c) => slugify(c) === cf)
      );
    }

    if (this.soloConStock()) {
      resultado = resultado.filter((p) => this.obtenerStockActual(p) > 0);
    }

    const orden = this.ordenSeleccionado();
    if (orden === 'precio-menor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(a) - this.obtenerPrecioActual(b));
    } else if (orden === 'precio-mayor') {
      resultado.sort((a, b) => this.obtenerPrecioActual(b) - this.obtenerPrecioActual(a));
    } else if (orden === 'nombre') {
      resultado.sort((a, b) => (a.nombre ?? '').localeCompare(b.nombre ?? ''));
    }

    return resultado;
  });

  readonly gruposPorCategoria = computed<GrupoCategoria[]>(() => {
    const prods = this.productosFiltrados();
    if (prods.length === 0) return [];

    const gruposMap = new Map<
      string,
      { catId?: number; catNombre: string; productos: Producto[] }
    >();

    for (const p of prods) {
      const nombreRaw = p.categoriaNombre || 'Otras Prendas';
      const key = slugify(nombreRaw) || 'otras-prendas';
      if (!gruposMap.has(key)) {
        gruposMap.set(key, {
          catId: p.categoriaId,
          catNombre: nombreRaw,
          productos: [],
        });
      }
      gruposMap.get(key)!.productos.push(p);
    }

    const resultado: GrupoCategoria[] = [];
    for (const [slug, val] of gruposMap.entries()) {
      const catApi = this.categoriaService.buscarPorSlug(slug);
      resultado.push({
        id: val.catId ?? catApi?.id,
        nombre: catApi?.nombre ?? val.catNombre,
        slug,
        icono: '👔',
        subtitulo: 'Colección de Alta Costura & Sastrería',
        productos: val.productos,
      });
    }

    resultado.sort((a, b) => a.nombre.localeCompare(b.nombre));
    return resultado;
  });

  readonly totalProductos = computed(() => this.productos().length);
  readonly totalDisponibles = computed(
    () => this.productos().filter((p) => this.obtenerStockActual(p) > 0).length
  );
  readonly totalSinStock = computed(
    () => this.productos().filter((p) => this.obtenerStockActual(p) === 0).length
  );

  ngOnInit(): void {
    this.wishlistService.conectarCatalogo(this.catalogoService.productos);
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.cargando.set(true);
    this.errorMensaje.set('');

    this.categoriaService.cargarCategorias().subscribe({
      next: (cats) => {
        this.categorias.set(cats);
      },
      error: (e) => console.warn('No se pudieron cargar categorías vía API:', e),
    });

    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando.set(true);
    this.errorMensaje.set('');

    this.catalogoService.cargarTodo().subscribe({
      next: (data) => {
        const nuevasTallas: Record<number, string> = {};
        const nuevosColores: Record<number, string> = {};

        for (const p of data) {
          if (p.id !== undefined) {
            const tallasOrd = ordenarTallas(p.tallas ?? []);
            if (tallasOrd.length > 0 && !this.tallasSeleccionadas()[p.id]) {
              nuevasTallas[p.id] = tallasOrd[0];
            }
            if (!this.coloresSeleccionados()[p.id]) {
              nuevosColores[p.id] =
                p.colores && p.colores.length > 0 ? p.colores[0] : 'Negro Azabache';
            }
          }
        }

        this.tallasSeleccionadas.update((current) => ({ ...current, ...nuevasTallas }));
        this.coloresSeleccionados.update((current) => ({ ...current, ...nuevosColores }));
        this.cargando.set(false);
      },
      error: (err) => {
        console.error(err);
        this.errorMensaje.set('No se pudo conectar con el catálogo de productos.');
        this.cargando.set(false);
      },
    });
  }

  obtenerIconoCategoria(_nombre: string): string {
    return '👔';
  }

  obtenerNombreMostrarCategoria(nombre: string): string {
    const slug = slugify(nombre);
    return this.categoriaService.buscarPorSlug(slug)?.nombre ?? nombre;
  }

  buscar(): void {
    this.filtrosStore.setBusqueda(this.textoBusqueda());
  }

  filtrarCategoria(catId?: number): void {
    this.categoriaSeleccionada.set(catId);
    if (catId === undefined) {
      this.filtrosStore.setCategoria('todos');
      return;
    }
    const cat = this.categorias().find((c) => c.id === catId);
    this.filtrosStore.setCategoria(cat?.nombre ?? 'todos');
  }

  filtrarGenero(genero: string): void {
    this.generoSeleccionado.set(genero);
  }

  filtrarPorColor(color: string): void {
    this.colorFiltro.update((current) => (current === color ? '' : color));
    this.filtrosStore.setColor(this.colorFiltro());

    if (this.colorFiltro()) {
      for (const p of this.productos()) {
        if (p.id !== undefined && this.obtenerColoresProducto(p).includes(this.colorFiltro())) {
          this.coloresSeleccionados.update((current) => ({
            ...current,
            [p.id!]: this.colorFiltro(),
          }));
        }
      }
    }
  }

  cambiarOrden(orden: string): void {
    this.ordenSeleccionado.set(orden);
    if (orden === 'precio-menor') {
      this.filtrosStore.setOrden('precio-asc');
    } else if (orden === 'precio-mayor') {
      this.filtrosStore.setOrden('precio-desc');
    } else {
      this.filtrosStore.setOrden('relevancia');
    }
  }

  esColorClaro(color: string): boolean {
    const key = slugify(color);
    return [
      'blanco',
      'blanco-marfil',
      'blanco-perla',
      'marfil',
      'perla',
      'champagne',
      'arena',
      'nude',
      'beige',
    ].includes(key);
  }

  limpiarFiltros(): void {
    this.categoriaSeleccionada.set(undefined);
    this.colorFiltro.set('');
    this.textoBusqueda.set('');
    this.ordenSeleccionado.set('relevancia');
    this.soloConStock.set(false);
    this.generoSeleccionado.set('Todos');
    this.mostrarSoloFavoritos.set(false);
    this.filtrosStore.resetear();
  }

  seleccionarTalla(productoId: number | undefined, talla: string): void {
    if (productoId !== undefined) {
      this.tallasSeleccionadas.update((current) => ({ ...current, [productoId]: talla }));
      this.filtrosStore.setTalla(talla);
    }
  }

  obtenerTallaSeleccionada(prod: Producto): string {
    if (prod.id === undefined) return 'Estándar';
    const tallasOrd = ordenarTallas(prod.tallas ?? []);
    return (
      this.tallasSeleccionadas()[prod.id] ||
      (tallasOrd.length > 0 ? tallasOrd[0] : 'Estándar')
    );
  }

  seleccionarColor(productoId: number | undefined, color: string): void {
    if (productoId !== undefined) {
      this.coloresSeleccionados.update((current) => ({ ...current, [productoId]: color }));
    }
  }

  obtenerColorSeleccionado(prod: Producto): string {
    if (prod.id === undefined) return 'Negro Azabache';
    return (
      this.coloresSeleccionados()[prod.id] ||
      (prod.colores && prod.colores.length > 0 ? prod.colores[0] : 'Negro Azabache')
    );
  }

  obtenerColoresProducto(prod: Producto): string[] {
    if (prod.colores && prod.colores.length > 0) {
      return prod.colores;
    }
    return ['Negro Azabache', 'Azul Marino', 'Blanco Marfil'];
  }

  obtenerHexColor(color: string): string {
    return obtenerHexColor(color);
  }

  obtenerVarianteActual(prod: Producto): ProductoDetalle | undefined {
    const talla = this.obtenerTallaSeleccionada(prod);
    const color = this.obtenerColorSeleccionado(prod);
    if (prod.variantes && prod.variantes.length > 0) {
      return (
        prod.variantes.find((v) => v.talla === talla && v.color === color) ||
        prod.variantes.find((v) => v.talla === talla) ||
        prod.variantes.find((v) => v.color === color) ||
        prod.variantes[0]
      );
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
    return this.monedaService.convertir(montoPen);
  }

  alternarMoneda(): void {
    this.monedaService.setMoneda(this.monedaActiva() === 'PEN' ? 'USD' : 'PEN');
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

  agregarDesdeModal(event: {
    producto: Producto;
    talla: string;
    color: string;
  }): void {
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

  toggleFiltroFavoritos(): void {
    this.mostrarSoloFavoritos.update((v) => !v);
  }

  toggleFavoritoItem(p: Producto, event: Event): void {
    event.stopPropagation();
    if (p.id === undefined) return;
    const yaEstaba = this.wishlistService.tiene(p.id);
    this.wishlistService.toggle(p.id);
    this.cartService.mensajeToast.set(
      !yaEstaba ? `¡${p.nombre} guardado en Favoritos! ❤️` : 'Eliminado de Favoritos'
    );
  }
}