package com.proyectomdweb.proyectomdweb.mapper;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.dtos.ProductoDetalleDTO;
import com.proyectomdweb.proyectomdweb.model.Categoria;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.model.ProductoDetalle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class ProductoMapper {

    //* Convertir de Entidad a DTO (Para enviar a la Vista y a la API) *//
    public ProductoDTO toDto(Producto producto) {
        List<ProductoDetalleDTO> variantesDto = Collections.emptyList();
        List<String> tallas = Collections.emptyList();
        List<String> colores = Collections.emptyList();

        if (producto.getVariantes() != null && !producto.getVariantes().isEmpty()) {
            variantesDto = producto.getVariantes().stream()
                    .map(ProductoDetalleDTO::deEntidad)
                    .toList();

            tallas = producto.getVariantes().stream()
                    .map(ProductoDetalle::getTalla)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();

            colores = producto.getVariantes().stream()
                    .map(ProductoDetalle::getColor)
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
        }

        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getGenero(),
            producto.getImagenUrl(),
            producto.getPrecioBase(),
            producto.getDisponibilidad(),
            producto.getCategoria() != null ? producto.getCategoria().getId() : null,
            producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoría",
            tallas,
            colores,
            variantesDto
        );
    }

    //* Convertir de DTO a Entidad (Para guardar en BD) *//
    public Producto toEntity(ProductoDTO dto, Categoria categoria) {
        Producto producto = new Producto();
        producto.setId(dto.id());
        producto.setNombre(dto.nombre());
        producto.setGenero(dto.genero());
        producto.setImagenUrl(dto.imagenUrl());
        producto.setPrecioBase(dto.precioBase());
        producto.setDisponibilidad(dto.disponibilidad());
        producto.setCategoria(categoria);
        return producto;
    }
}