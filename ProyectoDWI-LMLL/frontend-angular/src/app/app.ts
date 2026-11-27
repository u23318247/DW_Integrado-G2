import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppHeader } from './shared/app-header/app-header';
import { AppFooter } from './shared/app-footer/app-footer';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    AppHeader,
    AppFooter
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {}