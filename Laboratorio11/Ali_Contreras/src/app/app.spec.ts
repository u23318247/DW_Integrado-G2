import { TestBed } from '@angular/core/testing';
import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
    }).compileComponents();
  });

  async function render() {
    const fixture = TestBed.createComponent(App);
    await fixture.whenStable();
    return { fixture, dom: fixture.nativeElement as HTMLElement };
  }

  it('crea la aplicacion', () => {
    const fixture = TestBed.createComponent(App);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('muestra el titulo y los componentes header, resumen y footer', async () => {
    const { dom } = await render();
    expect(dom.querySelector('h1')?.textContent).toContain('Panel de productos');
    expect(dom.querySelector('app-header')).toBeTruthy();
    expect(dom.querySelector('app-summary-panel')).toBeTruthy();
    expect(dom.querySelector('app-footer')).toBeTruthy();
  });

  it('renderiza 4 productos con @for y distingue los agotados', async () => {
    const { dom } = await render();
    expect(dom.querySelectorAll('.product-card').length).toBe(4);
    expect(dom.querySelectorAll('.product-card.out-of-stock').length).toBe(2);
    expect(dom.querySelectorAll('.tag--danger').length).toBe(2);
  });

  it('muestra las metricas de total y sin stock', async () => {
    const { dom } = await render();
    const valores = Array.from(dom.querySelectorAll('.metric__value')).map((e) => e.textContent?.trim());
    expect(valores).toEqual(['4', '2']);
  });

  it('el boton alterna la visibilidad del stock (event binding + @if)', async () => {
    const { fixture, dom } = await render();
    expect(dom.querySelectorAll('.stock').length).toBe(4);

    (dom.querySelector('.hero button') as HTMLButtonElement).click();
    await fixture.whenStable();

    expect(dom.querySelectorAll('.stock').length).toBe(0);
    expect(dom.querySelector('.hero button')?.textContent).toContain('Mostrar stock');
  });

  it('[(ngModel)] filtra el catalogo y muestra @empty sin coincidencias', async () => {
    const { fixture, dom } = await render();
    const input = dom.querySelector('.search input') as HTMLInputElement;

    input.value = 'lap';
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    expect(dom.querySelectorAll('.product-card').length).toBe(1);
    expect(dom.querySelector('.search__echo')?.textContent).toContain('lap');

    input.value = 'xyz';
    input.dispatchEvent(new Event('input'));
    await fixture.whenStable();
    expect(dom.querySelectorAll('.product-card').length).toBe(0);
    expect(dom.querySelector('.empty')).toBeTruthy();
  });
});
