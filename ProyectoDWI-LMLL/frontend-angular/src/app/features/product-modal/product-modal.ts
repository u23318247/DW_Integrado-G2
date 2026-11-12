import { Component, EventEmitter, Input, Output, OnChanges, SimpleChanges, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto, ProductoDetalle } from '../../models/producto.model';
import { CartService } from '../../services/cart.service';

export interface LookComplemento {
  nombre: string;
  categoria: string;
  precioNormal: number;
  precioBundle: number;
  imagen: string;
  talla: string;
  color: string;
  agregado: boolean;
}

@Component({
  selector: 'app-product-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-modal.html',
  styleUrl: './product-modal.scss'
})
export class ProductModal implements OnChanges {
  private cartService = inject(CartService);

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

  // Cross-selling: Completa tu Look (Smart Outfit Bundle)
  lookBundle: LookComplemento | null = null;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['producto'] && this.producto) {
      this.construirGaleria();
      this.generarLookComplementario();
    }
  }

  construirGaleria(): void {
    if (!this.producto) return;
    const baseImg = this.producto.imagenUrl || '/img/logo.png';

    // Generar ángulos dinámicos de la misma prenda
    this.galeriaImagenes = [
      baseImg,
      baseImg.includes('?') ? `${baseImg}&angle=back` : `${baseImg}?angle=back`,
      baseImg.includes('?') ? `${baseImg}&angle=detail` : `${baseImg}?angle=detail`,
      baseImg.includes('?') ? `${baseImg}&angle=model` : `${baseImg}?angle=model`
    ];
    this.indiceImagenActiva = 0;
    this.detenerGiro();
  }

  generarLookComplementario(): void {
    if (!this.producto) return;
    const nom = (this.producto.nombre || '').toLowerCase();

    if (nom.includes('terno') || nom.includes('blazer') || nom.includes('traje')) {
      this.lookBundle = {
        nombre: 'Pantalón Sastrero Slim Fit Italiano',
        categoria: 'Pantalones',
        precioNormal: 149.00,
        precioBundle: 99.00,
        imagen: 'https://images.unsplash.com/photo-1479064555552-3ef4979f8908?w=300&auto=format&fit=crop&q=80',
        talla: '32',
        color: 'Negro Azabache',
        agregado: false
      };
    } else if (nom.includes('zapato') || nom.includes('mocas') || nom.includes('calzado')) {
      this.lookBundle = {
        nombre: 'Calcetines Ejecutivos Hilo de Escocia',
        categoria: 'Accesorios',
        precioNormal: 49.00,
        precioBundle: 29.00,
        imagen: 'https://images.unsplash.com/photo-1582966772680-860e372bb558?w=300&auto=format&fit=crop&q=80',
        talla: 'Estándar',
        color: 'Azul Marino',
        agregado: false
      };
    } else {
      this.lookBundle = {
        nombre: 'Corbata de Seda Pura & Pañuelo de Gala',
        categoria: 'Joyería y Pines',
        precioNormal: 79.00,
        precioBundle: 49.00,
        imagen: 'https://images.unsplash.com/photo-1598033129183-c4f50c736f10?w=300&auto=format&fit=crop&q=80',
        talla: 'Estándar',
        color: 'Dorado Borgoña',
        agregado: false
      };
    }
  }

  toggleBundle(): void {
    if (this.lookBundle) {
      this.lookBundle.agregado = !this.lookBundle.agregado;
    }
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

  obtenerPrecio(): number {
    return this.producto?.precioBase || 0;
  }

  obtenerPrecioTotalConBundle(): number {
    const base = this.obtenerPrecio();
    if (this.lookBundle && this.lookBundle.agregado) {
      return base + this.lookBundle.precioBundle;
    }
    return base;
  }

  obtenerStock(): number {
    if (this.producto?.variantes && this.producto.variantes.length > 0) {
      return this.producto.variantes.reduce((acc, v) => acc + (v.stock || 0), 0);
    }
    return 15;
  }

  onCerrar(): void {
    this.detenerGiro();
    this.cerrar.emit();
  }

  onAgregar(): void {
    if (!this.producto) return;
    this.agregar.emit({
      producto: this.producto,
      talla: this.tallaSeleccionada,
      color: this.colorSeleccionado
    });

    // Si seleccionó el combo "Completa tu Look", agregar el complemento con descuento
    if (this.lookBundle && this.lookBundle.agregado) {
      const prodCombo: Producto = {
        id: 999900 + (this.producto.id || 1),
        nombre: this.lookBundle.nombre,
        precioBase: this.lookBundle.precioBundle,
        imagenUrl: this.lookBundle.imagen,
        genero: this.producto.genero || 'Unisex',
        disponibilidad: true,
        categoriaId: 1
      };
      this.cartService.agregarProducto(prodCombo, this.lookBundle.talla, this.lookBundle.color);
    }

    this.onCerrar();
  }
}
