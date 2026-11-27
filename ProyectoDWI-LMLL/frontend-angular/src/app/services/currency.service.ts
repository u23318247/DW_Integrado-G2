import { Injectable, inject, computed } from '@angular/core';
import { httpResource } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class CurrencyService {
  private readonly API_URL = '/api/tipo-cambio';

  // httpResource para SSR-safe data fetching
  private tasaResource = httpResource<{ tasa: number }>(() => this.API_URL);

  // Signal reactivo con fallback
  tasaCambio = computed(() => this.tasaResource.value()?.tasa ?? 3.75);
  cargando = computed(() => this.tasaResource.isLoading());
  error = computed(() => this.tasaResource.error());

  // Método para refrescar manualmente
  refrescar(): void {
    this.tasaResource.reload();
  }
}