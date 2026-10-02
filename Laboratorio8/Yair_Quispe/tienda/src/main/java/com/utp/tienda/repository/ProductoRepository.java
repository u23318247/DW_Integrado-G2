package com.utp.tienda.repository;

import com.utp.tienda.model.Producto;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}