package com.proyectomdweb.proyectomdweb.config;

import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.model.ProductoDetalle;
import com.proyectomdweb.proyectomdweb.repository.CategoriaRepository;
import com.proyectomdweb.proyectomdweb.repository.ProductoDetalleRepository;
import com.proyectomdweb.proyectomdweb.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Laboratorio 6 (Persistencia JPA) integrado en el e-commerce:
 * al iniciar la aplicación garantiza el catálogo de 18 productos generales con
 * exactamente 20 variantes cada uno (360 prendas en producto_detalles).
 * Las variantes se guardan en CASCADA a través de Producto.variantes (CascadeType.ALL).
 * Es idempotente: solo completa lo que falta, nunca duplica.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "catalogo.inicializar", havingValue = "true", matchIfMissing = true)
public class CatalogoInicializador implements CommandLineRunner {

    public static final int VARIANTES_POR_PRODUCTO = 20;

    // Una variante por producto queda con stock crítico (<= 25) para la alerta del dashboard
    private static final int INDICE_STOCK_CRITICO = 3;
    private static final BigDecimal RECARGO_TALLA_GRANDE = new BigDecimal("5.00");

    private final CategoriaRepository       categoriaRepository;
    private final ProductoRepository        productoRepository;
    private final ProductoDetalleRepository productoDetalleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        //* Si la BD está vacía se crean categorías y productos base; si ya existen, se respetan *//
        if (productoRepository.count() == 0) {
            crearCatalogoBase();
        }

        Set<String> codigosUsados = new HashSet<>(productoDetalleRepository.findAllCodigos());
        int creadas = 0;
        for (int i = 0; i < LINEAS.size(); i++) {
            LineaPrenda linea = LINEAS.get(i);
            var producto = productoRepository.findFirstByNombreIgnoreCase(linea.nombre());
            if (producto.isEmpty()) {
                log.warn("Catálogo: no existe el producto base '{}', se omiten sus variantes", linea.nombre());
                continue;
            }
            creadas += completarVariantes(producto.get(), linea, i + 1, codigosUsados);
        }
        log.info("Catálogo: {} variantes nuevas. Total en producto_detalles: {}",
                creadas, productoDetalleRepository.count());
    }

    private void crearCatalogoBase() {
        Map<String, Categoria> categorias = new HashMap<>();
        for (String nombre : CATEGORIAS) {
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombre)
                    .orElseGet(() -> categoriaRepository.save(Categoria.builder().nombre(nombre).build()));
            categorias.put(nombre, categoria);
        }
        for (LineaPrenda linea : LINEAS) {
            productoRepository.save(Producto.builder()
                    .categoria(categorias.get(linea.categoria()))
                    .nombre(linea.nombre())
                    .genero(linea.genero())
                    .imagenUrl(linea.imagenUrl())
                    .precioBase(new BigDecimal(linea.precioBase()))
                    .disponibilidad(linea.disponible())
                    .build());
        }
        log.info("Catálogo: creadas {} categorías y {} productos base", CATEGORIAS.size(), LINEAS.size());
    }

    private int completarVariantes(Producto producto, LineaPrenda linea, int posicion, Set<String> codigosUsados) {
        int existentes = (producto.getVariantes() == null) ? 0 : producto.getVariantes().size();
        List<VariantePlan> plan = planificar(linea);
        int creadas = 0;

        for (int i = 0; i < plan.size() && existentes + creadas < VARIANTES_POR_PRODUCTO; i++) {
            VariantePlan v = plan.get(i);
            int indice = i + 1;
            String codigo = "%s-%s%02d".formatted(linea.prefijoSku(), codigoMarca(v.marca()), indice);
            if (!codigosUsados.add(codigo)) {
                continue; // El SKU ya existe: esa variante ya fue creada antes
            }
            producto.agregarVariante(ProductoDetalle.builder()
                    .nombreCompleto(String.join(" ", producto.getNombre(), v.marca(), v.segmento(), v.color()))
                    .codigo(codigo)
                    .imagenUrl(producto.getImagenUrl())
                    .marca(v.marca())
                    .talla(v.talla())
                    .color(v.color())
                    .descripcion(linea.material() + ". Línea " + v.segmento() + ".")
                    .stock(calcularStock(posicion, indice))
                    .precioBase(producto.getPrecioBase())
                    .precioAdicional(v.tallaGrande() ? RECARGO_TALLA_GRANDE : new BigDecimal("0.00"))
                    .build());
            creadas++;
        }

        if (creadas > 0) {
            productoRepository.save(producto); // CascadeType.ALL persiste las variantes nuevas
        }
        return creadas;
    }

    //* Reparte las 20 variantes entre adultos, niños y niñas según el género del producto *//
    private List<VariantePlan> planificar(LineaPrenda linea) {
        TipoTalla tallas = linea.tallas();
        int ninos = 0;
        int ninas = 0;
        if (!linea.marcasInfantil().isEmpty() && !tallas.infantil.isEmpty()) {
            switch (linea.genero()) {
                case "Hombre" -> ninos = 6;
                case "Mujer"  -> ninas = 6;
                default       -> { ninos = 4; ninas = 4; }
            }
        }
        int adultos = VARIANTES_POR_PRODUCTO - ninos - ninas;

        String etiquetaAdulto = linea.genero().equals("Unisex") ? "Adulto" : linea.genero();
        List<VariantePlan> plan = new ArrayList<>();
        agregarSegmento(plan, etiquetaAdulto, tallas.adulto, linea.marcasAdulto(), linea.colores(), adultos, 0, true);
        agregarSegmento(plan, "Niño", tallas.infantil, linea.marcasInfantil(), linea.colores(), ninos, 0, false);
        agregarSegmento(plan, "Niña", tallas.infantil, linea.marcasInfantil(), linea.colores(), ninas, 2, false);
        return plan;
    }

    // Cada grupo marca-color recorre toda la curva de tallas del segmento
    private void agregarSegmento(List<VariantePlan> plan, String segmento, List<String> tallas, List<String> marcas,
                                 List<String> colores, int cantidad, int desfaseColor, boolean recargoTallaGrande) {
        for (int k = 0; k < cantidad; k++) {
            int grupo = k / tallas.size();
            String talla = tallas.get(k % tallas.size());
            boolean tallaGrande = recargoTallaGrande && tallas.size() > 1 && talla.equals(tallas.getLast());
            plan.add(new VariantePlan(
                    segmento,
                    marcas.get(grupo % marcas.size()),
                    colores.get((grupo + desfaseColor) % colores.size()),
                    talla,
                    tallaGrande));
        }
    }

    private int calcularStock(int posicion, int indice) {
        if (indice == INDICE_STOCK_CRITICO) {
            return (posicion * 5) % 26;
        }
        return 30 + (posicion * 37 + indice * 53) % 91;
    }

    private String codigoMarca(String marca) {
        return marca.replaceAll("[^A-Za-z]", "").substring(0, 2).toUpperCase();
    }

    private record VariantePlan(String segmento, String marca, String color, String talla, boolean tallaGrande) {}

    private record LineaPrenda(String nombre, String categoria, String genero, String imagenUrl,
                               String precioBase, boolean disponible, String prefijoSku, String material,
                               TipoTalla tallas, List<String> marcasAdulto, List<String> marcasInfantil,
                               List<String> colores) {}

    private enum TipoTalla {
        ROPA(List.of("S", "M", "L", "XL"), List.of("4", "6", "8", "10", "12")),
        PANTALON(List.of("28", "30", "32", "34", "36"), List.of("4", "6", "8", "10", "12")),
        CALZADO(List.of("38", "39", "40", "41", "42", "43"), List.of("28", "30", "32", "34")),
        MEDIAS(List.of("35-38", "39-42", "43-45"), List.of("27-30", "31-34")),
        UNICA(List.of("Única"), List.of("Junior")),
        CADENA(List.of("45 cm", "50 cm", "55 cm", "60 cm"), List.of());

        private final List<String> adulto;
        private final List<String> infantil;

        TipoTalla(List<String> adulto, List<String> infantil) {
            this.adulto = adulto;
            this.infantil = infantil;
        }
    }

    //*-- DATOS DEL CATÁLOGO (mismos productos base que postgres_init_data.sql) --*//

    private static final List<String> CATEGORIAS = List.of(
            "Superiores", "Polos", "Pantalones", "Calzado", "Accesorios",
            "Abrigos", "Gorras y Chullos", "Medias", "Joyería y Pines");

    private static final List<LineaPrenda> LINEAS = List.of(
            new LineaPrenda("Sudadera con Capucha", "Superiores", "Unisex",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRZsD55MPh_Liz6sRG7MWs3iRR_R09tpw2NGQ&s",
                    "89.90", true, "SUD-CAP", "Algodón perchado con capucha y bolsillo canguro", TipoTalla.ROPA,
                    List.of("Nike", "Adidas", "Puma", "Zara"), List.of("Carter's", "Nike", "Adidas"),
                    List.of("Negro", "Gris Jaspe", "Azul Marino", "Blanco", "Verde Oliva")),
            new LineaPrenda("Camisa de lino", "Superiores", "Hombre",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTGrK3YkiHdKqY5WMn5pYK_tEGyzU8VPs6rPinhHcjxhA&s",
                    "51.50", true, "CAM-LIN", "Lino transpirable de manga larga", TipoTalla.ROPA,
                    List.of("Zara", "Tommy Hilfiger", "H&M", "Ralph Lauren"), List.of("Carter's", "Zara"),
                    List.of("Blanco", "Beige", "Celeste", "Negro", "Verde Salvia")),
            new LineaPrenda("Polo Oversize", "Polos", "Unisex",
                    "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=600&auto=format&fit=crop&q=60",
                    "49.90", true, "POL-OVE", "Algodón pesado de corte oversize", TipoTalla.ROPA,
                    List.of("Nike", "Adidas", "Zara", "H&M"), List.of("Carter's", "Nike"),
                    List.of("Negro", "Blanco", "Gris", "Arena", "Lila")),
            new LineaPrenda("Polo Básico Premium", "Polos", "Unisex",
                    "https://www.basisperu.com/cdn/shop/files/polos_basicos_hombre_denim_basis.png?v=1741728393&width=1445",
                    "39.90", false, "POL-BAS", "Algodón pima peruano de cuello redondo", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Tommy Hilfiger", "Levi's"), List.of("Carter's", "H&M"),
                    List.of("Blanco", "Negro", "Azul Marino", "Gris Jaspe", "Rojo")),
            new LineaPrenda("Jean Recto Clásico", "Pantalones", "Mujer",
                    "https://sydney.pe/wp-content/uploads/2025/02/jeans-clasico-celeste.jpg",
                    "99.90", true, "JEA-REC", "Denim clásico de tiro alto y corte recto", TipoTalla.PANTALON,
                    List.of("Levi's", "Zara", "Wrangler", "H&M"), List.of("Carter's", "Levi's"),
                    List.of("Celeste", "Azul Índigo", "Negro", "Gris Denim", "Blanco")),
            new LineaPrenda("Jean Skinny Ajustado", "Pantalones", "Hombre",
                    "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTT6vrQJzqb16V0y3Kb8lMVdWALT9ZZAk1YTg&s",
                    "109.90", true, "JEA-SKI", "Denim stretch de calce ajustado", TipoTalla.PANTALON,
                    List.of("Levi's", "Wrangler", "Zara", "Diesel"), List.of("Levi's", "Carter's"),
                    List.of("Azul Índigo", "Negro", "Celeste", "Gris Denim")),
            new LineaPrenda("Zapatilla Urbana", "Calzado", "Unisex",
                    "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg",
                    "149.90", true, "ZAP-URB", "Capellada de cuero sintético y suela de caucho", TipoTalla.CALZADO,
                    List.of("Nike", "Adidas", "Converse", "Vans"), List.of("Nike", "Adidas", "Carter's"),
                    List.of("Blanco", "Negro", "Beige", "Gris", "Rojo")),
            new LineaPrenda("Zapatilla Running", "Calzado", "Unisex",
                    "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg",
                    "169.90", true, "ZAP-RUN", "Malla transpirable con amortiguación ligera", TipoTalla.CALZADO,
                    List.of("Nike", "Adidas", "Puma", "New Balance"), List.of("Nike", "Adidas"),
                    List.of("Negro", "Blanco", "Azul", "Gris", "Fucsia")),
            new LineaPrenda("Mochila Minimal", "Accesorios", "Unisex",
                    "https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg",
                    "79.90", true, "MOC-MIN", "Lona resistente con compartimento acolchado", TipoTalla.UNICA,
                    List.of("Adidas", "Nike", "Herschel", "Puma"), List.of("Carter's", "Nike"),
                    List.of("Negro", "Gris", "Azul Marino", "Beige", "Verde Oliva")),
            new LineaPrenda("Casaca Impermeable", "Abrigos", "Unisex",
                    "https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=600&auto=format&fit=crop&q=60",
                    "139.90", true, "CAS-IMP", "Cortaviento impermeable con capucha plegable", TipoTalla.ROPA,
                    List.of("Columbia", "The North Face", "Adidas", "Nike"), List.of("Columbia", "Carter's"),
                    List.of("Verde", "Negro", "Azul Marino", "Rojo", "Amarillo")),
            new LineaPrenda("Chompa con diseño", "Superiores", "Mujer",
                    "https://media.falabella.com/tottusPE/43575253_2/w=1500,h=1500,fit=cover",
                    "50.00", true, "CHO-DIS", "Tejido de punto con diseño jacquard", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Mango", "Tommy Hilfiger"), List.of("Carter's", "Zara"),
                    List.of("Crema", "Rosa Palo", "Gris", "Negro", "Vino")),
            new LineaPrenda("Cangurera Streetwear", "Accesorios", "Unisex",
                    "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcSGitrghWoteaqPgukD4X3IU6ICCRahx5GZ53xNkxUN4JXJjgVx",
                    "69.90", true, "CAN-STR", "Nylon repelente al agua con correa ajustable", TipoTalla.UNICA,
                    List.of("Nike", "Adidas", "Puma", "Herschel"), List.of("Nike", "Carter's"),
                    List.of("Negro", "Gris", "Azul", "Verde Militar", "Rojo")),
            new LineaPrenda("Casaca Denim Oversize", "Abrigos", "Unisex",
                    "https://images.pexels.com/photos/1566412/pexels-photo-15166412.jpeg?auto=compress&cs=tinysrgb&w=600",
                    "149.90", true, "CAS-DEN", "Denim rígido de corte oversize con botones metálicos", TipoTalla.ROPA,
                    List.of("Levi's", "Zara", "Wrangler", "H&M"), List.of("Levi's", "Carter's"),
                    List.of("Azul Clásico", "Celeste", "Negro", "Gris Denim")),
            new LineaPrenda("Gorra Trucker LMTL", "Gorras y Chullos", "Unisex",
                    "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=600&auto=format&fit=crop&q=60",
                    "39.90", true, "GOR-TRU", "Gorra trucker con malla posterior y broche ajustable", TipoTalla.UNICA,
                    List.of("New Era", "Nike", "Adidas", "Puma"), List.of("New Era", "Carter's"),
                    List.of("Negro", "Blanco", "Azul Marino", "Rojo", "Beige")),
            new LineaPrenda("Polo Graphic Anime", "Polos", "Unisex",
                    "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=600&auto=format&fit=crop&q=60",
                    "55.00", true, "POL-GRA", "Algodón con estampado gráfico de anime", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Bershka", "Pull&Bear"), List.of("H&M", "Carter's"),
                    List.of("Negro", "Blanco", "Gris", "Azul", "Rojo")),
            new LineaPrenda("Pantalón Cargo Black", "Pantalones", "Hombre",
                    "https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=600",
                    "119.90", true, "PAN-CAR", "Drill resistente con bolsillos cargo laterales", TipoTalla.PANTALON,
                    List.of("Zara", "Levi's", "Dickies", "H&M"), List.of("Carter's", "H&M"),
                    List.of("Negro", "Verde Militar", "Beige", "Gris Carbón")),
            new LineaPrenda("Medias Altas Flame", "Medias", "Unisex",
                    "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=600&auto=format&fit=crop&q=60",
                    "19.90", true, "MED-ALT", "Algodón peinado con diseño de llamas", TipoTalla.MEDIAS,
                    List.of("Nike", "Adidas", "Puma", "Vans"), List.of("Carter's", "Nike"),
                    List.of("Blanco", "Negro", "Rojo", "Gris", "Naranja")),
            new LineaPrenda("Cadena de Acero Cubana", "Joyería y Pines", "Unisex",
                    "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=600&auto=format&fit=crop&q=60",
                    "45.00", true, "CAD-CUB", "Acero inoxidable 316L con eslabón cubano", TipoTalla.CADENA,
                    List.of("Tommy Hilfiger", "Fossil", "Guess", "Calvin Klein"), List.of(),
                    List.of("Plateado", "Dorado", "Negro", "Oro Rosa", "Grafito")));
}
