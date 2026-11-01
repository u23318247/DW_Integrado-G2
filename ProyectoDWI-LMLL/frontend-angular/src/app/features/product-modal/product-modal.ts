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
  @Input() colorSeleccionado: string = 'Negro';
  @Output() cerrar = new EventEmitter<void>();
  @Output() agregar = new EventEmitter<{ producto: Producto; talla: string; color: string }>();

  seleccionarTalla(talla: string): void {
    this.tallaSeleccionada = talla;
  }

  seleccionarColor(color: string): void {
    this.colorSeleccionado = color;
  }

  obtenerColores(): string[] {
    if (this.producto?.colores && this.producto.colores.length > 0) {
      return this.producto.colores;
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

  obtenerVariante(): ProductoDetalle | undefined {
    if (this.producto?.variantes && this.producto.variantes.length > 0) {
      return this.producto.variantes.find(v => v.talla === this.tallaSeleccionada && v.color === this.colorSeleccionado)
          || this.producto.variantes.find(v => v.talla === this.tallaSeleccionada)
          || this.producto.variantes.find(v => v.color === this.colorSeleccionado)
          || this.producto.variantes[0];
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
      this.agregar.emit({
        producto: this.producto,
        talla: this.tallaSeleccionada,
        color: this.colorSeleccionado
      });
    }
  }

  onCerrar(): void {
    this.cerrar.emit();
  }
}
