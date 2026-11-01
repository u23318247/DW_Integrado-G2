-- 1. Usuarios Iniciales (password: admin123 para todos con BCrypt)
INSERT INTO usuarios (id, nombre, email, password, telefono, direccion, rol, fecha_registro) 
VALUES 
(1, 'Admin', 'admin@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '919191919', 'Av. Central 123, Lima', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(2, 'Vendedor Oficial', 'vendedor@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '987654321', 'Gamarra 456, La Victoria', 'ROLE_VENDEDOR', CURRENT_TIMESTAMP),
(3, 'Almacenero Central', 'almacen@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '955443322', 'Almacén Central Lote 4, Lima', 'ROLE_ALMACENERO', CURRENT_TIMESTAMP),
(4, 'Repartidor Expreso', 'repartidor@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '911223344', 'Base de Despacho 12, Lima', 'ROLE_REPARTIDOR', CURRENT_TIMESTAMP),
(5, 'Cliente Frecuente', 'cliente@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '933221100', 'Av. Larco 789, Miraflores', 'ROLE_CLIENTE', CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 2. Categorías de Ropa
INSERT INTO categorias (id, nombre) VALUES 
(1, 'Superiores'),
(2, 'Polos'),
(3, 'Pantalones'),
(4, 'Calzado'),
(5, 'Accesorios'),
(6, 'Abrigos'),
(7, 'Gorras y Chullos'),
(8, 'Medias'),
(9, 'Joyería y Pines')
ON CONFLICT (id) DO NOTHING;

-- 3. Productos (Catálogo general con imágenes de alta definición)
INSERT INTO productos (id, categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES 
(1, 1, 'Sudadera con Capucha', 'Unisex', 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=900&auto=format&fit=crop&q=80', 89.90, true),
(2, 1, 'Camisa de lino', 'Hombre', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=900&auto=format&fit=crop&q=80', 51.50, true),
(3, 2, 'Polo Oversize', 'Unisex', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80', 49.90, true),
(4, 2, 'Polo Básico Premium', 'Unisex', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80', 39.90, false),
(5, 3, 'Jean Recto Clásico', 'Mujer', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80', 99.90, true),
(6, 3, 'Jean Skinny Ajustado', 'Hombre', 'https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=900&auto=format&fit=crop&q=80', 109.90, true),
(7, 4, 'Zapatilla Urbana', 'Unisex', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=900', 149.90, true),
(8, 4, 'Zapatilla Running', 'Unisex', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?auto=compress&cs=tinysrgb&w=900', 169.90, true),
(9, 5, 'Mochila Minimal', 'Unisex', 'https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg?auto=compress&cs=tinysrgb&w=900', 79.90, true),
(10, 6, 'Casaca Impermeable', 'Unisex', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=900&auto=format&fit=crop&q=80', 139.90, true),
(11, 1, 'Chompa con diseño', 'Mujer', 'https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=900&auto=format&fit=crop&q=80', 50.00, true),
(12, 5, 'Cangurera Streetwear', 'Unisex', 'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=900&auto=format&fit=crop&q=80', 69.90, true),
(13, 6, 'Casaca Denim Oversize', 'Unisex', 'https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=900&auto=format&fit=crop&q=80', 149.90, true),
(14, 7, 'Gorra Trucker LMTL', 'Unisex', 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=900&auto=format&fit=crop&q=80', 39.90, true),
(15, 2, 'Polo Graphic Anime', 'Unisex', 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=900&auto=format&fit=crop&q=80', 55.00, true),
(16, 3, 'Pantalón Cargo Black', 'Hombre', 'https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=900', 119.90, true),
(17, 8, 'Medias Altas Flame', 'Unisex', 'https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80', 19.90, true),
(18, 9, 'Cadena de Acero Cubana', 'Unisex', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80', 45.00, true)
ON CONFLICT (id) DO NOTHING;

-- 4. Detalles de Productos
INSERT INTO producto_detalles (id, producto_id, nombre_completo, codigo, imagen_url, marca, talla, color, descripcion, stock, precio_base, precio_adicional) VALUES 
(1, 1, 'Sudadera con Capucha Estándar', 'SUD-CAP-01', 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=900&auto=format&fit=crop&q=80', 'LMLL', 'M', 'Negro', 'Algodón con capucha', 50, 89.90, 0.00),
(2, 2, 'Camisa de lino Slim', 'CAM-LIN-02', 'https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=900&auto=format&fit=crop&q=80', 'LMLL', 'L', 'Blanco', 'Lino fresco de temporada', 40, 51.50, 0.00),
(3, 3, 'Polo Oversize Algodón', 'POL-OVE-03', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80', 'LMLL', 'XL', 'Negro', 'Corte suelto urbano', 35, 49.90, 0.00),
(4, 5, 'Jean Recto Celeste', 'JEA-REC-05', 'https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80', 'LMLL', '30', 'Celeste', 'Denim clásico resistente', 25, 99.90, 0.00),
(5, 7, 'Zapatilla Urbana Blanca', 'ZAP-URB-07', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=900', 'LMLL', '41', 'Blanco', 'Suela de caucho confortable', 15, 149.90, 0.00),
(6, 10, 'Casaca Impermeable Outdoor', 'CAS-IMP-10', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=900&auto=format&fit=crop&q=80', 'LMLL', 'L', 'Verde', 'Cortavientos impermeable', 20, 139.90, 0.00)
ON CONFLICT (id) DO NOTHING;

-- Ajustar secuencias de ID en PostgreSQL
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('categorias_id_seq', (SELECT MAX(id) FROM categorias));
SELECT setval('productos_id_seq', (SELECT MAX(id) FROM productos));
SELECT setval('producto_detalles_id_seq', (SELECT MAX(id) FROM producto_detalles));
