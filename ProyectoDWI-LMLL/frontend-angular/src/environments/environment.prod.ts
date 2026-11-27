export const environment = {
    production: true,
    apiBaseUrl: '/api',
    defaultCurrency: 'PEN' as const,
    defaultExchangeRate: 3.75,
    storageKeys: {
        wishlist: 'llama_wishlist_ids',
        currency: 'llama_currency',
        cart: 'llama_cart',
    },
};