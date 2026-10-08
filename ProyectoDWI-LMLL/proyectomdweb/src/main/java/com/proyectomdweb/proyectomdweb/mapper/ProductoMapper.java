package com.proyectomdweb.proyectomdweb.mapper;

import com.proyectomdweb.proyectomdweb.dtos.ProductoDTO;
import com.proyectomdweb.proyectomdweb.model.Producto;
import com.proyectomdweb.proyectomdweb.model.Categoria;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    //* Convertir de Entidad a DTO (Para enviar a la Vista) *//
    public ProductoDTO toDto(Producto producto) {
        return new ProductoDTO(
            producto.getId(),
            producto.getNombre(),
            producto.getGenero(),
            producto.getImagenUrl(),
            producto.getPrecioBase(),
            producto.getDisponibilidad(),
            producto.getCategoria() != null ? producto.getCategoria().getId() : null,
            producto.getCategoria() != null ? producto.getCategoria().getNombre() : "Sin categoría"
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