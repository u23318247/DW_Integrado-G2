// Service Worker para PWA (Progressive Web App) - La Moda te LLama
const CACHE_NAME = 'lmll-pwa-v1';
const ASSETS_TO_CACHE = [
  '/',
  '/manifest.json',
  '/css/bootstrap.min.css',
  '/reclamaciones',
  '/seguimiento',
  '/monitoreo'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE_NAME).then((cache) => {
      return cache.addAll(ASSETS_TO_CACHE).catch((e) => console.log('PWA cache prefetch:', e));
    })
  );
  self.skipWaiting();
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys().then((keys) => {
      return Promise.all(
        keys.map((key) => {
          if (key !== CACHE_NAME) {
            return caches.delete(key);
          }
        })
      );
    })
  );
  self.clients.claim();
});

self.addEventListener('fetch', (event) => {
  // Estrategia Network-First con fallback a Cache para que siempre tenga datos frescos
  event.respondWith(
    fetch(event.request).catch(() => {
      return caches.match(event.request);
    })
  );
});
