package com.proyectomdweb.proyectomdweb.service;

import com.proyectomdweb.proyectomdweb.dtos.GraficaDataDTO;
import java.util.List;

public interface PresentacionDatosService {    
   
    List<GraficaDataDTO> obtenerIngresosMensuales(int anio, int mes);
    
    List<GraficaDataDTO> obtenerIngresosAnuales(int anio);
}
