package com.proyectomdweb.proyectomdweb.repository;

import com.proyectomdweb.proyectomdweb.config.CatalogoInicializador;
import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.model.ProductoDetalle;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/*
 * Laboratorio 6 integrado: pruebas de la capa de persistencia JPA/Hibernate.
 * @DataJpaTest levanta solo el contexto JPA sobre una base embebida H2,
 * por lo que corre en cualquier equipo sin necesidad de PostgreSQL.
 */
@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class CatalogoPersistenciaTest {

    @Autowired private CategoriaRepository       categoriaRepository;
    @Autowired private ProductoRepository        productoRepository;
    @Autowired private ProductoDetalleRepository detalleRepository;
    @Autowired private TestEntityManager         em;

    // Se crea a mano (no como bean) para que no se ejecute solo al levantar el contexto
    private CatalogoInicializador inicializador;

    @BeforeEach
    void setUp() {
        inicializador = new CatalogoInicializador(categoriaRepository, productoRepository, detalleRepository);
    }

    //*-- CATÁLOGO DE 360 PRENDAS --*//

    @Test
    @DisplayName("En una BD vacía crea 9 categorías, 18 productos y 20 variantes por producto (360)")
    void creaCatalogoDe360Prendas() {
        inicializador.run();

        assertEquals(9, categoriaRepository.count());
        assertEquals(18, productoRepository.count());
        assertEquals(360, detalleRepository.count());
        productoRepository.findAll().forEach(p ->
                assertEquals(20, detalleRepository.countByProductoId(p.getId()), p.getNombre()));
    }

    @Test
    @DisplayName("Ejecutarlo varias veces no duplica variantes")
    void esIdempotente() {
        inicializador.run();
        inicializador.run();

        assertEquals(18, productoRepository.count());
        assertEquals(360, detalleRepository.count());
    }

    @Test
    @DisplayName("Si los productos ya existen, completa sus variantes hasta 20 y conserva las previas")
    void completaProductosExistentes() {
        Producto sudadera = producto("Sudadera con Capucha");
        sudadera.agregarVariante(variante("SUD-CAP-01"));
        productoRepository.save(sudadera);

        inicializador.run();

        assertEquals(1, productoRepository.count(), "No debe recrear el catálogo base si ya hay productos");
        assertEquals(20, detalleRepository.countByProductoId(sudadera.getId()));
        assertTrue(detalleRepository.findAllCodigos().contains("SUD-CAP-01"));
    }

    @Test
    @DisplayName("Las variantes tienen SKU único, marcas reconocidas y tallas para adultos, niños y niñas")
    void variantesConSkuMarcasYSegmentos() {
        inicializador.run();
        List<ProductoDetalle> variantes = detalleRepository.findAll();

        Set<String> codigos = variantes.stream().map(ProductoDetalle::getCodigo).collect(Collectors.toSet());
        assertEquals(360, codigos.size());

        Set<String> marcas = variantes.stream().map(ProductoDetalle::getMarca).collect(Collectors.toSet());
        assertTrue(marcas.containsAll(List.of("Nike", "Adidas", "Zara", "Levi's", "Carter's")));

        assertTrue(variantes.stream().anyMatch(v -> v.getNombreCompleto().contains(" Adulto ")));
        assertTrue(variantes.stream().anyMatch(v -> v.getNombreCompleto().contains(" Niño ")));
        assertTrue(variantes.stream().anyMatch(v -> v.getNombreCompleto().contains(" Niña ")));

        // Ninguna combinación prenda + talla se repite
        Set<String> combinaciones = new HashSet<>();
        variantes.forEach(v -> assertTrue(combinaciones.add(v.getNombreCompleto() + "|" + v.getTalla()),
                "Variante repetida: " + v.getNombreCompleto() + " talla " + v.getTalla()));

        // Alimenta la alerta de stock crítico del dashboard (<= 25 unidades)
        assertEquals(18, detalleRepository.findByStockLessThanEqualOrderByStockAsc(25).size());
    }

    //*-- MAPEO DE RELACIONES --*//

    @Test
    @DisplayName("CascadeType.ALL: guardar el producto persiste sus variantes")
    void guardaVariantesEnCascada() {
        Producto polo = producto("Polo Test");
        polo.agregarVariante(variante("TST-001"));
        polo.agregarVariante(variante("TST-002"));

        productoRepository.save(polo);
        em.flush();
        em.clear();

        assertEquals(2, detalleRepository.countByProductoId(polo.getId()));
    }

    @Test
    @DisplayName("orphanRemoval: quitar una variante de la lista la elimina de la BD")
    void eliminaVarianteHuerfana() {
        Producto polo = producto("Polo Test");
        polo.agregarVariante(variante("TST-001"));
        polo.agregarVariante(variante("TST-002"));
        productoRepository.save(polo);
        em.flush();
        em.clear();

        Producto recuperado = productoRepository.findById(polo.getId()).orElseThrow();
        recuperado.quitarVariante(recuperado.getVariantes().get(0));
        em.flush();

        assertEquals(1, detalleRepository.countByProductoId(polo.getId()));
    }

    @Test
    @DisplayName("FetchType.LAZY: la categoría no se carga hasta que se usa")
    void categoriaSeCargaPerezosamente() {
        Producto polo = productoRepository.save(producto("Polo Test"));
        em.flush();
        em.clear();

        Producto recuperado = productoRepository.findById(polo.getId()).orElseThrow();

        assertFalse(Hibernate.isInitialized(recuperado.getCategoria()));
        assertEquals("Polos", recuperado.getCategoria().getNombre());
        assertTrue(Hibernate.isInitialized(recuperado.getCategoria()));
    }

    @Test
    @DisplayName("El código SKU es único en producto_detalles")
    void rechazaSkuDuplicado() {
        Producto polo = producto("Polo Test");
        polo.agregarVariante(variante("TST-001"));
        polo.agregarVariante(variante("TST-001"));

        assertThrows(DataIntegrityViolationException.class, () -> productoRepository.saveAndFlush(polo));
    }

    private Producto producto(String nombre) {
        Categoria categoria = categoriaRepository.save(Categoria.builder().nombre("Polos").build());
        return Producto.builder()
                .categoria(categoria)
                .nombre(nombre)
                .genero("Unisex")
                .imagenUrl("https://img.test/polo.jpg")
                .precioBase(new BigDecimal("49.90"))
                .disponibilidad(true)
                .build();
    }

    private ProductoDetalle variante(String codigo) {
        return ProductoDetalle.builder()
                .nombreCompleto("Variante " + codigo)
                .codigo(codigo)
                .imagenUrl("https://img.test/polo.jpg")
                .marca("Nike")
                .talla("M")
                .color("Negro")
                .descripcion("Variante de prueba")
                .stock(10)
                .precioBase(new BigDecimal("49.90"))
                .precioAdicional(new BigDecimal("0.00"))
                .build();
    }
}
