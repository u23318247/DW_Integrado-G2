import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    // Sin provideRouter: la app no usa rutas y vive incrustada en la pagina /productos de
    // Spring Boot; el router fallaba con NG04002 al no encontrar una ruta para "productos".
    provideHttpClient()
  ]
};
