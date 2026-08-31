package com.acme.sica.repository;

import com.acme.sica.model.Visita;
import java.util.List;
import java.util.Optional;

public interface VisitaRepository {
    Visita guardar(Visita visita);
    void actualizar(Visita visita);
    Optional<Visita> buscarPorId(int id);
    /** Busca la última visita de una persona cuyo estado sea "Dentro" (para detectar salida olvidada). */
    Optional<Visita> buscarVisitaAbiertaDePersona(int personaId);
    List<Visita> listarPendientesPorFuncionario(int empresaId);
    List<Visita> listarTodas();
    List<Visita> listarPorPersona(int personaId);
}
