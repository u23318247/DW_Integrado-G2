package com.g2.dwi_lmll.service.implement;

import com.g2.dwi_lmll.model.EstadoPedido;
import com.g2.dwi_lmll.model.Pedido;
import com.g2.dwi_lmll.repository.PedidoRepository;
import com.g2.dwi_lmll.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarPorEstado(EstadoPedido estado) {
        return pedidoRepository.countByEstado(estado);
    }

    @Override
    @Transactional
    public Pedido guardar(Pedido pedido) {
        if (pedido.getEstado() == null) {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }
        return pedidoRepository.save(pedido);
    }

    @Override
    @Transactional
    public Pedido actualizar(Long id, Pedido pedido) {
        Pedido existente = pedidoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("El pedido con ID " + id + " no existe."));

        existente.setUsuarioId(pedido.getUsuarioId());
        existente.setTotal(pedido.getTotal());
        if (pedido.getEstado() != null) {
            existente.setEstado(pedido.getEstado());
        }

        return pedidoRepository.save(existente);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!pedidoRepository.existsById(id)) {
            throw new RuntimeException("El pedido con ID " + id + " no existe.");
        }
        pedidoRepository.deleteById(id);
    }
}
