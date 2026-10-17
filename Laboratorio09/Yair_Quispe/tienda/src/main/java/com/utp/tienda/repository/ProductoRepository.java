package com.utp.tienda.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.utp.tienda.model.Producto;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}