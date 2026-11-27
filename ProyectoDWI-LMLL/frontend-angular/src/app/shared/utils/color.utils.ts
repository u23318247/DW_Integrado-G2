import { slugify } from './slug.utils';

const COLOR_HEX_MAP: Record<string, string> = {
    negro: '#111827',
    blanco: '#f9fafb',
    rojo: '#dc2626',
    azul: '#2563eb',
    verde: '#16a34a',
    amarillo: '#eab308',
    rosa: '#ec4899',
    morado: '#9333ea',
    gris: '#6b7280',
    beige: '#d6cbb7',
    marron: '#78350f',
    oro: '#d4af37',
    plata: '#cbd5e1',
};

const DEFAULT_HEX = '#64748b';

export function obtenerHexColor(colorName: string): string {
    const key = slugify(colorName).replace(/-/g, '');
    return COLOR_HEX_MAP[key] ?? DEFAULT_HEX;
}