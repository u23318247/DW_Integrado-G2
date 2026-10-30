import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto, ProductoDetalle } from '../../models/producto.model';

@Component({
  selector: 'app-product-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-modal.html',
  styleUrl: './product-modal.scss'
})
export class ProductModal {
  @Input() producto: Producto | null = null;
  @Input() tallaSeleccionada: string = 'Estándar';
  @Output() cerrar = new EventEmitter<void>();
  @Output() agregar = new EventEmitter<{ producto: Producto; talla: string }>();

  seleccionarTalla(talla: string): void {
    this.tallaSeleccionada = talla;
  }

  obtenerVariante(): ProductoDetalle | undefined {
    if (this.producto?.variantes && this.producto.variantes.length > 0) {
      return this.producto.variantes.find(v => v.talla === this.tallaSeleccionada);
    }
    return undefined;
  }

  obtenerPrecio(): number {
    const v = this.obtenerVariante();
    return v ? Number(v.precioTotal) : Number(this.producto?.precioBase || 0);
  }

  obtenerStock(): number {
    const v = this.obtenerVariante();
    return v ? v.stock : 99;
  }

  onAgregar(): void {
    if (this.producto) {
      this.agregar.emit({ producto: this.producto, talla: this.tallaSeleccionada });
    }
  }

  onCerrar(): void {
    this.cerrar.emit();
  }
}
