import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-summary-panel',
  standalone: true,
  templateUrl: './summary-panel.html',
  styleUrl: './summary-panel.scss'
})
export class SummaryPanel {
  @Input() totalProductos: number = 0;
  @Input() totalDisponibles: number = 0;
  @Input() totalSinStock: number = 0;
}
