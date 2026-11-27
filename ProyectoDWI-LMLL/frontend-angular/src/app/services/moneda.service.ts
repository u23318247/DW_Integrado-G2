import { Injectable, inject, signal, computed } from '@angular/core';
import { ConfigService } from './config.service';

export type MonedaCodigo = 'PEN' | 'USD';

@Injectable({ providedIn: 'root' })
export class MonedaService {
  private readonly config = inject(ConfigService);

  private readonly _moneda = signal<MonedaCodigo>(this.leerMonedaInicial());
  private readonly _tasaUsd = signal<number>(this.config.defaultExchangeRate);

  readonly moneda = this._moneda.asReadonly();
  readonly tasaUsd = this._tasaUsd.asReadonly();
  readonly simbolo = computed(() => (this._moneda() === 'USD' ? '$' : 'S/'));

  setMoneda(moneda: MonedaCodigo): void {
    this._moneda.set(moneda);
    try {
      localStorage.setItem(this.config.storageKeys.currency, moneda);
    } catch {
      // Ignorar si localStorage no está disponible
    }
  }

  setTasaUsd(tasa: number): void {
    if (tasa > 0) this._tasaUsd.set(tasa);
  }

  convertir(montoPen: number): number {
    if (this._moneda() === 'USD') {
      return Number((montoPen / this._tasaUsd()).toFixed(2));
    }
    return Number(montoPen.toFixed(2));
  }

  formatear(montoPen: number): string {
    const valor = this.convertir(montoPen);
    return `${this.simbolo()} ${valor.toFixed(2)}`;
  }

  private leerMonedaInicial(): MonedaCodigo {
    try {
      const guardada = localStorage.getItem(this.config.storageKeys.currency);
      return guardada === 'USD' || guardada === 'PEN' ? guardada : this.config.defaultCurrency;
    } catch {
      return this.config.defaultCurrency;
    }
  }
}