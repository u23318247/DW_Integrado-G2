import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EstadoService, EstadoBackend } from '../../services/estado.service';

@Component({
  selector: 'app-inicio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './inicio.component.html',
  styleUrl: './inicio.component.css'
})
export class InicioComponent implements OnInit {
  private estadoService = inject(EstadoService);

  estado: EstadoBackend | null = null;
  cargando: boolean = true;
  error: string | null = null;
  fechaConsulta: string = '';

  ngOnInit(): void {
    this.verificarConexion();
  }

  verificarConexion(): void {
    this.cargando = true;
    this.error = null;
    this.fechaConsulta = new Date().toLocaleTimeString();

    this.estadoService.obtenerEstado().subscribe({
      next: (data) => {
        this.estado = data;
        this.cargando = false;
      },
      error: (err) => {
        this.error = 'No se pudo conectar con el backend. Verifique que los contenedores Docker estén arriba o el proxy activo. Detalle: ' + (err.message || err.statusText || 'Error de red');
        this.cargando = false;
      }
    });
  }
}
