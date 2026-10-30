import { Injectable, computed, signal } from '@angular/core';
import { CartItem } from '../models/cart.model';
import { Producto, ProductoDetalle } from '../models/producto.model';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly STORAGE_KEY = 'carritoLlama';

  // Estado reactivo con Signals de Angular
  items = signal<CartItem[]>(this.cargarItemsDeStorage());
  isCartOpen = signal<boolean>(false);
  mensajeToast = signal<string | null>(null);

  // Computados automáticos
  totalItems = computed(() =>
    this.items().reduce((acc, item) => acc + item.cantidad, 0)
  );

  cantidadTotal = computed(() => this.totalItems());

  subtotal = computed(() =>
    this.items().reduce((acc, item) => acc + (item.precio * item.cantidad), 0)
  );

  igv = computed(() =>
    this.subtotal() * 0.18
  );

  total = computed(() =>
    this.subtotal() + this.igv()
  );

  constructor() {
    this.sincronizarConThymeleaf();
  }

  agregarProducto(producto: Producto, tallaSeleccionada?: string): void {
    const lista = [...this.items()];
    const talla = tallaSeleccionada || (producto.tallas && producto.tallas.length > 0 ? producto.tallas[0] : 'Estándar');

    // Buscar variante que coincida con la talla si existe
    let variante: ProductoDetalle | undefined;
    if (producto.variantes && producto.variantes.length > 0) {
      variante = producto.variantes.find(v => v.talla === talla);
    }

    const precioFinal = variante ? Number(variante.precioTotal) : Number(producto.precioBase);
    const sku = variante ? variante.sku : `PROD-${producto.id}-${talla}`;
    const marca = variante ? variante.marca : 'LMLL';
    const color = variante ? variante.color : 'Único';
    const stockMax = variante ? variante.stock : 99;

    const indexExistente = lista.findIndex(i =>
      (i.sku && i.sku === sku) || (i.nombre === producto.nombre && i.talla === talla)
    );

    if (indexExistente >= 0) {
      if (lista[indexExistente].cantidad < stockMax) {
        lista[indexExistente].cantidad += 1;
        this.mostrarToast(`Se agregó una unidad más de ${producto.nombre} (${talla})`);
      } else {
        this.mostrarToast(`Stock máximo alcanzado para talla ${talla}`);
        return;
      }
    } else {
      lista.push({
        id: variante?.id,
        productoId: producto.id,
        sku: sku,
        nombre: producto.nombre,
        precio: precioFinal,
        cantidad: 1,
        imagen: producto.imagenUrl,
        talla: talla,
        color: color,
        marca: marca,
        stockMaximo: stockMax
      });
      this.mostrarToast(`¡${producto.nombre} (${talla}) añadido a tu bolsa!`);
    }

    this.actualizarEstado(lista);
  }

  incrementar(index: number): void {
    const lista = [...this.items()];
    if (lista[index]) {
      const stock = lista[index].stockMaximo || 99;
      if (lista[index].cantidad < stock) {
        lista[index].cantidad += 1;
        this.actualizarEstado(lista);
      } else {
        this.mostrarToast(`No hay más stock disponible.`);
      }
    }
  }

  decrementar(index: number): void {
    const lista = [...this.items()];
    if (lista[index]) {
      if (lista[index].cantidad > 1) {
        lista[index].cantidad -= 1;
        this.actualizarEstado(lista);
      } else {
        this.eliminar(index);
      }
    }
  }

  eliminar(index: number): void {
    const lista = [...this.items()];
    if (lista[index]) {
      const nombre = lista[index].nombre;
      lista.splice(index, 1);
      this.actualizarEstado(lista);
      this.mostrarToast(`${nombre} eliminado de la bolsa`);
    }
  }

  vaciar(): void {
    this.actualizarEstado([]);
    this.mostrarToast('Bolsa de compras vaciada');
  }

  toggleCarrito(): void {
    this.isCartOpen.update(v => !v);
    this.sincronizarPanelThymeleaf(this.isCartOpen());
  }

  abrirCarrito(): void {
    this.isCartOpen.set(true);
    this.sincronizarPanelThymeleaf(true);
  }

  cerrarCarrito(): void {
    this.isCartOpen.set(false);
    this.sincronizarPanelThymeleaf(false);
  }

  private mostrarToast(mensaje: string): void {
    this.mensajeToast.set(mensaje);
    setTimeout(() => {
      if (this.mensajeToast() === mensaje) {
        this.mensajeToast.set(null);
      }
    }, 3000);
  }

  private actualizarEstado(nuevaLista: CartItem[]): void {
    this.items.set(nuevaLista);
    try {
      localStorage.setItem(this.STORAGE_KEY, JSON.stringify(nuevaLista));
    } catch (e) {
      console.warn('No se pudo guardar en localStorage:', e);
    }
    this.sincronizarConThymeleaf();
  }

  private cargarItemsDeStorage(): CartItem[] {
    try {
      const data = localStorage.getItem(this.STORAGE_KEY);
      if (data) {
        const parsed = JSON.parse(data);
        if (Array.isArray(parsed)) {
          return parsed;
        }
      }
    } catch (e) {
      console.warn('Error leyendo carrito de localStorage:', e);
    }
    return [];
  }

  private sincronizarConThymeleaf(): void {
    if (typeof window === 'undefined') return;

    try {
      const lista = this.items();
      const count = this.totalItems();

      // Actualizar badge del header Thymeleaf si está presente en el DOM
      const countSpan = document.getElementById('cart-count');
      if (countSpan) {
        countSpan.textContent = String(count);
        countSpan.style.display = count > 0 ? 'inline-flex' : 'none';
      }

      // Notificar evento para que script.js de Thymeleaf lo detecte
      window.dispatchEvent(new CustomEvent('carritoActualizado', { detail: lista }));
    } catch {
      // Entorno sin DOM puro
    }
  }

  private sincronizarPanelThymeleaf(abrir: boolean): void {
    if (typeof document === 'undefined') return;
    const panel = document.getElementById('carrito-panel');
    if (panel) {
      panel.style.right = abrir ? '0' : '-320px';
    }
  }
}
