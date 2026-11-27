import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-top-announcement',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './top-announcement.html',
  styleUrl: './top-announcement.scss'
})
export class TopAnnouncement {
  readonly mensajes = [
    '✨ ENVÍO GRATIS a todo el Perú en compras mayores a S/ 199',
    '🧵 Prendas en Algodón 100% Peruano Tangüis & Pima',
    '💳 3 Cuotas sin intereses con todas las tarjetas & Yape / Plin',
    '🔄 30 días para cambio de talla sin costo adicional'
  ];
}
