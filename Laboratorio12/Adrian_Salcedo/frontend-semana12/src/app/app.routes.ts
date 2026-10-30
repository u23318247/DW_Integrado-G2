import { Routes } from '@angular/router';
import { Inicio } from './pages/inicio/inicio';
import { Productos } from './pages/productos/productos';
import { ProductoDetalle } from './pages/producto-detalle/producto-detalle';
import { Categorias } from './pages/categorias/categorias';
import { NoEncontrado } from './pages/no-encontrado/no-encontrado';

export const routes: Routes = [
  { path: '', component: Inicio },
  { path: 'productos', component: Productos },
  { path: 'productos/:id', component: ProductoDetalle },
  { path: 'categorias', component: Categorias },
  { path: '**', component: NoEncontrado },
];
