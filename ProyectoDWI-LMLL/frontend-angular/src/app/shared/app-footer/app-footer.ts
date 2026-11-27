import { Component, inject, PLATFORM_ID, afterNextRender, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './app-footer.html',
  styleUrl: './app-footer.scss'
})
export class AppFooter {
  readonly curso = 'Desarrollo Web Integrado';
  readonly anio = 2026;
  readonly estudiante = 'Antonela Jaimes';

  esModoOscuro = true;

  private platformId = inject(PLATFORM_ID);
  private renderer = inject(Renderer2);

  constructor(public authService: AuthService) {
    // Inicializar tema desde localStorage solo en browser después de hidratación
    if (isPlatformBrowser(this.platformId)) {
      afterNextRender(() => {
        const savedTheme = localStorage.getItem('lmll-theme');
        if (savedTheme) {
          this.esModoOscuro = savedTheme === 'dark';
          this.aplicarTema();
        }
      });
    }
  }

  alternarTema(): void {
    this.esModoOscuro = !this.esModoOscuro;
    this.aplicarTema();
  }

  private aplicarTema(): void {
    if (!isPlatformBrowser(this.platformId)) return;

    const body = document.body;
    if (this.esModoOscuro) {
      this.renderer.removeClass(body, 'theme-light');
      this.renderer.addClass(body, 'theme-dark');
      localStorage.setItem('lmll-theme', 'dark');
    } else {
      this.renderer.removeClass(body, 'theme-dark');
      this.renderer.addClass(body, 'theme-light');
      localStorage.setItem('lmll-theme', 'light');
    }
  }
}