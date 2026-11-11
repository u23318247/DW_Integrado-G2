package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.model.Reclamacion;
import com.proyectomdweb.proyectomdweb.repository.ReclamacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión del Libro de Reclamaciones Virtual
 * Conforme a la Ley N.° 29571 e INDECOPI
 * Alumna: Antonela Jaimes
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReclamacionService {

    private final ReclamacionRepository reclamacionRepository;

    @Transactional
    public Reclamacion registrarReclamacion(Reclamacion rec) {
        int anio = LocalDateTime.now().getYear();
        String prefijo = (rec.getTipo() != null && rec.getTipo().equalsIgnoreCase("QUEJA")) ? "QUE" : "REC";
        String patron = prefijo + "-" + anio;

        long conteo = reclamacionRepository.countByCodigoReclamoStartingWith(patron) + 1;
        String codigoGenerado = String.format("%s-%05d", patron, conteo);

        rec.setCodigoReclamo(codigoGenerado);
        rec.setEstado("PENDIENTE");
        if (rec.getFechaRegistro() == null) {
            rec.setFechaRegistro(LocalDateTime.now());
        }

        Reclamacion guardada = reclamacionRepository.save(rec);
        log.info("[LIBRO-RECLAMACIONES] Nuevo registro legal creado: {} para {} ({})",
                codigoGenerado, rec.getNombresApellidos(), rec.getEmail());

        enviarConstanciaEmailAsync(guardada);
        return guardada;
    }

    public Optional<Reclamacion> consultarPorCodigo(String codigo) {
        return reclamacionRepository.findByCodigoReclamo(codigo);
    }

    public List<Reclamacion> listarTodas() {
        return reclamacionRepository.findAll();
    }

    @Async
    public void enviarConstanciaEmailAsync(Reclamacion rec) {
        log.info("======================================================================");
        log.info("[INDECOPI / CONSTANCIA DE RECLAMACIÓN ENVIADA]");
        log.info(">>> Código Correlativo: {}", rec.getCodigoReclamo());
        log.info(">>> Consumidor: {} | Documento: {}", rec.getNombresApellidos(), rec.getNumeroDocumento());
        log.info(">>> Destino: {}", rec.getEmail());
        log.info(">>> Tipo: {} sobre {}", rec.getTipo(), rec.getTipoBien());
        log.info(">>> Plazo legal de respuesta: 15 días hábiles conforme al D.S. N.° 011-2011-PCM.");
        log.info("======================================================================");
    }
}
