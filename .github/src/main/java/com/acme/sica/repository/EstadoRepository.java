package com.acme.sica.repository;

import com.acme.sica.model.EstadoAcceso;
import com.acme.sica.model.EstadoVisita;
import java.util.List;
import java.util.Optional;

/** Repositorio de los catálogos (lookup tables) de estados. */
public interface EstadoRepository {
    Optional<EstadoVisita> buscarEstadoVisitaPorNombre(String nombre);
    Optional<EstadoAcceso> buscarEstadoAccesoPorNombre(String nombre);
    List<EstadoVisita> listarEstadosVisita();
    List<EstadoAcceso> listarEstadosAcceso();
}
