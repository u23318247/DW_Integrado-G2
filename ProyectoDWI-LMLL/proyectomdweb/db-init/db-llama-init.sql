-- 1. Tabla de Usuarios
CREATE TABLE usuarios (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(60) NOT NULL,
    email           VARCHAR(150) UNIQUE NOT NULL,
    password        VARCHAR(100) NOT NULL,
    telefono        VARCHAR(12),
    direccion       TEXT,
    fecha_registro  DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla de Categorías
CREATE TABLE categorias (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(20) NOT NULL
);

-- 3. Tabla de Productos (Catálogo general)
CREATE TABLE productos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id    INT,
    nombre          VARCHAR(40) NOT NULL,
    genero          VARCHAR(10) NOT NULL,
    imagen_url      VARCHAR(255) NOT NULL, 
    precio_base     DECIMAL(8, 2) NOT NULL, 
    disponibilidad  BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE SET NULL
);

-- 4. Tabla de Detalles (Inventario Real / SKU)
CREATE TABLE producto_detalles (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    producto_id     INT NOT NULL,
    nombre_completo VARCHAR(70) NOT NULL,
    codigo          VARCHAR(20) UNIQUE NOT NULL, 
    imagen_url       VARCHAR(255) NOT NULL,
    marca           VARCHAR(20) NOT NULL,
    talla           VARCHAR(10) NOT NULL,
    color           VARCHAR(20) NOT NULL,
    descripcion     VARCHAR(100) NOT NULL,
    stock           INT DEFAULT 0,
    precio_base     DECIMAL(8, 2) NOT NULL,
    precio_adicional DECIMAL(7, 2) DEFAULT 0.00, 
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE
);

-- 5. Tabla de Pedidos (Cabecera de la intención de compra)
CREATE TABLE pedidos (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id      INT NOT NULL,
    total           DECIMAL(8, 2) NOT NULL,
    estado          ENUM('PENDIENTE', 'PAGADO', 'ENVIADO', 'CANCELADO') DEFAULT 'PENDIENTE',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 6. Tabla de Detalles de Pedido (Lo que el cliente puso en el carrito)
CREATE TABLE pedido_detalles (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id           INT NOT NULL,
    producto_detalle_id INT NOT NULL, 
    cantidad            INT NOT NULL,
    precio_unitario     DECIMAL(10, 2) NOT NULL,
    fecha_pedido        DATETIME DEFAULT CURRENT_TIMESTAMP, 
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_detalle_id) REFERENCES producto_detalles(id)
);

-- 7. Tabla de Ventas (Cabecera del comprobante)
CREATE TABLE ventas (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    pedido_id           INT NOT NULL UNIQUE,
    tipo_comprobante    ENUM('BOLETA', 'FACTURA') NOT NULL,
    serie_correlativo   VARCHAR(20) NOT NULL, -- Ej: B001-000145
    metodo_pago         ENUM('EFECTIVO', 'TARJETA_DEBITO', 'TARJETA_CREDITO', 'YAPE', 'PAYPAL') NOT NULL,
    fecha_emision       DATETIME DEFAULT CURRENT_TIMESTAMP,
    subtotal            DECIMAL(8, 2) NOT NULL,
    igv                 DECIMAL(8, 2) NOT NULL,     
    total               DECIMAL(8, 2) NOT NULL,  
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE RESTRICT
);

-- 8. Tabla de Detalles de Venta (Las líneas dentro de la boleta)
CREATE TABLE ventas_detalles (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    venta_id            INT NOT NULL,
    producto_detalle_id INT NOT NULL,
    cantidad            INT NOT NULL,
    precio_unitario     DECIMAL(8, 2) NOT NULL,
    subtotal            DECIMAL(8, 2) NOT NULL, -- cantidad * precio_unitario
    FOREIGN KEY (venta_id) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_detalle_id) REFERENCES producto_detalles(id)
);


-- INSERCIONES 

INSERT INTO usuarios (id, nombre, email, password, telefono, direccion, fecha_registro) 
VALUES (1, 'Admin', 'admin@email.com', '$2a$10$HUqJ0zRrUSPqtKiLpEDe3.FJEQjvnbTomkp1TPvYZSA8Pw5eTZmvy', '919191919', 'Av. ubicacion, Lima', '2025-01-10 08:00:00');

--Categorías 
INSERT INTO categorias (id, nombre) VALUES 
(1, 'Superiores'),
(2, 'Polos'),
(3, 'Pantalones'),
(4, 'Calzado'),
(5, 'Accesorios'),
(6, 'Abrigos'),
(7, 'Gorras y Chullos'),
(8, 'Medias'),
(9, 'Joyería y Pines');

--Catálogo
INSERT INTO productos (id, categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES 
(1, 1, 'Sudadera con Cappucha', 'Unisex', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZsD55MPh_Liz6sRG7MWs3iRR_R09tpw2NGQ&s', 89.90, 1),
(2, 1, 'Camisa de lino', 'Hombre', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGrK3YkiHdKqY5WMn5pYK_tEGyzU8VPs6rPinhHcjxhA&s', 51.50, 1),
(3, 2, 'Polo Oversize', 'Unisex', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2FtaXNldGF8ZW58MHx8MHx8fDA%3D', 49.90, 1),
(4, 2, 'Polo Básico Premium', 'Unisex', 'https://www.basisperu.com/cdn/shop/files/polos_basicos_hombre_denim_basis.png?v=1741728393&width=1445', 39.90, 0),
(5, 3, 'Jean Recto Clásico', 'Mujer', 'https://sydney.pe/wp-content/uploads/2025/02/jeans-clasico-celeste.jpg', 99.90, 1),
(6, 3, 'Jean Skinny Ajustado', 'Hombre', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT6vrQJzqb16V0y3Kb8lMVdWALT9ZZAk1YTg&s', 109.90, 1),
(7, 4, 'Zapatilla Urbana', 'Unisex', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg', 149.90, 1),
(8, 4, 'Zapatilla Running', 'Unisex', 'https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg', 169.90, 1),
(9, 5, 'Mochila Minimal', 'Unisex', 'https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg', 79.90, 1),
(10, 6, 'Casaca Impermeable', 'Unisex', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8Y2FzYWNhfGVufDB8fDB8fHww', 139.90, 1),
(11, 1, 'chompa con diseño', 'Mujer', 'https://media.falabella.com/tottusPE/43575253_2/w=1500,h=1500,fit=cover', 50.00, 1),
(12, 5, 'Cangurera Streetwear', 'Unisex', 'https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSGitrghWoteaqPgukD4X3IU6ICCRahx5GZ53xNkxUN4JXJjgVx', 69.90, 1),
(13, 6, 'Casaca Denim Oversize', 'Unisex', 'https://images.pexels.com/photos/1566412/pexels-photo-15166412.jpeg?auto=compress&cs=tinysrgb&w=600', 149.90, 1),
(14, 7, 'Gorra Trucker LMTL', 'Unisex', 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600&auto=format&fit=crop&q=60', 39.90, 1),
(15, 2, 'Polo Graphic Anime', 'Unisex', 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=60', 55.00, 1),
(16, 3, 'Pantalón Cargo Black', 'Hombre', 'https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=600', 119.90, 1),
(17, 8, 'Medias Altas Flame', 'Unisex', 'https://images.unsplash.com/photo-1582966772680-860e372bb558?w=600&auto=format&fit=crop&q=60', 19.90, 1),
(18, 9, 'Cadena de Acero Cubana', 'Unisex', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600&auto=format&fit=crop&q=60', 45.00, 1);

-- 4. detalles
INSERT INTO producto_detalles (id, producto_id, nombre_completo, codigo, imagen_url, marca, talla, color, descripcion, stock, precio_base, precio_adicional) VALUES 
(1, 1, 'Sudadera con Capucha Estándar', 'SUD-CAP-01', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZsD55MPh_Liz6sRG7MWs3iRR_R09tpw2NGQ&s', 'Generico', 'M', 'Negro', 'Algodón con capucha', 50, 89.90, 0.00),
(2, 2, 'Camisa de lino Slim', 'CAM-LIN-02', 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGrK3YkiHdKqY5WMn5pYK_tEGyzU8VPs6rPinhHcjxhA&s', 'Generico', 'L', 'Blanco', 'Lino fresco de temporada', 40, 51.50, 0.00),
(3, 3, 'Polo Oversize Algodón', 'POL-OVE-03', 'https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8Y2FtaXNldGF8ZW58MHx8MHx8fDA%3D', 'Generico', 'XL', 'Negro', 'Corte suelto urbano', 35, 49.90, 0.00),
(4, 5, 'Jean Recto Celeste', 'JEA-REC-05', 'https://sydney.pe/wp-content/uploads/2025/02/jeans-clasico-celeste.jpg', 'Generico', '30', 'Celeste', 'Denim clásico resistente', 25, 99.90, 0.00),
(5, 7, 'Zapatilla Urbana Blanca', 'ZAP-URB-07', 'https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg', 'Generico', '41', 'Blanco', 'Suela de caucho confortable', 15, 149.90, 0.00),
(6, 10, 'Casaca Impermeable Outdoor', 'CAS-IMP-10', 'https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=600&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Nnx8Y2FzYWNhfGVufDB8fDB8fHww', 'Generico', 'L', 'Verde', 'Cortavientos impermeable', 20, 139.90, 0.00),
(7, 12, 'Cangurera Negra Reflectiva', 'CAN-NEG-ST', 'https://images.unsplash.com/photo-1622560480605-d83c853bc5c3?w=600&auto=format&fit=crop&q=60', 'La Moda te Llama', 'Estándar', 'Negro', 'Cangurera con correas ajustables y detalles reflectivos', 15, 69.90, 0.00),
(8, 13, 'Casaca Denim Azul Claro L', 'CAS-DEN-AZL', 'https://images.pexels.com/photos/1566412/pexels-photo-15166412.jpeg?auto=compress&cs=tinysrgb&w=600', 'La Moda te Llama', 'L', 'Azul Claro', 'Casaca jean rígida corte oversize retro', 20, 149.90, 0.00),
(9, 13, 'Casaca Denim Azul Claro XL', 'CAS-DEN-AZXL', 'https://images.pexels.com/photos/1566412/pexels-photo-15166412.jpeg?auto=compress&cs=tinysrgb&w=600', 'La Moda te Llama', 'XL', 'Azul Claro', 'Casaca jean rígida corte oversize retro extra grande', 12, 149.90, 10.00),
(10, 14, 'Gorra Trucker Vintage Negra', 'GOR-TRU-NEG', 'https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600&auto=format&fit=crop&q=60', 'La Moda te Llama', 'Estándar', 'Negro', 'Gorra con malla trasera y regulador de presión', 30, 39.90, 0.00),
(11, 15, 'Polo Graphic White M', 'POL-GRA-BWM', 'https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=60', 'La Moda te Llama', 'M', 'Blanco', 'Polo de algodón 20/1 con estampado en serigrafía', 25, 55.00, 0.00),
(12, 16, 'Pantalón Cargo Black Talla 32', 'CAR-BLK-32', 'https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=600', 'La Moda te Llama', '32', 'Negro', 'Pantalón cargo de gabardina con 6 bolsillos', 18, 119.90, 0.00),
(13, 17, 'Medias Altas Flame White', 'MED-FLA-WH', 'https://images.unsplash.com/photo-1582966772680-860e372bb558?w=600&auto=format&fit=crop&q=60', 'La Moda te Llama', 'M', 'Blanco/Fuego', 'Medias urbanas caña alta tejido grueso', 40, 19.90, 0.00),
(14, 18, 'Cadena Acero Cubana 55cm', 'JOY-CUB-55', 'https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600&auto=format&fit=crop&q=60', 'Generico', '55 cm', 'Plata', 'Cadena de eslabones cubanos acero quirúrgico 316L', 15, 45.00, 0.00);

-- Ventas 2025
INSERT INTO pedidos (id, usuario_id, total, estado) VALUES 
(1, 1, 89.90, 'PAGADO'),   (2, 1, 51.50, 'PAGADO'),   (3, 1, 49.90, 'PAGADO'),
(4, 1, 99.90, 'PAGADO'),   (5, 1, 149.90, 'PAGADO'),  (6, 1, 139.90, 'PAGADO'),
(7, 1, 179.80, 'PAGADO'),  (8, 1, 103.00, 'PAGADO'),  (9, 1, 49.90, 'PAGADO'),
(10, 1, 99.90, 'PAGADO'),  (11, 1, 149.90, 'PAGADO'), (12, 1, 139.90, 'PAGADO'),
(13, 1, 89.90, 'PAGADO'),  (14, 1, 51.50, 'PAGADO'),  (15, 1, 249.80, 'PAGADO'),
(16, 1, 99.90, 'PAGADO'),  (17, 1, 149.90, 'PAGADO'), (18, 1, 139.90, 'PAGADO');

-- Ventas 2026
INSERT INTO pedidos (id, usuario_id, total, estado) VALUES 
(19, 1, 89.90, 'PAGADO'),  (20, 1, 103.00, 'PAGADO'), (21, 1, 49.90, 'PAGADO'),
(22, 1, 199.80, 'PAGADO'), (23, 1, 149.90, 'PAGADO'), (24, 1, 139.90, 'PAGADO'),
(25, 1, 89.90, 'PAGADO'),  (26, 1, 51.50, 'PAGADO'),  (27, 1, 49.90, 'PAGADO'),
(28, 1, 99.90, 'PAGADO'),  (29, 1, 289.80, 'PAGADO'), (30, 1, 139.90, 'PAGADO');

INSERT INTO pedido_detalles (pedido_id, producto_detalle_id, cantidad, precio_unitario, fecha_pedido) VALUES 
-- 2025
(1, 1, 1, 89.90, '2025-01-15 10:30:00'),
(2, 2, 1, 51.50, '2025-01-22 14:15:00'),
(3, 3, 1, 49.90, '2025-02-05 16:45:00'),
(4, 4, 1, 99.90, '2025-02-18 11:20:00'),
(5, 5, 1, 149.90, '2025-03-12 09:10:00'),
(6, 6, 1, 139.90, '2025-03-29 18:35:00'),
(7, 1, 2, 89.90, '2025-04-14 15:22:00'),
(8, 2, 2, 51.50, '2025-05-03 13:40:00'),
(9, 3, 1, 49.90, '2025-06-21 20:15:00'),
(10, 4, 1, 99.90, '2025-07-08 10:05:00'),
(11, 5, 1, 149.90, '2025-08-19 17:50:00'),
(12, 6, 1, 139.90, '2025-09-02 12:14:00'),
(13, 1, 1, 89.90, '2025-10-10 14:30:00'),
(14, 2, 1, 51.50, '2025-10-25 19:45:00'),
(15, 4, 1, 99.90, '2025-11-15 11:00:00'),
(15, 5, 1, 149.90, '2025-11-15 11:00:00'),
(16, 4, 1, 99.90, '2025-11-28 16:20:00'),
(17, 5, 1, 149.90, '2025-12-12 10:15:00'),
(18, 6, 1, 139.90, '2025-12-23 21:00:00'),
-- 2026
(19, 1, 1, 89.90, '2026-01-10 11:15:00'),
(20, 2, 2, 51.50, '2026-01-28 15:45:00'),
(21, 3, 1, 49.90, '2026-02-14 18:20:00'),
(22, 4, 2, 99.90, '2026-02-27 10:30:00'),
(23, 5, 1, 149.90, '2026-03-16 14:10:00'),
(24, 6, 1, 139.90, '2026-03-30 17:25:00'),
(25, 1, 1, 89.90, '2026-04-12 09:40:00'),
(26, 2, 1, 51.50, '2026-04-25 13:55:00'),
(27, 3, 1, 49.90, '2026-05-08 16:15:00'),
(28, 4, 1, 99.90, '2026-05-22 11:50:00'),
(29, 5, 1, 149.90, '2026-06-02 15:20:00'),
(29, 6, 1, 139.90, '2026-06-02 15:20:00'),
(30, 6, 1, 139.90, '2026-06-05 19:10:00');


-- COMPROBANTES

INSERT INTO ventas (pedido_id, tipo_comprobante, serie_correlativo, metodo_pago, fecha_emision, subtotal, igv, total) VALUES 
-- 2025
(1, 'BOLETA', 'B001-000001', 'YAPE', '2025-01-15 10:32:00', 76.19, 13.71, 89.90),
(2, 'FACTURA', 'F001-000001', 'TARJETA_CREDITO', '2025-01-22 14:18:00', 43.64, 7.86, 51.50),
(3, 'BOLETA', 'B001-000002', 'EFECTIVO', '2025-02-05 16:46:00', 42.29, 7.61, 49.90),
(4, 'BOLETA', 'B001-000003', 'YAPE', '2025-02-18 11:22:00', 84.66, 15.24, 99.90),
(5, 'FACTURA', 'F001-000002', 'TARJETA_DEBITO', '2025-03-12 09:15:00', 127.03, 22.87, 149.90),
(6, 'BOLETA', 'B001-000004', 'YAPE', '2025-03-29 18:36:00', 118.56, 21.34, 139.90),
(7, 'BOLETA', 'B001-000005', 'EFECTIVO', '2025-04-14 15:25:00', 152.37, 27.43, 179.80),
(8, 'FACTURA', 'F001-000003', 'TARJETA_CREDITO', '2025-05-03 13:42:00', 87.29, 15.71, 103.00),
(9, 'BOLETA', 'B001-000006', 'PAYPAL', '2025-06-21 20:18:00', 42.29, 7.61, 49.90),
(10, 'BOLETA', 'B001-000007', 'YAPE', '2025-07-08 10:10:00', 84.66, 15.24, 99.90),
(11, 'FACTURA', 'F001-000004', 'TARJETA_DEBITO', '2025-08-19 17:55:00', 127.03, 22.87, 149.90),
(12, 'BOLETA', 'B001-000008', 'YAPE', '2025-09-02 12:18:00', 118.56, 21.34, 139.90),
(13, 'BOLETA', 'B001-000009', 'EFECTIVO', '2025-10-10 14:35:00', 76.19, 13.71, 89.90),
(14, 'FACTURA', 'F001-000005', 'TARJETA_CREDITO', '2025-10-25 19:46:00', 43.64, 7.86, 51.50),
(15, 'FACTURA', 'F001-000006', 'TARJETA_DEBITO', '2025-11-15 11:05:00', 211.69, 38.11, 249.80),
(16, 'BOLETA', 'B001-000010', 'YAPE', '2025-11-28 16:22:00', 84.66, 15.24, 99.90),
(17, 'BOLETA', 'B001-000011', 'EFECTIVO', '2025-12-12 10:18:00', 127.03, 22.87, 149.90),
(18, 'FACTURA', 'F001-000007', 'TARJETA_CREDITO', '2025-12-23 21:05:00', 118.56, 21.34, 139.90),
-- 2026
(19, 'BOLETA', 'B001-000012', 'YAPE', '2026-01-10 11:18:00', 76.19, 13.71, 89.90),
(20, 'FACTURA', 'F001-000008', 'TARJETA_CREDITO', '2026-01-28 15:46:00', 87.29, 15.71, 103.00),
(21, 'BOLETA', 'B001-000013', 'EFECTIVO', '2026-02-14 18:22:00', 42.29, 7.61, 49.90),
(22, 'BOLETA', 'B001-000014', 'YAPE', '2026-02-27 10:35:00', 169.32, 30.48, 199.80),
(23, 'FACTURA', 'F001-000009', 'TARJETA_DEBITO', '2026-03-16 14:15:00', 127.03, 22.87, 149.90),
(24, 'BOLETA', 'B001-000015', 'YAPE', '2026-03-30 17:28:00', 118.56, 21.34, 139.90),
(25, 'BOLETA', 'B001-000016', 'EFECTIVO', '2026-04-12 09:42:00', 76.19, 13.71, 89.90),
(26, 'FACTURA', 'F001-000010', 'TARJETA_CREDITO', '2026-04-25 13:58:00', 43.64, 7.86, 51.50),
(27, 'BOLETA', 'B001-000017', 'PAYPAL', '2026-05-08 16:18:00', 42.29, 7.61, 49.90),
(28, 'BOLETA', 'B001-000018', 'YAPE', '2026-05-22 11:52:00', 84.66, 15.24, 99.90),
(29, 'FACTURA', 'F001-000011', 'TARJETA_DEBITO', '2026-06-02 15:25:00', 245.59, 44.21, 289.80),
(30, 'BOLETA', 'B001-000019', 'YAPE', '2026-06-05 19:12:00', 118.56, 21.34, 139.90);


-- DETALLES DE VENTA

INSERT INTO ventas_detalles (venta_id, producto_detalle_id, cantidad, precio_unitario, subtotal) VALUES 
-- 2025
(1, 1, 1, 76.19, 76.19),     (2, 2, 1, 43.64, 43.64),     (3, 3, 1, 42.29, 42.29),
(4, 4, 1, 84.66, 84.66),     (5, 5, 1, 127.03, 127.03),   (6, 6, 1, 118.56, 118.56),
(7, 1, 2, 76.19, 152.37),    (8, 2, 2, 43.64, 87.29),     (9, 3, 1, 42.29, 42.29),
(10, 4, 1, 84.66, 84.66),    (11, 5, 1, 127.03, 127.03),  (12, 6, 1, 118.56, 118.56),
(13, 1, 1, 76.19, 76.19),    (14, 2, 1, 43.64, 43.64),    (15, 4, 1, 84.66, 84.66),
(15, 5, 1, 127.03, 127.03),  (16, 4, 1, 84.66, 84.66),    (17, 5, 1, 127.03, 127.03),
(18, 6, 1, 118.56, 118.56),
-- 2026
(19, 1, 1, 76.19, 76.19),    (20, 2, 2, 43.64, 87.29),    (21, 3, 1, 42.29, 42.29),
(22, 4, 2, 84.66, 169.32),   (23, 5, 1, 127.03, 127.03),  (24, 6, 1, 118.56, 118.56),
(25, 1, 1, 76.19, 76.19),    (26, 2, 1, 43.64, 43.64),    (27, 3, 1, 42.29, 42.29),
(28, 4, 1, 84.66, 84.66),    (29, 5, 1, 127.03, 127.03),  (29, 6, 1, 118.56, 118.56),
(30, 6, 1, 118.56, 118.56);