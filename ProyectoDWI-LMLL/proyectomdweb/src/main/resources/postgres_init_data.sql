-- 1. Usuarios Iniciales (password: admin123 y vendedor123 con BCrypt)
INSERT INTO usuarios (id, nombre, email, password, telefono, direccion, rol, fecha_registro) 
VALUES 
(1, 'Admin', 'admin@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '919191919', 'Av. Central 123, Lima', 'ROLE_ADMIN', CURRENT_TIMESTAMP),
(2, 'Vendedor Oficial', 'vendedor@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '987654321', 'Gamarra 456, La Victoria', 'ROLE_VENDEDOR', CURRENT_TIMESTAMP)
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

-- 3. Productos (Catálogo general)
INSERT INTO productos (id, categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES 
(1, 1, 'Sudadera con Capucha', 'Unisex', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZsD55MPh_Liz6sRG7MWs3iRR_R09tpw2NGQ&s', 89.90, true),
(2, 1, 'Camisa de lino', 'Hombre', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGrK3YkiHdKqY5WMn5pYK_tEGyzU8VPs6rPinhHcjxhA&s', 51.50, true),
(3, 2, 'Polo Oversize', 'Unisex', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&auto=format&fit=crop&q=60', 49.90, true),
(4, 2, 'Polo Básico Premium', 'Unisex', 'https://www.basisperu.com/cdn/shop/files/polos_basicos_hombre_denim_basis.png?v=1741728393&width=1445', 39.90, false),
(5, 3, 'Jean Recto Clásico', 'Mujer', 'https://sydney.pe/wp-content/uploads/2025/02/jeans-clasico-celeste.jpg', 99.90, true),
(6, 3, 'Jean Skinny Ajustado', 'Hombre', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT6vrQJzqb16V0y3Kb8lMVdWALT9ZZAk1YTg&s', 109.90, true),
(7, 4, 'Zapatilla Urbana', 'Unisex', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg', 149.90, true),
(8, 4, 'Zapatilla Running', 'Unisex', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg', 169.90, true),
(9, 5, 'Mochila Minimal', 'Unisex', 'https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg', 79.90, true),
(10, 6, 'Casaca Impermeable', 'Unisex', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=600&auto=format&fit=crop&q=60', 139.90, true),
(11, 1, 'Chompa con diseño', 'Mujer', 'https://media.falabella.com/tottusPE/43575253_2/w=1500,h=1500,fit=cover', 50.00, true),
(12, 5, 'Cangurera Streetwear', 'Unisex', 'https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSGitrghWoteaqPgukD4X3IU6ICCRahx5GZ53xNkxUN4JXJjgVx', 69.90, true),
(13, 6, 'Casaca Denim Oversize', 'Unisex', 'https://images.pexels.com/photos/1566412/pexels-photo-15166412.jpeg?auto=compress&cs=tinysrgb&w=600', 149.90, true),
(14, 7, 'Gorra Trucker LMTL', 'Unisex', 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600&auto=format&fit=crop&q=60', 39.90, true),
(15, 2, 'Polo Graphic Anime', 'Unisex', 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=60', 55.00, true),
(16, 3, 'Pantalón Cargo Black', 'Hombre', 'https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=600', 119.90, true),
(17, 8, 'Medias Altas Flame', 'Unisex', 'https://images.unsplash.com/photo-1582966772680-860e372bb558?w=600&auto=format&fit=crop&q=60', 19.90, true),
(18, 9, 'Cadena de Acero Cubana', 'Unisex', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600&auto=format&fit=crop&q=60', 45.00, true)
ON CONFLICT (id) DO NOTHING;

-- 4. Detalles de Productos
INSERT INTO producto_detalles (id, producto_id, nombre_completo, codigo, imagen_url, marca, talla, color, descripcion, stock, precio_base, precio_adicional) VALUES 
(1, 1, 'Sudadera con Capucha Estándar', 'SUD-CAP-01', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZsD55MPh_Liz6sRG7MWs3iRR_R09tpw2NGQ&s', 'LMLL', 'M', 'Negro', 'Algodón con capucha', 50, 89.90, 0.00),
(2, 2, 'Camisa de lino Slim', 'CAM-LIN-02', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGrK3YkiHdKqY5WMn5pYK_tEGyzU8VPs6rPinhHcjxhA&s', 'LMLL', 'L', 'Blanco', 'Lino fresco de temporada', 40, 51.50, 0.00),
(3, 3, 'Polo Oversize Algodón', 'POL-OVE-03', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&auto=format&fit=crop&q=60', 'LMLL', 'XL', 'Negro', 'Corte suelto urbano', 35, 49.90, 0.00),
(4, 5, 'Jean Recto Celeste', 'JEA-REC-05', 'https://sydney.pe/wp-content/uploads/2025/02/jeans-clasico-celeste.jpg', 'LMLL', '30', 'Celeste', 'Denim clásico resistente', 25, 99.90, 0.00),
(5, 7, 'Zapatilla Urbana Blanca', 'ZAP-URB-07', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg', 'LMLL', '41', 'Blanco', 'Suela de caucho confortable', 15, 149.90, 0.00),
(6, 10, 'Casaca Impermeable Outdoor', 'CAS-IMP-10', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=600&auto=format&fit=crop&q=60', 'LMLL', 'L', 'Verde', 'Cortavientos impermeable', 20, 139.90, 0.00)
ON CONFLICT (id) DO NOTHING;

-- Ajustar secuencias de ID en PostgreSQL
SELECT setval('usuarios_id_seq', (SELECT MAX(id) FROM usuarios));
SELECT setval('categorias_id_seq', (SELECT MAX(id) FROM categorias));
SELECT setval('productos_id_seq', (SELECT MAX(id) FROM productos));
SELECT setval('producto_detalles_id_seq', (SELECT MAX(id) FROM producto_detalles));
