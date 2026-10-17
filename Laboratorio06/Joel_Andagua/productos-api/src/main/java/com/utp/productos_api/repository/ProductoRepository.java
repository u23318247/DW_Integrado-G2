package com.utp.productos_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.utp.productos_api.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}