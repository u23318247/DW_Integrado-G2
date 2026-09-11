package com.g2.dwi_lmll.config;

import com.g2.dwi_lmll.model.*;
import com.g2.dwi_lmll.model.enums.MetodoPago;
import com.g2.dwi_lmll.model.enums.TipoComprobante;
import com.g2.dwi_lmll.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final VentaRepository ventaRepository;

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() > 0) return;

        // Categorías
        Categoria polos = categoriaRepository.save(Categoria.builder().nombre("Polos").build());
        Categoria poleras = categoriaRepository.save(Categoria.builder().nombre("Poleras").build());
        Categoria pantalones = categoriaRepository.save(Categoria.builder().nombre("Pantalones").build());
        Categoria casacas = categoriaRepository.save(Categoria.builder().nombre("Casacas").build());
        Categoria accesorios = categoriaRepository.save(Categoria.builder().nombre("Accesorios").build());

        // Productos
        productoRepository.save(Producto.builder()
                .nombre("Polo Oversize Urban Llama")
                .genero("Unisex")
                .imagenUrl("https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=500")
                .precioBase(new BigDecimal("49.90"))
                .disponibilidad(true)
                .categoria(polos)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Polera Hoodie Alpaca Style")
                .genero("Hombre")
                .imagenUrl("https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=500")
                .precioBase(new BigDecimal("89.90"))
                .disponibilidad(true)
                .categoria(poleras)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Jogger Cargo Streetwear")
                .genero("Mujer")
                .imagenUrl("https://images.unsplash.com/photo-1517445312882-bc9910d016b7?w=500")
                .precioBase(new BigDecimal("79.90"))
                .disponibilidad(true)
                .categoria(pantalones)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Casaca Denim Bordada")
                .genero("Unisex")
                .imagenUrl("https://images.unsplash.com/photo-1551028719-00167b16eac5?w=500")
                .precioBase(new BigDecimal("129.90"))
                .disponibilidad(true)
                .categoria(casacas)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Gorra Llama Streetwear")
                .genero("Unisex")
                .imagenUrl("https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=500")
                .precioBase(new BigDecimal("39.90"))
                .disponibilidad(true)
                .categoria(accesorios)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Polo Básico Algodón Pima")
                .genero("Hombre")
                .imagenUrl("https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=500")
                .precioBase(new BigDecimal("39.90"))
                .disponibilidad(true)
                .categoria(polos)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Polera Crop Top Moda")
                .genero("Mujer")
                .imagenUrl("https://images.unsplash.com/photo-1503342217505-b0a15ec3261c?w=500")
                .precioBase(new BigDecimal("69.90"))
                .disponibilidad(true)
                .categoria(poleras)
                .build());

        productoRepository.save(Producto.builder()
                .nombre("Pantalón Jeans Slim Fit")
                .genero("Hombre")
                .imagenUrl("https://images.unsplash.com/photo-1542272604-780c96856592?w=500")
                .precioBase(new BigDecimal("89.90"))
                .disponibilidad(true)
                .categoria(pantalones)
                .build());

        // Usuario inicial
        Usuario usuario = new Usuario();
        usuario.setNombre("Antonela Jaimes");
        usuario.setEmail("antonela@example.com");
        usuario.setTelefono("987654321");
        usuario.setDireccion("Lima, Perú");
        usuario.setPassword("123456");
        usuarioRepository.save(usuario);

        // Pedido inicial
        Pedido pedido = new Pedido();
        pedido.setUsuarioId(usuario.getId());
        pedido.setTotal(new BigDecimal("139.80"));
        pedido.setEstado(EstadoPedido.PAGADO);
        pedidoRepository.save(pedido);

        // Venta inicial
        Venta venta = new Venta();
        venta.setPedido(pedido);
        venta.setTipoComprobante(TipoComprobante.BOLETA);
        venta.setSerieCorrelativo("B001-000001");
        venta.setMetodoPago(MetodoPago.YAPE);
        venta.setFechaEmision(LocalDateTime.now());
        venta.setSubtotal(new BigDecimal("118.47"));
        venta.setIgv(new BigDecimal("21.33"));
        venta.setTotal(new BigDecimal("139.80"));
        ventaRepository.save(venta);
    }
}
