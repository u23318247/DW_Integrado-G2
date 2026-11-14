import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface EstadoBackend {
  estado: string;
  mensaje: string;
  autor?: string;
  timestamp: string;
  version: string;
}

/**
 * Servicio Angular para consultar el estado del backend a través del Reverse Proxy
 * Laboratorio 17: Despliegue con Docker y Nginx
 * Estudiante: Ali Contreras
 */
@Injectable({
  providedIn: 'root'
})
export class EstadoService {
  private http = inject(HttpClient);
  // La llamada es relativa gracias a Nginx / proxy.conf.json
  private apiUrl = '/api/estado';

  obtenerEstado(): Observable<EstadoBackend> {
    return this.http.get<EstadoBackend>(this.apiUrl);
  }
}
