-- Datos iniciales para pruebas con Postman
INSERT INTO categorias (nombre) VALUES ('Polos');
INSERT INTO categorias (nombre) VALUES ('Poleras');
INSERT INTO categorias (nombre) VALUES ('Pantalones');
INSERT INTO categorias (nombre) VALUES ('Accesorios');

INSERT INTO productos (categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES (1, 'Polo Oversize', 'Unisex', '/img/polo-oversize.jpg', 49.90, true);
INSERT INTO productos (categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES (2, 'Polera Hoodie', 'Hombre', '/img/hoodie.jpg', 89.90, true);
INSERT INTO productos (categoria_id, nombre, genero, imagen_url, precio_base, disponibilidad) VALUES (3, 'Jean Slim', 'Mujer', '/img/jean-slim.jpg', 119.00, true);

INSERT INTO usuarios (nombre, email, password, telefono, direccion) VALUES ('Admin', 'admin@email.com', 'admin123', '999999999', 'Lima, Peru');
