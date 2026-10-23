import { Component } from '@angular/core';

@Component({
  selector: 'app-footer',
  standalone: true,
  templateUrl: './app-footer.html',
  styleUrl: './app-footer.scss'
})
export class AppFooter {
  readonly curso: string = 'Desarrollo Web Integrado';
  readonly anio: number = 2026;
  readonly estudiante: string = 'Antonela Jaimes';
}
