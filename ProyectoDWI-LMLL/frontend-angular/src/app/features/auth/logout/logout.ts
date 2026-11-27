import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-logout',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="container py-5">
      <div class="text-center">
        <h1 class="brand-font text-white">Cerrando sesión...</h1>
        <div class="spinner-border text-warning mt-3" role="status"></div>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; min-height: 60vh; }
  `]
})
export class LogoutComponent implements OnInit {
  private authService = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}