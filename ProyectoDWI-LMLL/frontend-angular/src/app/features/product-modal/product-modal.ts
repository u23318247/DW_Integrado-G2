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
  @Input() colorSeleccionado: string = 'Negro Azabache';
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
      'coñac': '#8b5a2b',
      'nude': '#e8d8c8'
    };
    const key = (color || '').toLowerCase().trim();
    return mapa[key] || '#1a1a1a';
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
