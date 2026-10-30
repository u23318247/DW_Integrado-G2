import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { RouterTestingHarness } from '@angular/router/testing';
import { App } from './app';
import { routes } from './app.routes';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter(routes)]
    }).compileComponents();
  });

  it('muestra el menú con Inicio, Productos y Categorías', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const enlaces = [...fixture.nativeElement.querySelectorAll('nav a')].map(
      (a: HTMLElement) => a.textContent?.trim()
    );
    expect(enlaces).toEqual(['Inicio', 'Productos', 'Categorías']);
  });
});

describe('Rutas', () => {
  let harness: RouterTestingHarness;

  beforeEach(async () => {
    TestBed.configureTestingModule({ providers: [provideRouter(routes)] });
    harness = await RouterTestingHarness.create();
  });

  const texto = () => (harness.routeNativeElement as HTMLElement).textContent ?? '';

  it('/ muestra Inicio', async () => {
    await harness.navigateByUrl('/');
    expect(texto()).toContain('Navegación y comunicación en Angular');
  });

  it('/productos recibe 3 tarjetas y destaca las de precio > 1000 (input)', async () => {
    await harness.navigateByUrl('/productos');
    const el = harness.routeNativeElement as HTMLElement;
    expect(el.querySelectorAll('app-producto-card').length).toBe(3);
    expect(el.querySelectorAll('.card.destacado').length).toBe(2);
  });

  it('Favorito dos veces incrementa el contador a 2 (output)', async () => {
    await harness.navigateByUrl('/productos');
    const el = harness.routeNativeElement as HTMLElement;
    const favorito = el.querySelector<HTMLButtonElement>('button.ghost')!;
    favorito.click();
    favorito.click();
    harness.detectChanges();
    expect(texto()).toContain('Favoritos seleccionados: 2');
  });

  it('Comparar cuenta productos sin repetir (Reto C)', async () => {
    await harness.navigateByUrl('/productos');
    const el = harness.routeNativeElement as HTMLElement;
    const botones = [...el.querySelectorAll<HTMLButtonElement>('button')].filter(
      b => b.textContent?.trim() === 'Comparar'
    );
    botones[0].click();
    botones[0].click();
    botones[1].click();
    harness.detectChanges();
    expect(texto()).toContain('En comparación: 2');
  });

  it('Ver detalle del id 2 navega a /productos/2 (output + Router)', async () => {
    await harness.navigateByUrl('/productos');
    const el = harness.routeNativeElement as HTMLElement;
    el.querySelectorAll<HTMLButtonElement>('app-producto-card')[1]
      .querySelector<HTMLButtonElement>('button')!.click();
    await harness.fixture.whenStable();
    expect(TestBed.inject(Router).url).toBe('/productos/2');
    expect(texto()).toContain('Monitor 27 pulgadas');
  });

  it('/productos/3 muestra Teclado mecánico', async () => {
    await harness.navigateByUrl('/productos/3');
    expect(texto()).toContain('Teclado mecánico');
  });

  it('/productos/999 muestra Producto no encontrado', async () => {
    await harness.navigateByUrl('/productos/999');
    expect(texto()).toContain('Producto no encontrado');
  });

  it('/categorias muestra las categorías únicas (Reto A)', async () => {
    await harness.navigateByUrl('/categorias');
    const items = (harness.routeNativeElement as HTMLElement).querySelectorAll('li');
    expect(items.length).toBe(2);
    expect(texto()).toContain('Tecnología');
    expect(texto()).toContain('Accesorios');
  });

  it('/xyz muestra la página 404', async () => {
    await harness.navigateByUrl('/xyz');
    expect(texto()).toContain('Ruta no encontrada');
  });
});
