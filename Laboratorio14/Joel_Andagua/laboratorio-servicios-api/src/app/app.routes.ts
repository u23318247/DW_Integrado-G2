import { Routes } from '@angular/router';
import { Usuarios } from './usuarios/usuarios';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'usuarios'
  },
  {
    path: 'usuarios',
    component: Usuarios
  },
  {
    path: '**',
    redirectTo: 'usuarios'
  }
];
