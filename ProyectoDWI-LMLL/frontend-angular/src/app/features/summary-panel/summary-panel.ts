import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-summary-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './summary-panel.html',
  styleUrl: './summary-panel.scss'
})
export class SummaryPanel {
  @Input() totalProductos: number = 0;
  @Input() totalDisponibles: number = 0;
  @Input() totalSinStock: number = 0;
  @Input() articulosEnBolsa: number = 0;
}
