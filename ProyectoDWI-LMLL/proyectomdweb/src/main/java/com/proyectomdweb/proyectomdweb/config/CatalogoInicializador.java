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
 * al iniciar la aplicaciÃ³n garantiza el catÃ¡logo de 18 productos generales con
 * exactamente 20 variantes cada uno (360 prendas en producto_detalles).
 * Las variantes se guardan en CASCADA a travÃ©s de Producto.variantes (CascadeType.ALL).
 * Es idempotente: solo completa lo que falta, nunca duplica.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "catalogo.inicializar", havingValue = "true", matchIfMissing = true)
public class CatalogoInicializador implements CommandLineRunner {

    public static final int VARIANTES_POR_PRODUCTO = 20;

    // Una variante por producto queda con stock crÃ­tico (<= 25) para la alerta del dashboard
    private static final int INDICE_STOCK_CRITICO = 3;
    private static final BigDecimal RECARGO_TALLA_GRANDE = new BigDecimal("5.00");

    private final CategoriaRepository       categoriaRepository;
    private final ProductoRepository        productoRepository;
    private final ProductoDetalleRepository productoDetalleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        //* Si la BD estÃ¡ vacÃ­a se crean categorÃ­as y productos base; si ya existen, se respetan *//
        if (productoRepository.count() == 0) {
            crearCatalogoBase();
        }

        Set<String> codigosUsados = new HashSet<>(productoDetalleRepository.findAllCodigos());
        int creadas = 0;
        for (int i = 0; i < LINEAS.size(); i++) {
            LineaPrenda linea = LINEAS.get(i);
            var producto = productoRepository.findFirstByNombreIgnoreCase(linea.nombre());
            if (producto.isEmpty()) {
                log.warn("CatÃ¡logo: no existe el producto base '{}', se omiten sus variantes", linea.nombre());
                continue;
            }
            creadas += completarVariantes(producto.get(), linea, i + 1, codigosUsados);
        }
        log.info("CatÃ¡logo: {} variantes nuevas. Total en producto_detalles: {}",
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
        log.info("CatÃ¡logo: creadas {} categorÃ­as y {} productos base", CATEGORIAS.size(), LINEAS.size());
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
                    .descripcion(linea.material() + ". LÃ­nea " + v.segmento() + ".")
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

    //* Reparte las 20 variantes entre adultos, niÃ±os y niÃ±as segÃºn el gÃ©nero del producto *//
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
        agregarSegmento(plan, "NiÃ±o", tallas.infantil, linea.marcasInfantil(), linea.colores(), ninos, 0, false);
        agregarSegmento(plan, "NiÃ±a", tallas.infantil, linea.marcasInfantil(), linea.colores(), ninas, 2, false);
        return plan;
    }

    // Cada grupo marca-color recorre toda la curva de tallas del segmento
    private void agregarSegmento(List<VariantePlan> plan, String segmento, List<String> tallas, List<String> marcas,
                                 List<String> colores, int cantidad, int desfaseColor, boolean recargoTallaGrande) {
        for (int k = 0; k < cantidad; k++) {
            int grupo = k / tallas.size();
            String talla = tallas.get(k % tallas.size());
            boolean tallaGrande = recargoTallaGrande && tallas.size() > 1 && talla.equals(tallas.get(tallas.size() - 1));
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
        UNICA(List.of("Ãšnica"), List.of("Junior")),
        CADENA(List.of("45 cm", "50 cm", "55 cm", "60 cm"), List.of());

        private final List<String> adulto;
        private final List<String> infantil;

        TipoTalla(List<String> adulto, List<String> infantil) {
            this.adulto = adulto;
            this.infantil = infantil;
        }
    }

    //*-- DATOS DEL CATÃLOGO (mismos productos base que postgres_init_data.sql) --*//

    private static final List<String> CATEGORIAS = List.of(
            "Superiores", "Polos", "Pantalones", "Calzado", "Accesorios",
            "Abrigos", "Gorras y Chullos", "Medias", "JoyerÃ­a y Pines");

    private static final List<LineaPrenda> LINEAS = List.of(
            new LineaPrenda("Sudadera con Capucha", "Superiores", "Unisex",
                    "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=900&auto=format&fit=crop&q=80",
                    "89.90", true, "SUD-CAP", "AlgodÃ³n perchado con capucha y bolsillo canguro", TipoTalla.ROPA,
                    List.of("Nike", "Adidas", "Puma", "Zara"), List.of("Carter's", "Nike", "Adidas"),
                    List.of("Negro", "Gris Jaspe", "Azul Marino", "Blanco", "Verde Oliva")),
            new LineaPrenda("Camisa de lino", "Superiores", "Hombre",
                    "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?w=900&auto=format&fit=crop&q=80",
                    "51.50", true, "CAM-LIN", "Lino transpirable de manga larga", TipoTalla.ROPA,
                    List.of("Zara", "Tommy Hilfiger", "H&M", "Ralph Lauren"), List.of("Carter's", "Zara"),
                    List.of("Blanco", "Beige", "Celeste", "Negro", "Verde Salvia")),
            new LineaPrenda("Polo Oversize", "Polos", "Unisex",
                    "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=900&auto=format&fit=crop&q=80",
                    "49.90", true, "POL-OVE", "AlgodÃ³n pesado de corte oversize", TipoTalla.ROPA,
                    List.of("Nike", "Adidas", "Zara", "H&M"), List.of("Carter's", "Nike"),
                    List.of("Negro", "Blanco", "Gris", "Arena", "Lila")),
            new LineaPrenda("Polo BÃ¡sico Premium", "Polos", "Unisex",
                    "https://images.unsplash.com/photo-1521572267360-ee0c2909d518?w=900&auto=format&fit=crop&q=80",
                    "39.90", false, "POL-BAS", "AlgodÃ³n pima peruano de cuello redondo", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Tommy Hilfiger", "Levi's"), List.of("Carter's", "H&M"),
                    List.of("Blanco", "Negro", "Azul Marino", "Gris Jaspe", "Rojo")),
            new LineaPrenda("Jean Recto ClÃ¡sico", "Pantalones", "Mujer",
                    "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=900&auto=format&fit=crop&q=80",
                    "99.90", true, "JEA-REC", "Denim clÃ¡sico de tiro alto y corte recto", TipoTalla.PANTALON,
                    List.of("Levi's", "Zara", "Wrangler", "H&M"), List.of("Carter's", "Levi's"),
                    List.of("Celeste", "Azul Ãndigo", "Negro", "Gris Denim", "Blanco")),
            new LineaPrenda("Jean Skinny Ajustado", "Pantalones", "Hombre",
                    "https://images.unsplash.com/photo-1542272454315-4c01d7abdf4a?w=900&auto=format&fit=crop&q=80",
                    "109.90", true, "JEA-SKI", "Denim stretch de calce ajustado", TipoTalla.PANTALON,
                    List.of("Levi's", "Wrangler", "Zara", "Diesel"), List.of("Levi's", "Carter's"),
                    List.of("Azul Ãndigo", "Negro", "Celeste", "Gris Denim")),
            new LineaPrenda("Zapatilla Urbana", "Calzado", "Unisex",
                    "https://images.pexels.com/photos/2529148/pexels-photo-2529148.jpeg?auto=compress&cs=tinysrgb&w=900",
                    "149.90", true, "ZAP-URB", "Capellada de cuero sintÃ©tico y suela de caucho", TipoTalla.CALZADO,
                    List.of("Nike", "Adidas", "Converse", "Vans"), List.of("Nike", "Adidas", "Carter's"),
                    List.of("Blanco", "Negro", "Beige", "Gris", "Rojo")),
            new LineaPrenda("Zapatilla Running", "Calzado", "Unisex",
                    "https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg?auto=compress&cs=tinysrgb&w=900",
                    "169.90", true, "ZAP-RUN", "Malla transpirable con amortiguaciÃ³n ligera", TipoTalla.CALZADO,
                    List.of("Nike", "Adidas", "Puma", "New Balance"), List.of("Nike", "Adidas"),
                    List.of("Negro", "Blanco", "Azul", "Gris", "Fucsia")),
            new LineaPrenda("Mochila Minimal", "Accesorios", "Unisex",
                    "https://images.pexels.com/photos/2905238/pexels-photo-2905238.jpeg?auto=compress&cs=tinysrgb&w=900",
                    "79.90", true, "MOC-MIN", "Lona resistente con compartimento acolchado", TipoTalla.UNICA,
                    List.of("Adidas", "Nike", "Herschel", "Puma"), List.of("Carter's", "Nike"),
                    List.of("Negro", "Gris", "Azul Marino", "Beige", "Verde Oliva")),
            new LineaPrenda("Casaca Impermeable", "Abrigos", "Unisex",
                    "https://images.unsplash.com/photo-1706765779494-2705542ebe74?w=900&auto=format&fit=crop&q=80",
                    "139.90", true, "CAS-IMP", "Cortaviento impermeable con capucha plegable", TipoTalla.ROPA,
                    List.of("Columbia", "The North Face", "Adidas", "Nike"), List.of("Columbia", "Carter's"),
                    List.of("Verde", "Negro", "Azul Marino", "Rojo", "Amarillo")),
            new LineaPrenda("Chompa con diseÃ±o", "Superiores", "Mujer",
                    "https://images.unsplash.com/photo-1576871337622-98d48d1cf531?w=900&auto=format&fit=crop&q=80",
                    "50.00", true, "CHO-DIS", "Tejido de punto con diseÃ±o jacquard", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Mango", "Tommy Hilfiger"), List.of("Carter's", "Zara"),
                    List.of("Crema", "Rosa Palo", "Gris", "Negro", "Vino")),
            new LineaPrenda("Cangurera Streetwear", "Accesorios", "Unisex",
                    "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=900&auto=format&fit=crop&q=80",
                    "69.90", true, "CAN-STR", "Nylon repelente al agua con correa ajustable", TipoTalla.UNICA,
                    List.of("Nike", "Adidas", "Puma", "Herschel"), List.of("Nike", "Carter's"),
                    List.of("Negro", "Gris", "Azul", "Verde Militar", "Rojo")),
            new LineaPrenda("Casaca Denim Oversize", "Abrigos", "Unisex",
                    "https://images.unsplash.com/photo-1576995853123-5a10305d93c0?w=900&auto=format&fit=crop&q=80",
                    "149.90", true, "CAS-DEN", "Denim rÃ­gido de corte oversize con botones metÃ¡licos", TipoTalla.ROPA,
                    List.of("Levi's", "Zara", "Wrangler", "H&M"), List.of("Levi's", "Carter's"),
                    List.of("Azul ClÃ¡sico", "Celeste", "Negro", "Gris Denim")),
            new LineaPrenda("Gorra Trucker LMTL", "Gorras y Chullos", "Unisex",
                    "https://images.unsplash.com/photo-1588850561407-ed78c282e89b?w=900&auto=format&fit=crop&q=80",
                    "39.90", true, "GOR-TRU", "Gorra trucker con malla posterior y broche ajustable", TipoTalla.UNICA,
                    List.of("New Era", "Nike", "Adidas", "Puma"), List.of("New Era", "Carter's"),
                    List.of("Negro", "Blanco", "Azul Marino", "Rojo", "Beige")),
            new LineaPrenda("Polo Graphic Anime", "Polos", "Unisex",
                    "https://images.unsplash.com/photo-1576566588028-4147f3842f27?w=900&auto=format&fit=crop&q=80",
                    "55.00", true, "POL-GRA", "AlgodÃ³n con estampado grÃ¡fico de anime", TipoTalla.ROPA,
                    List.of("Zara", "H&M", "Bershka", "Pull&Bear"), List.of("H&M", "Carter's"),
                    List.of("Negro", "Blanco", "Gris", "Azul", "Rojo")),
            new LineaPrenda("PantalÃ³n Cargo Black", "Pantalones", "Hombre",
                    "https://images.pexels.com/photos/16894086/pexels-photo-16894086.jpeg?auto=compress&cs=tinysrgb&w=900",
                    "119.90", true, "PAN-CAR", "Drill resistente con bolsillos cargo laterales", TipoTalla.PANTALON,
                    List.of("Zara", "Levi's", "Dickies", "H&M"), List.of("Carter's", "H&M"),
                    List.of("Negro", "Verde Militar", "Beige", "Gris CarbÃ³n")),
            new LineaPrenda("Medias Altas Flame", "Medias", "Unisex",
                    "https://images.unsplash.com/photo-1582966772680-860e372bb558?w=900&auto=format&fit=crop&q=80",
                    "19.90", true, "MED-ALT", "AlgodÃ³n peinado con diseÃ±o de llamas", TipoTalla.MEDIAS,
                    List.of("Nike", "Adidas", "Puma", "Vans"), List.of("Carter's", "Nike"),
                    List.of("Blanco", "Negro", "Rojo", "Gris", "Naranja")),
            new LineaPrenda("Cadena de Acero Cubana", "JoyerÃ­a y Pines", "Unisex",
                    "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=900&auto=format&fit=crop&q=80",
                    "45.00", true, "CAD-CUB", "Acero inoxidable 316L con eslabÃ³n cubano", TipoTalla.CADENA,
                    List.of("Tommy Hilfiger", "Fossil", "Guess", "Calvin Klein"), List.of(),
                    List.of("Plateado", "Dorado", "Negro", "Oro Rosa", "Grafito")));
}
