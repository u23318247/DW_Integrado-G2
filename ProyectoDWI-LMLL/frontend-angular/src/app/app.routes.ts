import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home').then(m => m.HomeComponent) },
  { path: 'productos', loadComponent: () => import('./features/productos/productos').then(m => m.ProductosComponent) },
  { path: 'producto/:id', loadComponent: () => import('./features/producto-detalle/producto-detalle').then(m => m.ProductoDetalleComponent) },
  { path: 'carrito', loadComponent: () => import('./features/carrito/carrito').then(m => m.CarritoComponent) },
  { path: 'pago', loadComponent: () => import('./features/pago/pago').then(m => m.PagoComponent) },
  { path: 'pago-resultado', loadComponent: () => import('./features/pago-resultado/pago-resultado').then(m => m.PagoResultadoComponent) },
  { path: 'mis-pedidos', loadComponent: () => import('./features/mis-pedidos/mis-pedidos').then(m => m.MisPedidosComponent) },
  { path: 'seguimiento', loadComponent: () => import('./features/seguimiento/seguimiento').then(m => m.SeguimientoComponent) },
  { path: 'reclamaciones', loadComponent: () => import('./features/reclamaciones/reclamaciones').then(m => m.ReclamacionesComponent) },
  { path: 'auth/login', loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent) },
  { path: 'auth/registro', loadComponent: () => import('./features/auth/registro/registro').then(m => m.RegistroComponent) },
  { path: 'auth/logout', loadComponent: () => import('./features/auth/logout/logout').then(m => m.LogoutComponent) },
  { path: 'admin/dashboard', loadComponent: () => import('./features/admin/dashboard/dashboard').then(m => m.AdminDashboardComponent) },
  { path: 'admin/productos', loadComponent: () => import('./features/admin/productos/productos').then(m => m.AdminProductosComponent) },
  { path: 'admin/pedidos', loadComponent: () => import('./features/admin/pedidos/pedidos').then(m => m.AdminPedidosComponent) },
  { path: 'admin/ingresos', loadComponent: () => import('./features/admin/ingresos/ingresos').then(m => m.AdminIngresosComponent) },
  { path: '**', redirectTo: '' }
];