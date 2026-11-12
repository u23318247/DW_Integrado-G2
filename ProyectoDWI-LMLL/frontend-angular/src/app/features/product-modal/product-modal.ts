import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto, ProductoDetalle } from '../../models/producto.model';

@Component({
  selector: 'app-product-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-modal.html',
  styleUrl: './product-modal.scss'
})
export class ProductModal implements OnChanges {
  @Input() producto: Producto | null = null;
  @Input() tallaSeleccionada: string = 'M';
  @Input() colorSeleccionado: string = 'Negro';
  @Output() cerrar = new EventEmitter<void>();
  @Output() agregar = new EventEmitter<{ producto: Producto; talla: string; color: string }>();

  // Galería de imágenes multi-ángulo (360° / Vistas)
  galeriaImagenes: string[] = [];
  indiceImagenActiva: number = 0;
  estaGirando: boolean = false;
  timerGiro: any = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['producto'] && this.producto) {
      this.construirGaleria();
    }
  }

  construirGaleria(): void {
    if (!this.producto) return;
    const baseImg = this.producto.imagenUrl || '/img/logo.png';

    // Generar ángulos dinámicos de la misma prenda (Frontal, Detalle textura, Modelo, Espalda)
    this.galeriaImagenes = [
      baseImg,
      baseImg.includes('?') ? `${baseImg}&angle=back` : `${baseImg}?angle=back`,
      baseImg.includes('?') ? `${baseImg}&angle=detail` : `${baseImg}?angle=detail`,
      baseImg.includes('?') ? `${baseImg}&angle=model` : `${baseImg}?angle=model`
    ];
    this.indiceImagenActiva = 0;
    this.detenerGiro();
  }

  cambiarImagen(indice: number): void {
    this.indiceImagenActiva = indice;
  }

  siguienteImagen(): void {
    this.indiceImagenActiva = (this.indiceImagenActiva + 1) % this.galeriaImagenes.length;
  }

  anteriorImagen(): void {
    this.indiceImagenActiva = (this.indiceImagenActiva - 1 + this.galeriaImagenes.length) % this.galeriaImagenes.length;
  }

  // Vista 360 interactiva / Rotación automática
  alternarGiro360(): void {
    if (this.estaGirando) {
      this.detenerGiro();
    } else {
      this.iniciarGiro();
    }
  }

  iniciarGiro(): void {
    this.estaGirando = true;
    this.timerGiro = setInterval(() => {
      this.siguienteImagen();
    }, 1200);
  }

  detenerGiro(): void {
    this.estaGirando = false;
    if (this.timerGiro) {
      clearInterval(this.timerGiro);
      this.timerGiro = null;
    }
  }

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
    return ['Negro', 'Azul Marino', 'Blanco Marfil', 'Verde Bosque'];
  }

  obtenerHexColor(color: string): string {
    const mapa: Record<string, string> = {
      'negro': '#111113',
      'negro azabache': '#111113',
      'azul marino': '#0a192f',
      'azul noche': '#0b132b',
      'blanco': '#fdfbf7',
      'blanco marfil': '#fdfbf7',
      'gris oxford': '#4a4e5a',
      'camel': '#c19a6b',
      'beige': '#d4b996',
      'verde botella': '#1b4332',
      'verde bosque': '#2d6a4f',
      'vino tinto': '#58111a',
      'dorado': '#d4af37'
    };
    return mapa[(color || '').toLowerCase().trim()] || '#333333';
  }

  obtenerStock(): number {
    if (this.producto?.variantes && this.producto.variantes.length > 0) {
      return this.producto.variantes.reduce((acc, v) => acc + (v.stock || 0), 0);
    }
    return 15;
  }

  obtenerPrecio(): number {
    return this.producto?.precioBase ?? 0;
  }

  onCerrar(): void {
    this.detenerGiro();
    this.cerrar.emit();
  }

  onAgregar(): void {
    if (this.producto) {
      this.agregar.emit({
        producto: this.producto,
        talla: this.tallaSeleccionada,
        color: this.colorSeleccionado
      });
      this.onCerrar();
    }
  }
}