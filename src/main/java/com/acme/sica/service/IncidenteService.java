package com.acme.sica.service;

import com.acme.sica.model.Incidente;
import com.acme.sica.model.RegistroAuditoria;
import com.acme.sica.model.Usuario;
import com.acme.sica.model.Visita;
import com.acme.sica.repository.AuditoriaRepository;
import com.acme.sica.repository.IncidenteRepository;
import com.acme.sica.repository.VisitaRepository;

import java.util.List;
import java.util.Optional;

public class IncidenteService {

    private final IncidenteRepository incidenteRepository;
    private final VisitaRepository visitaRepository;
    private final AutorizacionService autorizacionService;
    private final AuditoriaRepository auditoriaRepository;

    public IncidenteService(IncidenteRepository incidenteRepository, VisitaRepository visitaRepository,
                             AutorizacionService autorizacionService, AuditoriaRepository auditoriaRepository) {
        this.incidenteRepository = incidenteRepository;
        this.visitaRepository = visitaRepository;
        this.autorizacionService = autorizacionService;
        this.auditoriaRepository = auditoriaRepository;
    }

    public Incidente reportarIncidente(Usuario operador, Integer visitaId, String descripcion) {
        autorizacionService.verificarPermiso(operador, "registrar_incidente");

        Visita visita = null;
        if (visitaId != null) {
            Optional<Visita> visitaOpt = visitaRepository.buscarPorId(visitaId);
            visita = visitaOpt.orElse(null);
        }

        Incidente incidente = new Incidente(visita, operador, descripcion);
        incidenteRepository.guardar(incidente);

        auditoriaRepository.registrar(new RegistroAuditoria(operador.getId(), "REGISTRO_INCIDENTE", "incidentes",
                incidente.getId(), descripcion));
        return incidente;
    }

    public List<Incidente> listarIncidentes(Usuario operador) {
        autorizacionService.verificarPermiso(operador, "registrar_incidente");
        return incidenteRepository.listarTodos();
    }
}
