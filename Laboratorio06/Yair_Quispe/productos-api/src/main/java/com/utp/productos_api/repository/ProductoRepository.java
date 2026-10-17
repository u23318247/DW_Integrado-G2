package com.utp.productos_api.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.utp.productos_api.model.Producto;
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}