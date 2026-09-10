package com.g2.dwi_lmll.service;

import com.g2.dwi_lmll.dto.ProductoDTO;
import com.g2.dwi_lmll.mapper.ProductoMapper;
import com.g2.dwi_lmll.model.Categoria;
import com.g2.dwi_lmll.model.Producto;
import com.g2.dwi_lmll.repository.CategoriaRepository;
import com.g2.dwi_lmll.repository.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private ProductoMapper productoMapper;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoServiceImpl productoService;

    private Categoria categoria;
    private Producto producto;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("Polos");

        producto = Producto.builder()
                .id(1L)
                .nombre("Polo Oversize")
                .genero("Unisex")
                .imagenUrl("/img/polo.jpg")
                .precioBase(new BigDecimal("49.90"))
                .disponibilidad(true)
                .categoria(categoria)
                .build();

        productoDTO = new ProductoDTO(
                1L,
                "Polo Oversize",
                "Unisex",
                "/img/polo.jpg",
                new BigDecimal("49.90"),
                true,
                1L,
                "Polos"
        );
    }

    @Test
    @DisplayName("Debe listar todos los productos convertidos a DTO")
    void listarTodos_debeRetornarListaDeProductosDTO() {
        when(productoRepository.findAllConCategoria()).thenReturn(List.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = productoService.listarTodos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).nombre()).isEqualTo("Polo Oversize");
        verify(productoRepository).findAllConCategoria();
        verify(productoMapper).toDto(producto);
    }

    @Test
    @DisplayName("Debe buscar producto por ID existente y retornar DTO")
    void buscarPorId_cuandoExiste_debeRetornarProductoDTO() {
        when(productoRepository.findByIdConCategoria(1L)).thenReturn(Optional.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        Optional<ProductoDTO> resultado = productoService.buscarPorId(1L);

        assertThat(resultado).isPresent();
        assertThat(resultado.get().id()).isEqualTo(1L);
        assertThat(resultado.get().nombre()).isEqualTo("Polo Oversize");
        verify(productoRepository).findByIdConCategoria(1L);
    }

    @Test
    @DisplayName("Debe retornar Optional vacío cuando el ID no existe")
    void buscarPorId_cuandoNoExiste_debeRetornarVacio() {
        when(productoRepository.findByIdConCategoria(99L)).thenReturn(Optional.empty());

        Optional<ProductoDTO> resultado = productoService.buscarPorId(99L);

        assertThat(resultado).isEmpty();
        verify(productoRepository).findByIdConCategoria(99L);
    }

    @Test
    @DisplayName("Debe buscar productos por ID de categoría")
    void buscarPorCategoriaId_debeRetornarLista() {
        when(productoRepository.findByCategoriaId(1L)).thenReturn(List.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = productoService.buscarPorCategoriaId(1L);

        assertThat(resultado).hasSize(1);
        verify(productoRepository).findByCategoriaId(1L);
    }

    @Test
    @DisplayName("Debe buscar productos por nombre")
    void buscarPorNombre_debeRetornarLista() {
        when(productoRepository.findByNombreContainingIgnoreCase("polo")).thenReturn(List.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = productoService.buscarPorNombre("polo");

        assertThat(resultado).hasSize(1);
        verify(productoRepository).findByNombreContainingIgnoreCase("polo");
    }

    @Test
    @DisplayName("Debe filtrar productos por género y categoría")
    void filtrar_debeRetornarLista() {
        when(productoRepository.filtrarPorGeneroYCategoria("Unisex", 1L)).thenReturn(List.of(producto));
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        List<ProductoDTO> resultado = productoService.filtrar("Unisex", 1L);

        assertThat(resultado).hasSize(1);
        verify(productoRepository).filtrarPorGeneroYCategoria("Unisex", 1L);
    }

    @Test
    @DisplayName("Debe guardar un nuevo producto correctamente")
    void guardar_nuevoProducto_debeGuardarYRetornarDTO() {
        ProductoDTO nuevoDTO = new ProductoDTO(
                null,
                "Polera Hoodie",
                "Hombre",
                "/img/hoodie.jpg",
                new BigDecimal("89.90"),
                true,
                1L,
                null
        );

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));
        when(productoMapper.toEntity(nuevoDTO, categoria)).thenReturn(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(productoMapper.toDto(producto)).thenReturn(productoDTO);

        ProductoDTO guardado = productoService.guardar(nuevoDTO);

        assertThat(guardado).isNotNull();
        assertThat(guardado.nombre()).isEqualTo("Polo Oversize");
        verify(categoriaRepository).findById(1L);
        verify(productoRepository).save(producto);
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el nombre está vacío")
    void guardar_sinNombre_debeLanzarExcepcion() {
        ProductoDTO sinNombreDTO = new ProductoDTO(
                null,
                "",
                "Hombre",
                "/img/hoodie.jpg",
                new BigDecimal("89.90"),
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> productoService.guardar(sinNombreDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El nombre del producto es obligatorio.");
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si el precio es cero o menor")
    void guardar_precioInvalido_debeLanzarExcepcion() {
        ProductoDTO precioCeroDTO = new ProductoDTO(
                null,
                "Polera Hoodie",
                "Hombre",
                "/img/hoodie.jpg",
                BigDecimal.ZERO,
                true,
                1L,
                null
        );

        assertThatThrownBy(() -> productoService.guardar(precioCeroDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("El precio debe ser mayor a cero.");
    }

    @Test
    @DisplayName("Debe lanzar excepción al guardar si no se especifica categoría")
    void guardar_sinCategoria_debeLanzarExcepcion() {
        ProductoDTO sinCatDTO = new ProductoDTO(
                null,
                "Polera Hoodie",
                "Hombre",
                "/img/hoodie.jpg",
                new BigDecimal("89.90"),
                true,
                null,
                null
        );

        assertThatThrownBy(() -> productoService.guardar(sinCatDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Debe seleccionar una categoría.");
    }

    @Test
    @DisplayName("Debe eliminar producto cuando el ID existe")
    void eliminar_cuandoExiste_debeEliminar() {
        when(productoRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productoRepository).deleteById(1L);

        productoService.eliminar(1L);

        verify(productoRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al eliminar cuando el ID no existe")
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        when(productoRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> productoService.eliminar(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No se puede eliminar: el producto no existe.");
    }

    @Test
    @DisplayName("Debe obtener la entidad directa por ID cuando existe")
    void obtenerEntidadPorId_cuandoExiste_debeRetornarEntidad() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.obtenerEntidadPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Debe lanzar excepción al obtener entidad por ID inexistente")
    void obtenerEntidadPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.obtenerEntidadPorId(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Producto no encontrado con id: 99");
    }
}
