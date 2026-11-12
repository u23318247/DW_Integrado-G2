import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app-footer.html',
  styleUrl: './app-footer.scss'
})
export class AppFooter {
  readonly curso = 'Desarrollo Web Integrado';
  readonly anio = 2026;
  readonly estudiante = 'Antonela Jaimes';

  esModoOscuro: boolean = true;

  constructor(public authService: AuthService) {}

  alternarTema(): void {
    this.esModoOscuro = !this.esModoOscuro;
    if (this.esModoOscuro) {
      document.body.classList.remove('theme-light');
      document.body.classList.add('theme-dark');
      localStorage.setItem('lmll-theme', 'dark');
    } else {
      document.body.classList.remove('theme-dark');
      document.body.classList.add('theme-light');
      localStorage.setItem('lmll-theme', 'light');
    }
  }
}
