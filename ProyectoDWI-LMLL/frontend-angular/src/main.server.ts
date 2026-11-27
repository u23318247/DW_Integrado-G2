import { bootstrapApplication } from '@angular/platform-browser';
import { provideServerRendering } from '@angular/platform-server';
import { appConfig } from './app/app.config';
import { App } from './app/app';

export default () => bootstrapApplication(App, {
  ...appConfig,
  providers: [
    ...(appConfig.providers || []),
    provideServerRendering()
  ]
}).catch((err) => console.error(err));