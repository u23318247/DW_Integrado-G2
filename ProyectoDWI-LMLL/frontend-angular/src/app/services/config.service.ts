import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConfigService {
    readonly apiBaseUrl = environment.apiBaseUrl;
    readonly defaultCurrency = environment.defaultCurrency;
    readonly defaultExchangeRate = environment.defaultExchangeRate;
    readonly storageKeys = environment.storageKeys;

    apiUrl(endpoint: string): string {
        const path = endpoint.startsWith('/') ? endpoint : `/${endpoint}`;
        return `${this.apiBaseUrl}${path}`;
    }
}