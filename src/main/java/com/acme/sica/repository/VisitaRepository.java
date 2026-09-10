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
    /** Pendientes de invitados que no pertenecen a ninguna empresa (persona.empresa_id IS NULL). */
    List<Visita> listarPendientesSinEmpresa();
    /** Personas (trabajadores o invitados) de una empresa cuya visita está actualmente "Dentro". */
    List<Visita> listarDentroPorEmpresa(int empresaId);
    List<Visita> listarTodas();
    List<Visita> listarPorPersona(int personaId);
}
