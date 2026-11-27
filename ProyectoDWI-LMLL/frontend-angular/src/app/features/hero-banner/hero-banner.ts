import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hero-banner',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './hero-banner.html',
  styleUrl: './hero-banner.scss'
})
export class HeroBanner {
  @Output() verCatalogo = new EventEmitter<void>();
  @Output() verOfertas = new EventEmitter<void>();

  onVerCatalogo(): void {
    this.verCatalogo.emit();
  }

  onVerOfertas(): void {
    this.verOfertas.emit();
  }
}
