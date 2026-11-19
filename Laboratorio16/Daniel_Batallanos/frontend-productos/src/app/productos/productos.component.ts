import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Producto, ProductoRequest } from '../models/producto';
import { ProductoService } from '../services/producto.service';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.css'
})
export class ProductosComponent {
  private readonly productoService = inject(ProductoService);
  private readonly fb = inject(FormBuilder);

  productos = signal<Producto[]>([]);
  cargando = signal(false);
  mensajeError = signal('');
  productoEditandoId = signal<number | null>(null);

  formulario = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.minLength(3)]],
    precio: [0, [Validators.required, Validators.min(0)]],
    stock: [0, [Validators.required, Validators.min(0)]],
    fechaRegistro: ['', [Validators.required]]
  });

  constructor() {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando.set(true);
    this.mensajeError.set('');
    this.productoService.listar().subscribe({
      next: (data) => {
        this.productos.set(data);
        this.cargando.set(false);
      },
      error: (error) => this.manejarError(error)
    });
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const request: ProductoRequest = this.formulario.getRawValue();
    const id = this.productoEditandoId();
    if (id === null) {
      this.crearProducto(request);
    } else {
      this.actualizarProducto(id, request);
    }
  }

  crearProducto(request: ProductoRequest): void {
    this.productoService.crear(request).subscribe({
      next: () => {
        this.limpiarFormulario();
        this.cargarProductos();
      },
      error: (error) => this.manejarError(error)
    });
  }

  actualizarProducto(id: number, request: ProductoRequest): void {
    this.productoService.actualizar(id, request).subscribe({
      next: () => {
        this.limpiarFormulario();
        this.cargarProductos();
      },
      error: (error) => this.manejarError(error)
    });
  }

  editar(producto: Producto): void {
    this.productoEditandoId.set(producto.id);
    this.formulario.setValue({
      nombre: producto.nombre,
      precio: producto.precio,
      stock: producto.stock,
      fechaRegistro: producto.fechaRegistro
    });
  }

  eliminar(id: number): void {
    const confirmado = confirm('¿Está seguro de eliminar este producto?');
    if (!confirmado) {
      return;
    }
    this.productoService.eliminar(id).subscribe({
      next: () => this.cargarProductos(),
      error: (error) => this.manejarError(error)
    });
  }

  limpiarFormulario(): void {
    this.productoEditandoId.set(null);
    this.formulario.reset({
      nombre: '',
      precio: 0,
      stock: 0,
      fechaRegistro: ''
    });
  }

  private manejarError(error: HttpErrorResponse): void {
    this.cargando.set(false);
    if (error.status === 0) {
      this.mensajeError.set('No se pudo conectar con el backend. Verifique que Spring Boot esté ejecutándose.');
      return;
    }
    const mensajeBackend = error.error?.mensaje;
    this.mensajeError.set(mensajeBackend ?? `Error HTTP ${error.status}`);
  }
}
