const SIZE_ORDER = ['XXS', 'XS', 'S', 'M', 'L', 'XL', 'XXL', 'XXXL', 'UNICA'];

export function normalizarTalla(talla: string): string {
    return (talla ?? '').trim().toUpperCase();
}

export function ordenarTallas(tallas: string[]): string[] {
    return [...new Set(tallas.map(normalizarTalla).filter(Boolean))].sort((a, b) => {
        const idxA = SIZE_ORDER.indexOf(a);
        const idxB = SIZE_ORDER.indexOf(b);
        if (idxA !== -1 && idxB !== -1) return idxA - idxB;
        if (idxA !== -1) return -1;
        if (idxB !== -1) return 1;

        const numA = Number(a);
        const numB = Number(b);
        if (!Number.isNaN(numA) && !Number.isNaN(numB)) return numA - numB;

        return a.localeCompare(b);
    });
}