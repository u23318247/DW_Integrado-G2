import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { PanelComponent } from './pages/panel/panel.component';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'panel', component: PanelComponent, canActivate: [authGuard] },
  { path: '**', redirectTo: 'login' }
];
