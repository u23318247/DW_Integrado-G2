import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from './services/producto.service';
import { Producto } from './models/producto.model';
import { Categoria } from './models/categoria.model';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  private productoService = inject(ProductoService);

  productos: Producto[] = [];
  categorias: Categoria[] = [];
  cargando: boolean = true;
  errorMensaje: string = '';

  textoBusqueda: string = '';
  categoriaSeleccionada?: number;
  generoSeleccionado: string = 'Todos';

  ngOnInit(): void {
    this.cargarTodo();
  }

  cargarTodo(): void {
    this.cargando = true;
    this.errorMensaje = '';

    // Cargar categorías primero o en paralelo
    this.productoService.listarCategorias().subscribe({
      next: (cats) => {
        this.categorias = cats;
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
        this.cargando = false;
      },
      error: (err) => {
        console.error(err);
        this.errorMensaje = 'No se pudo conectar con el catálogo de productos.';
        this.cargando = false;
      }
    });
  }

  get productosFiltrados(): Producto[] {
    if (this.generoSeleccionado === 'Todos') {
      return this.productos;
    }
    return this.productos.filter(p => p.genero?.toLowerCase() === this.generoSeleccionado.toLowerCase());
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

  // Integración con el carrito existente de La Moda te Llama
  agregarAlCarritoGlobal(prod: Producto): void {
    try {
      let carrito: any[] = [];
      const data = localStorage.getItem('carritoLlama');
      if (data) {
        carrito = JSON.parse(data) || [];
      }

      const itemExistente = carrito.find((item: any) => item.nombre === prod.nombre);
      if (itemExistente) {
        itemExistente.cantidad += 1;
      } else {
        carrito.push({
          nombre: prod.nombre,
          precio: Number(prod.precioBase),
          cantidad: 1,
          imagen: prod.imagenUrl
        });
      }

      localStorage.setItem('carritoLlama', JSON.stringify(carrito));

      // Disparar evento para que script.js de Thymeleaf actualice el panel de bolsa
      window.dispatchEvent(new CustomEvent('carritoActualizado', { detail: carrito }));

      // Abrir panel lateral si existe
      const panel = document.getElementById('carrito-panel');
      if (panel) {
        panel.style.right = '0';
      }

      // Actualizar contenedor si script.js está presente
      const toggle = document.getElementById('carrito-toggle');
      if (toggle) {
        // Disparar click en botón del carrito si se requiere refresco
        const countSpan = document.getElementById('cart-count');
        if (countSpan) {
          const totalQty = carrito.reduce((acc, curr) => acc + curr.cantidad, 0);
          countSpan.textContent = String(totalQty);
          countSpan.style.display = totalQty > 0 ? 'flex' : 'none';
        }
      }

      // Despachar click o re-renderizar items si la función global existe
      const eventStorage = new StorageEvent('storage', {
        key: 'carritoLlama',
        newValue: JSON.stringify(carrito)
      });
      window.dispatchEvent(eventStorage);
    } catch (e) {
      console.error('Error al agregar al carrito:', e);
    }
  }
}
